package labat;

=head1 NAME

labat - Lapiota Batch-Processing

=head1 SYNOPSIS

use labat;
use YAML;

=cut

use strict;
use vars qw($LOGNAME $LOGLEVEL);
use cmt::lang('_o');
use cmt::log(3);
    $LOGNAME    = __PACKAGE__;
    $LOGLEVEL   = 1;
use cmt::path('path_join', 'path_split');
use cmt::perlsys('which_sub');
use cmt::pp('ppvarf');
use cmt::util('qsplit', 'readfile');
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id$');
    our $VER    = "0.$RCSID{rev}";
use vars('%ALIAS');
use Data::Dumper;
use Exporter;

our @ISA    = qw(Exporter);
our @EXPORT = qw(labat_eval
                 labat_compile
                 load_syaml
                 );

sub labat_compile;
sub _compact;
sub _compile;
sub _compile2;

=head1 EVALUATE
    my $funs = {};      # function-proto    name => [\&eval, \&proto-parser]
    my $vars = {};      # variable-binding  name => $value
    my $cat  = {};      # category-tree     name => [\%subcat, $desc, @eval]
    my $ctx = [ $funs, $vars, $cat ];

    my ($ev, $ctx) =
        labat_compile Load(<.labat-file>);
    labat_eval($ev, $ctx);

evaluate the compiled labat node

=cut
sub labat_eval {
    my ($node, $ctx) = @_;
    if (ref $node eq 'ARRAY') {
        return undef unless @$node;
        if (ref $node->[0] eq 'CODE') {
            # XXX: eval-node in deep-child when necessary
            #      (eval-priority defined in fun-proto)
            my ($code, @args) = @$node;
            $code->($ctx, @args)
        } else {
            for (@$node) {
                labat_eval($_, $ctx);
            }
        }
    } else {
        # IGNORE
    }
}

=head1 COMPILE

compile .labat.yaml to internal representation

=cut
sub labat_compile {
    my ($root, $ctx) = @_;
    my $ev   = _compact _compile2(':root', $root, $ctx);
    ($ev, $ctx)
}

=head2 Simplified Yaml

This function replaces the YAML::Load, to allow you using a simpler
syntax for:

=over

=item Hashref(s) in the list

    - text
        name: value
        ...
    - more
Replaces:
    - text
    -
        name: value
        ...
    - more

=item Backtick at the beginning

Since YAML doesn't allow backtick(`) appears at the beginning, you should
quote the whole sentence in the quote(" or '):

    - '`a sentence with backtick(`) at the beginning.'

With load_syaml, you can just type:

    - `a sentence with backtick(`) at the beginning.

=item Story without the final newline

YAML require the .yaml file ends with a new line, this is really... so let's
just add the newline at the end, and then call YAML::Load.

=item Multiple documents turns into a list

We don't need the "multiple documents" feature, so if a .yaml file occasionaly
contains multiple documents, just turns into an array-ref.

    ---
        - file 1
    ---
        - file 2
Becomes to:
    ---
        -
            - file 1
            - file 2

And, if the last file is empty, it will be removed. So

    ---
        - the only file
    ---
Becomes to:
    ---
        - the only file

=back

