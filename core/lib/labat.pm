package labat;          # Lapiota Batch-Processing

use strict;
use cmt::perlsys;
use cmt::pp;
use cmt::util;
use Data::Dumper;
use Exporter;

our $opt_verbtitle      = 'labat';
our $opt_verbtime       = 0;
our $opt_verbose        = 1;

our @ISA    = qw(Exporter);
our @EXPORT = qw(labat_eval
                 labat_compile
                 );

sub info {
    return if $opt_verbose < 1;
    my $text = shift;
    print datetime.' ' if $opt_verbtime;
    print "[$opt_verbtitle] $text\n";
}

sub info2 {
    return if $opt_verbose < 2;
    my $text = shift;
    print datetime.' ' if $opt_verbtime;
    print "[$opt_verbtitle] $text\n";
}

sub hi {
    # pkg, file, line, sub, hasargs, wantarray, evaltext, is_req, hints, bits
    my @caller = caller(1);
    my $sub = $caller[3];
    $sub =~ s/^labat:://;
    my $ctx = shift;
    info $sub.': '.join(' -> ', @_);
}

sub labat_eval {
    my ($node, $ctx) = @_;
    if (ref $node eq 'ARRAY') {
        return undef unless @$node;
        if (ref $node->[0] eq 'CODE') {
            # XXX: eval-node in deep-child when necessary
            #      (eval-priority defined in fun-proto)
            my ($code, @args) = @$node;
            $code->($ctx, @args);
        } else {
            for (@$node) {
                labat_eval($_, $ctx);
            }
        }
    } else {
        # IGNORE
    }
}

sub _stdargs    { qsplit(qr/\s+/, join('', @_)) }

sub _resolvf    { my $ctx = shift; my ($funs, $vars) = @$ctx;
                  sub { my $name = shift;   # vars -> env -> other...
                        $vars->{$name}
                     || $ENV{$name} } }
sub _resolv     { my ($ctx, $s) = @_; my $f = _resolvf($ctx);
                  ppvarf(\&$f, $s) }
sub _resolv2    { my ($ctx, $s) = @_;
                  qsplit(qr/\s+/, _resolv($ctx, $s), undef, '\'"`') }

sub _behav_RAW  { shift }
sub _behav_STD  { my $code = shift;
                  sub { my $ctx = shift; @_ = _resolv2($ctx, join('', @_));
                        $code->($ctx, @_) } }

sub _die        { shift; die shift }
sub _echo       { shift; print join(' ', @_) }
sub _eval       { my $ctx = shift; my $perl = 'sub {'.join(' ', @_).'}';
                  my $code = eval $perl;
                  die "failed to eval: $@\n$perl\n" if $@;
                  $code->($ctx) }
sub _exit       { shift; exit shift }
sub _use        { shift; cmt::util::_use $_ for @_ }

sub _compact {
    if (wantarray) {
        @_>1 ? ([@_]) : @_==1 ? ($_[0]) : ()
    } else {
        @_>1 ? [@_] : @_==1 ? $_[0] : []
    }
}

sub _stdpp {
    my ($fun_code, $arg_node, $ctx) = @_;
    my @calls_with = _arg_unzip($arg_node);
    my @calls;
    for (@calls_with) {
        if (ref $_ eq 'ARRAY') {
            my @args = @$_;
                       # _stdargs @$_;
            push @calls, [ $fun_code, @args ];
        } else {
            my @args = ($_);
                       # qsplit(qr/\s+/, $_);
            push @calls, [ $fun_code, @args ];
        }
    }
    # info2 'unzipped '.Dumper($arg_node).' to '.Dumper(\@calls);
    @calls
}

sub _arg_unzip {
    my $node = shift;
    return $node unless (ref $node eq 'ARRAY');
    my @list;
    my @prefix;
    local $_;
    for (my $i = 0; $i < @$node; $i++) {
        $_ = $node->[$i];
        if (ref $_ eq 'ARRAY') {
            if (@prefix) {
                my @sublist = _arg_unzip($_);
                for (@sublist) {
                    push @list, [ @prefix, $_ ];
                }
                undef @prefix;
            } else {
                push @list, [ $_ ];
            }
        } elsif (ref $_) {
            if (@prefix) {
                push @list, [ @prefix, $_ ];
                undef @prefix;
            } else {
                push @list, $_;
            }
        } elsif (s/->$//) {
            push @prefix, $_;
        } elsif (not ref $node->[$i + 1] and $node->[$i + 1] =~ /^\|/) {
            push @prefix, $_;
            $node->[$i + 1] =~ s/^\|\s*//;
        } else {
            if (@prefix) {
                push @list, [ @prefix, $_ ];
                undef @prefix;
            } else {
                push @list, $_;
            }
        }
    }
    return @list;
}

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
                die "unexpected line-definition: $name || $rest";
            }
        }
    } while (0);
    @ev_list
}

sub _compile2 {
    my ($name, $val, $ctx) = @_;
    return () if $name eq ':';
    if ($name =~ /^:(\S+)$/) {
        my ($funs, $vars, $cat) = @$ctx;
        my $cat_name = $1;
        my $cat_desc = $val->{':'} if ref $val eq 'HASH';
        my $subcat = {};
        my @ev_list = _compact _compile($val, [ $funs, $vars, $subcat ]);
        $cat->{$cat_name} = [ $subcat, $cat_desc, @ev_list ];
        @ev_list
    } else {
        my $funs = $ctx->[0];
        my $pp;
        my $code;
        my $imm;
        if (defined $funs->{$name}) {
            my $fun = $funs->{$name};
            my ($pp, $code) = @$fun;
        } else {
            my $sysauto = $name;
            $sysauto =~ s/[[:punct:]]/_/g;
            if ($code = __PACKAGE__->can($sysauto)) {
                $name = $sysauto;
            } elsif ($code = __PACKAGE__->can('_'.$sysauto)) {
                $name = '_'.$sysauto;
                $imm = 1;
            } else {
                die "unknown function: $name (sysauto=$sysauto)";
            }
            $pp = \&_stdpp;
            my $pkg = which_sub($code);
            if (my $btab = eval '\%'.$pkg.'::BTAB') {
                if (my $behav = $btab->{$name}) {
                    my $b_impl = __PACKAGE__->can('_behav_'.$behav)
                              || $pkg->can($behav);
                    die "invalid behavior of function $name: $behav"
                        unless defined $b_impl;
                    $code = $b_impl->($code);
                }
            }
        }
        my @ev_list = $pp->($code, $val, $ctx);
        if ($imm) {
            labat_eval [ @ev_list ], $ctx;
            return ();
        }
        @ev_list
    }
}

sub labat_compile {
    my $root = shift;
    my $funs = {};      # function-proto    name => [\&eval, \&proto-parser]
    my $vars = {};      # variable-binding  name => $value
    my $cat  = {};      # category-tree     name => [\%subcat, $desc, @eval]
    my $ctx  = [ $funs, $vars, $cat ];
    my $ev   = _compact _compile2(':root', $root, $ctx);
    ($ev, $ctx)
}

1