=cut
sub load_syaml {
    @_ = split /\n/, join('', @_);
    my ($ll) = $_[0] =~ /^(\s*)/; # last-lead
    my @t;
    local $_;
    for (my $i = 0; $i < @_; $i++) {
        $_ = $_[$i];
        my ($l, $c) = /^(\s*)(.?)/;
        if (length($l) > length($ll)) {
            if ($c eq '-') {
                if ($_[$i-1] =~ /^\s*-\s*(\S.*)$/) {
                    push @t, "$ll-" if $1 !~ /:\s*$/;
                }
            }
        }
        if (/^(\s*)-(\s+)(?=`)/) {
            my ($lead, $pre, $text) = ($1, $2, $');
            $text =~ s/[\\']/\\$1/g;
            chop $text; # \n
            $_ = "$lead-$pre'$text'\n";
        }
        $ll = $l;
        push @t, $_;
    }
    push @t, "";
    my $yaml = join("\n", @t);
    _log3 "load yaml: \"$yaml\"" if $LOGLEVEL >= 3;
    my @docs = YAML::Load($yaml);
    pop @docs until defined $docs[-1] or !@docs;
    @docs > 1 ? [ @docs ] : $docs[0]
}

=head2 Internal functions

=cut
sub _resolvf    {
    my $ctx = shift;
    my ($funs, $vars) = @$ctx;
    sub {
        local $_ = shift;
        return $vars->{$_} if exists $vars->{$_};
        return $ENV{$_} if exists $ENV{$_};
        if (/^`(.*)`$/) {
            return eval $1;
        }
        if (/\s/) {
            my ($call, @args) = qsplit(qr/\s+/, _resolv($ctx, $_));
            my $code = __PACKAGE__->can($call);
            return $code->($ctx, @args) if ref $code;
        }
        undef
    }
}
sub _resolv     { my ($ctx, $s) = @_; my $f = _resolvf($ctx);
                  ppvarf(\&$f, $s) }
sub _resolv2    { my ($ctx, $s) = @_; $s = _resolv($ctx, $s);
                  qsplit(qr/\s+/, $s, undef, undef, '\'"`') }

sub _compact {
    if (wantarray) {
        @_>1 ? ([@_]) : @_==1 ? ($_[0]) : ()
    } else {
        @_>1 ? [@_] : @_==1 ? $_[0] : []
    }
}

=head2 Zipped Calls

=over

=item * Standard Function

Using _stdpp() preprocessor, this includes:
    - support of zipped-arguments

=cut
sub _calls_unzip {
    my ($fun_code, $arg_node) = @_;
    my @calls_with = _args_unzip($arg_node);
    my @calls;
    for (@calls_with) {
        if (ref $_ eq 'ARRAY') {
            my @args = @$_; # _stdargs @$_;
            push @calls, [ $fun_code, @args ];
        } else {
            my @args = ($_); # qsplit(qr/\s+/, $_);
            push @calls, [ $fun_code, @args ];
        }
    }
    # _log2 'unzipped '.Dumper($arg_node).' to '.Dumper(\@calls);
    @calls
}

=item * Format of zipped arguments:

=over

=item * PREFIX

    - prefix ->
    -
        - rest-of-arguments (1)
        - ...

=item * CONTINUATION

    - prefix
    -   | continuation (1)      WARNING: The space betwen `- |' have at
    -   | ...                            least 2 space, otherwise may cause
    -   | rest-of-arguments              YAML Parser fails.

=back

More about standard functions...

=back

=cut
sub _args_unzip {
    my $node = shift;
    return $node unless (ref $node eq 'ARRAY');
    my @list;
    my $prefix;
    local $_;
    for (my $i = 0; $i < @$node; $i++) {
        $_ = $node->[$i];
        my $folw = $node->[$i + 1];
        if (ref $_ eq 'ARRAY') {
            if (defined $prefix) {          # p x [A]       => (p x A(i), ...)
                my @sublist = _args_unzip($_);
                if (ref $prefix) {
                    for (@sublist) {
                        if (ref $_ eq 'ARRAY') {
                            push @list, [ @$prefix, @$_ ];
                        } else {
                            push @list, [ @$prefix, $_ ];
                        }
                    }
                } else {
                    for (@sublist) {
                        if (ref $_ eq 'ARRAY') {
                            push @list, [ $prefix.$_->[0], @$_[1..$#$_] ];
                        } else {
                            push @list, $prefix.$_;
                        }
                    }
                }
                undef $prefix;
            } else {
                push @list, [ $_ ];
            }
        } elsif (ref $_) {                  # p x Object    => [ p, Object ]
            if (defined $prefix) {
                push @list, [ $prefix, $_ ];
                undef $prefix;
            } else {
                push @list, $_;
            }
        } elsif (s/->$//) {                 # p x p'        => p.=p', next
            if (ref $prefix) {
                $prefix->[-1] .= $_;
            } else {
                $prefix .= $_;
            }
        } elsif (defined $folw and $folw =~ /^\|/) {
                                            # p x ? x "|.." => p=[p,?], next
            if (ref $prefix) {
                push @$prefix, $_;
            } else {
                $prefix = [ _o($prefix) . $_ ];
            }
            $node->[$i + 1] =~ s/^\|\s*//;
        } else {                            # p x str       => p.str
            if (ref $prefix) {
                push @list, [ @$prefix, $_ ];
                undef $prefix;
            } elsif (defined $prefix) {
                push @list, $prefix.$_;
                undef $prefix;
            } else {
                push @list, $_;
            }
        }
    }
    return @list;
}

=head1 FILE FORMAT

=head2 Aggregates

=over

=item * PARALLEL EXECUTION

    function: arguments
    ...

=item * SEQUENCE EXECUTION

    - function: arguments
    - ...

=item * COMPILE-TIME EXECUTION

    - !perl/code: perl-script...

=item * LINE-DEFINITION

=cut
sub _compile {
    my ($val, $ctx) = @_;
    my @ev_list;
    do {
        if (ref $val eq 'HASH') {
            for (keys %$val) {
                push @ev_list, _compact _compile2($_, $val->{$_}, $ctx);
            }
        } elsif (ref $val eq 'ARRAY') {
            for (@$val) {
                push @ev_list, _compact _compile($_, $ctx);
            }
        } elsif (ref $val eq 'CODE') {
            $val = $val->($ctx);
            next if defined $val;
        } elsif (ref $val) {
            # this may happen if `!perl/xxx:' is used in YAML.
            die "invalid node-type: $val (type=".(ref $val).")";
        } else {
            my ($name, $rest) = split(/\s+/, $val, 2);
            if ($name =~ s/:$//) {
                push @ev_list, _compact _compile2($name, $rest, $ctx);
            } else {
                die "invalid line-definition: `$val'";
            }
        }
    } while (0);
    @ev_list
}

=item * CATEGORY

    :CATEGORY:
        [:: comment]
        ...

=back

=head2 Call Function

    FUNCTION: arguments
    FUNCTION:
        - indexed-arguments
        - ...
    FUNCTION:
        named: arguments
        ...

=head2 Function Naming

    system-function: functions imported to 'labat' package
        funcname: arguments     (delayed evaluation)
        _funcname: arguments    (immediate evaluation)
    user-function:
        (not implemented.)

=cut
sub _compile2 {
    my ($name, $val, $ctx) = @_;
    return () if $name eq ':';
    if ($name =~ /^:(\S+)$/) {
        my $cat_name = $1;
        my $cat_desc = $val->{':'} if ref $val eq 'HASH';
        my $cstack = $ctx->[2];
        push @$cstack, {};
        my @ev_list = _compact _compile($val, $ctx);
        my $subcat = pop @$cstack;
        $cstack->[-1]->{$cat_name} = [ $subcat, $cat_desc, @ev_list ];
        @ev_list
    } else {
        my $funs = $ctx->[0];
        my $cname = $name;      # canonical
           $cname =~ s/[[:punct:]]/_/g;
        my ($code, $cs);
        my $rtl = 0;
        while ($rtl < 2) {
            if (defined $funs->{$cname}) {
                my $fun = $funs->{$cname};
                ($code, $cs) = @$fun;
                last
            } else {
                $code = __PACKAGE__->can($cname);
                unless (defined $code) {
                    $code = $ALIAS{$cname};
                }
                if (defined $code) {
                    my $pkg = which_sub($code);
                    if (my $ctab = eval '\%'.$pkg.'::CTAB') {
                        $cs = $ctab->{$cname};
                        if (defined $cs and !ref $cs) {
                            my $cs2 = $pkg->can('_cs_'.$cs)
                                    || __PACKAGE__->can('_cs_'.$cs);
                            die "invalid call style of function $name: $cs"
                                unless ref $cs2;
                            $cs = $cs2;
                        }
                    }
                    last
                }
            }
            last unless $cname =~ s/^_//;
            $rtl++;
        }

        die "unknown function: $name ($cname)" unless defined $code;
        $code = $cs->($code) if defined $cs;

        my @ev_list = _calls_unzip($code, $val);
        if ($rtl > 0) {
            labat_eval [ @ev_list ], $ctx;
            return ();
        }
        @ev_list
    }
}

=head1 WRITING EXTENSION

=over

=item * hi

print what-to-do (-q to suppress)
output: `caller: arg1 -> arg2 -> ...'

=cut
sub hi {
    # pkg, file, line, sub, hasargs, wantarray, evaltext, is_req, hints, bits
    my @caller = caller(1);
    my $sub = $caller[3];
    $sub =~ s/^labat:://;
    no warnings 'uninitialized';
    _log1 $sub.': '.join(' -> ', @_[1..$#_]);
}

=back

=head1 Built-in Functions

=cut
our %CTAB   = qw(defun      RAW
                 define     RAW
                 include    STD
                 );

=head2 Kernel Call Styles

=cut
sub _cs_RAW     { shift }
sub _cs_VLST    { my $code = shift;
                  sub { my $ctx = shift; @_ = _resolv2($ctx, join('', @_));
                        $code->($ctx, @_) } }
sub _cs_STD     { &_cs_VLST }

=head2 Kernel Functions

=cut
sub k_die       { shift; die shift }
sub k_echo      { shift; print join(' ', @_) }
sub k_eval      { my $ctx = shift; my $perl = 'sub {'.join(' ', @_).'}';
                  my $code = eval $perl;
                  die "failed to eval: $@\n$perl\n" if $@;
                  $code->($ctx) }
sub k_exit      { shift; exit shift }
sub k_use       { shift; cmt::util::_use $_ for @_ }

sub defun { &hi;
    my ($ctx, $fmap) = @_;
    unless (ref $fmap eq 'HASH') {
        die "defun(FUNCTION-MAP)";
    }
    my ($funs, $vars) = @$ctx;
    for (keys %$fmap) {
        my ($name, $cs) = split(':', $_, 2);
        if ($cs ne '') {
            my $cs2 = __PACKAGE__->can('_cs_'.$cs);
            die "invalid call style of function $name: $cs"
                unless ref $cs2;
            $cs = $cs2;
        } else {
            $cs = undef;
        }
        my $body = $fmap->{$_};
        my $perl = 'sub {&hi;'.$body.'}';
        my $code = eval $perl;
        die "failed to compile fun $name: $@\n$perl\n" if $@;
        $funs->{$name} = [ $code, $cs ];
    }
}

sub define { &hi;
    my ($ctx, $vmap) = @_;
    unless (ref $vmap eq 'HASH') {
        die "define(VARIABLE-MAP)";
    }
    my ($funs, $vars) = @$ctx;
    for my $name (keys %$vmap) {
        my $value = $vmap->{$name};
        $value = _resolv($ctx, $value);
        $vars->{$name} = $value;
    }
}

sub include { &hi;
    my ($ctx, $path) = @_;
    my $fs = $ctx->[3];
    my $fpath = $fs->[-1];
    my ($fdir, $fbase) = path_split($fpath);
    my $loadpath = path_join($fdir, $path);
    die "can't find file $loadpath (include: $path)"
        unless -f $loadpath;
    _log2 "parse $loadpath";
    my $cnt = readfile($loadpath);
    my $root = load_syaml($cnt);
    push @$fs, $loadpath;
    my ($ev, $ctx2) = labat_compile($root, $ctx);
    pop @$fs;
    labat_eval($ev, $ctx2);
}

=head2 Function Alias (Global)

=cut
our %ALIAS  = (
    'die'   => \&k_die,
    'echo'  => \&k_echo,
    'eval'  => \&k_eval,
    'exit'  => \&k_exit,
    'use'   => \&k_use,
    );
sub set_alias { $ALIAS{$_[0]} = $ALIAS{$_[1]} }

=head1 SEE ALSO

L<labat::win32> - Win32 functions

=head1 AUTHORS

Xima Lenik <F<lenik@bodz.net>>

=cut

1