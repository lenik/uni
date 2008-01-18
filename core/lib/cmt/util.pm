package cmt::util;

use strict;
use vars qw($LOGNAME $LOGLEVEL);
    $LOGNAME    = __PACKAGE__;
use cmt::lang('_or');
use cmt::log(2);
use cmt::path();
use cmt::proxy;
use Data::Dumper;
use Exporter;

our @ISA    = qw(Exporter);
our @EXPORT = qw(forx
                 qr_literal
                 qr_dos
                 qr_auto
                 safeslash
                 ssub
                 qeval_perl
                 _qeval
                 qeval
                 qsplit
                 append_cmdline
                 get_named_args
                 addopts_long
                 arraycmp
                 arrayeq
                 arrayne
                 array_index
                 array_remove
                 hasheq
                 hashne
                 hashindex
                 hash2tuples
                 bserchi
                 readfile
                 writefile
                 select_input
                 seleci
                 fire_sub
                 fire_method
                 at_exit
                 _use
                 listdir
                 fswalk
                 ieval
                 );

our $opt_strict         = 0;

# forx $pattern \&hit [\&miss] $text
sub forx($&;$$) {
    my $exp     = shift;
    my $hitf    = shift;
    my $missf;
    my $s       = $_[0];
    ($missf, $s) = @_ if ref $s eq 'CODE';

    my $off = 0;
    my $buf;

    untie $_ if my $tieback = tied $_;
    local $_;

    while ($s =~ /$exp/g) {
        my $hit = $&;
        #               |MISS |   HIT    |
        # ...(last match)<...>(this match)...
        #               |     |          |
        #              off   $-[0]     $+[0]
        $_ = substr($s, $off, $-[0] - $off);
        $off = $+[0];
        if ($_ ne '') {
            $missf->() if defined $missf;
            $buf .= $_;
        }
        $_ = $hit;
        $hitf->() if defined $hitf;
        $buf .= $_;
    }
    $_ = substr($s, $off);
    if ($_ ne '') {
        $missf->() if defined $missf;
        $buf .= $_;
    }

    tie $_, 'cmt::proxy', $tieback if defined $tieback;
    return $buf;
}

my %QRMODE = (
    'all'       => qr/[()\[\]{}?*+.\$\\^|]/,
    'or'        => qr/[()\[\]{}?*+.\$\\^]/,
);
sub qr_literal {
    local $_ = shift;
    my $mode = shift || 'all';
       $mode = $QRMODE{$mode} or die "Invalid mode $mode";
    s/$mode/\\$&/g;
    qr/$_/
}

sub qr_dos {
    local $_ = shift;
    s/\./\\./g;
    s/\?/./g;
    s/\*/.*?/g;  # s/\*\*/.*/g;
    s/[()\[\]{}+\$\\^|]/\\$&/g;
    qr/$_/
}

sub qr_auto {
    local $_ = shift;
    $_ = qr/$_/s if /(^|[^\\])\\[nr]/;
    $_ = qr/$_/s if /[\n\r]/s;
    $_
}

sub safeslash {
    local $_ = shift;
    s|\\\\|\\\\X|g;
    s|\\?/|\\/|g;
    s|\\\\X|\\\\|g;
    $_
}

sub ssub {
    local $_ = safeslash shift;
    my $dst = safeslash shift;
    my $mod = _or(shift, '');
    $mod .= 's' if (/(^|[^\\])\\[nr]/ or /[\n\r]/s);
    my $code = eval 'sub(\$) { $_[0] =~ s/'."$_/$dst/$mod }";
    die "can't compile substitute-regexp: $@" if $@;
    return $code;
}

# eval quoted-string by perl-language
sub qeval_perl {
    eval shift
}

# eval quoted-string into syntax elements
# _qeval $string [\&evaluator [$qchars]]
sub _qeval {
    my ($s, $evl, $qc) = @_;
    $evl ||= \&qeval_perl;
    $qc  ||= '"\'';
    $s   =~ s/~/~;/g;
    $s   =~ s/\\([\\$qc])/'~.'.ord($1).';'/eg;
    my $pat = join('|', map {"(?:$_(?:\\\\.|[^$_])*$_)"} split('', $qc) );
    my @mem;
    my $k = 0;
    $s =~ s/$pat/push @mem, $evl->($&); '~'.$k++.';'/eg;
    $_[0] = $s;
    @mem
}

# eval quoted-string with a specified variable evaluator
# qeval [$string=$_ [\&evaluator [$qchars]]]
sub qeval {
    local $_= _or(shift, $_);
    my @mem = _qeval($_, @_);
    s/~(\d+);/$mem[$1]/g;
    s/~\.(\d+);/chr($1)/eg;
    s/~;/~/g;
    $_
}

# qsplit [$sep=SPC [$string=$_ [$max [\&evaluator [$qchars]]]]]
sub qsplit {
    my $sep = _or(shift, qr/\s+/);
    local $_= @_ ? shift : $_;
    my $max = _or(shift, 0);
    my @mem = _qeval($_, @_);
    map {
            s/~(\d+);/$mem[$1]/g;
            s/~\.(\d+);/chr($1)/eg;
            s/~;/~/g;
            $_
        }
        split($sep, $_, $max)
}

sub append_cmdline {
    local $_ = shift;
    push @ARGV, qsplit;
}

sub get_named_args(\@;\%$) {
    my $arg = shift;
      @$arg = @$arg;    # de-alias
    my $cfg = shift || {};
    my $nolead = shift;
    my $namp = $nolead ? qr/^-(\w+)$/ : qr/^(-\w+)$/;
    my @passby;
    while (@$arg) {
        $_ = shift @$arg;
        if (ref($_) eq '' and /$namp/) {
            $cfg->{$1} = shift @$arg;
        } else {
            push @passby, $_;
        }
    }
    @$arg = @passby;
    return %$cfg;
}

sub addopts_long {
    my ($cfg, $vars) = @_;
    $vars = {} unless defined $vars;
    if (ref $cfg eq 'ARRAY') {
        my $n = @$cfg;
        for (my $i = 0; $i < $n; $i += 2) {
            my $optnam = $cfg->[$i];
            if ($optnam =~ /^\w+/) {
                my $varnam = $&;
                unless (ref $cfg->[$i + 1]) {
                    $vars->{$varnam} = undef unless exists $vars->{$varnam};
                    # insert the reference
                    splice @$cfg, $i + 1, 0, \$vars->{$varnam};
                    $n++;
                }
            } else {
                die "invalid option name: $optnam";
            }
        }
    } elsif (ref $cfg eq 'HASH') {
        for (keys %$cfg) {
            if (/^\w+/) {
                my $varnam = $&;
                unless (ref $cfg->{$_}) {
                    $vars->{$varnam} = undef unless exists $vars->{$varnam};
                    $cfg->{$_} = \$vars->{$varnam};
                }
            } else {
                die "invalid option name: $_";
            }
        }
    } else {
        die "invalid addopts-config type: ".(ref $cfg);
    }
    $vars;
}

sub _eq {
    my ($a, $b) = @_;
    defined $a ? (defined $b ? $a eq $b : undef)
               : (! defined $b)
}

sub arraycmp {
    my ($a, $b) = @_;
    my $c = $#$a - $#$b;
    return $c if $c != 0;
    for (0..$#$a) {
        $c = $a->[$_] cmp $b->[$_];
        return $c if $c != 0;
    }
    return 0;
}

sub arrayeq     { arraycmp(@_) == 0 }
sub arrayne     { arraycmp(@_) != 0 }

sub array_index(\@$;$) {
    my ($array, $v, $equalf) = @_;
    $equalf = \&_eq unless defined $equalf;
    for (0..$#$array) {
        return $_ if $equalf->($array->[$_], $v);
    }
    return -1;
}

sub array_remove(\@$;$) {
    my ($array, $v, $equalf) = @_;
    my $i = array_index(@$array, $v, $equalf);
    return undef if $i < 0;
    splice @$array, $i, 1;
}

sub hasheq {
    my ($a, $b) = @_;
    my $na = scalar(keys %$a);
    my $nb = scalar(keys %$b);
    return 0 if $na != $nb;
    for (keys %$a) {
        return 0 if $a->{$_} ne $b->{$_};
    }
    return 1;
}

sub hashne      { ! hasheq(@_) }

sub hash_index(\%$;$) {
    my ($hash, $v, $equalf) = @_;
    $equalf = \&_eq unless defined $equalf;
    for (keys %$hash) {
        return $_ if $equalf->($hash->{$_}, $v);
    }
    undef;
}

sub hash2tuples {
    my $hashref = shift;
    my @tuples;
    for my $k (keys %$hashref) {
        push @tuples, [$k => $hashref->{$k}];
    }
    return \@tuples;
}

# to avoid conflict with POSIX::bsearch
sub bserchi(&$@) {
    my $cmp = shift || sub { $a cmp $b };
    my $x = shift;
    my ($l, $r) = (0, scalar @_);
    while ($l < $r) {
        my $m = $l + int(($r - $l) / 2);
        my $t = $_[$m];
        if ($t < $x) {
            $l = $m + 1;
        } elsif ($x < $t) {
            $r = $m;
        } else {
            # $t == $x
            return $m;
        }
    }
    return $l;
}

sub readfile {
    my $path = shift;
    my @lines;
    undef $!;
    if ($path =~ /\.gz$/i) {
        eval('use Compress::Zlib; 1')
            or die("Can't load Compress::Zlib: $@");
        open(FH, "<$path")
            or die("Can't open file $path for read");
        my $h = gzopen(\*FH, "rt")
            or die("Can't open gzip-file $path for read");
        while ($h->gzreadline($_)) {
            push @lines, $_;
        }
        # return 'string' when success, otherwise an error-code
        my $err = eval('$gzerrno');
        if ($err != eval('Z_STREAM_END')) {
            $! = "Error deflating from $path: $err\n";
        }
        $h->gzclose();
        close FH;
    } else {
        open(FH, "<$path")
            or die("Can't open file $path for read");
        binmode FH;
        @lines = <FH>;
        close FH;
    }
    return wantarray ? @lines : join('', @lines);
}

sub writefile {
    my $path = shift;
    open(FH, ">$path")
        or die("Can't open file $path to write");
    binmode FH;
    print FH for @_;
    close FH;
}

sub select_input {
    my $oldin;
    my $newin = shift;
    open($oldin, '<&STDIN')
        or die "can't dup (the original) STDIN: $!";
    open(STDIN, '<&', $newin)
        or die "can't redirect STDIN to a new one: $!";
    $oldin
}

sub seleci {
    my $oldin = select_input @_;
    sub {
        open(STDIN, '<&', $oldin)
            or die "can't restore STDIN to the dup-backuped one: $!";
    }
}

sub fire_sub {
    my $obj = shift;
    my $name = shift;
    if (defined (my $callback = $obj->{$name})) {
        if (ref $callback eq 'CODE') {
            return $callback->(@_);
        } elsif (ref $callback eq '') {     # string
            return eval($callback);
        } else {
            die "$obj->\{$name\}: invalid callback value: $callback";
        }
    } else {
        if ($opt_strict) {
            die "callback $name isn't existed in $obj.";
        }
        return undef;
    }
}

sub fire_method {
    my $obj = shift;
    my $name = shift;
    if ($obj->can($name)) {
        return $obj->$name(@_);
    }
    return fire_sub($obj, $name, $obj, @_);
}

my  @ATEXIT;
sub at_exit(&) {
    my $dstr = shift;
    push @ATEXIT, [$dstr, \@_];
}

sub _use {
    my $mod = shift;
    my $par = '';
       $par = 'qw('.join(' ', @_).')' if @_;
    my $val = eval("package ".caller()."; use $mod$par; 1")
        or die "failed to load $mod: $@";
}

# XXX - thread-unsafe ("free unreferenced scalars" in multi-threads.)
END {
    $_->[0]->(@{$_->[1]}) for @ATEXIT;
}

# listdir start-dir, incl, excl, filter, sort
sub listdir {
    my $dir = _or(shift, $_, '.');
    my ($incl, $excl, $filter, $sort) = @_;
    opendir(DIR, $dir) || die "can't opendir $dir: $!";
    my @files = readdir(DIR);
    @files = grep { /$incl/ } @files if defined $incl;
    @files = grep {!/$excl/ } @files if defined $excl;
    @files = grep { $filter->($dir, $_) } @files if defined $filter;
    closedir DIR;
    @files = sort { $sort->($a, $b) } @files if defined $sort;
    return @files;
}

# incremental-eval
sub ieval($&;$$) {
    my ($stage, $cb, $timeout, $loader, $dumper) = @_;
    unless (defined $loader) {
        require YAML;
        $loader = \&YAML::Load;
        $dumper = \&YAML::Dump;
    }
    my ($base, $lev) = $stage =~ /^(?:(\w+)\.)?([^.]+)$/
        or die "invalid stage name: $stage";
    $base = (defined $base ? $base : $LOGNAME).'.st';
    my $stagefile = $base.$lev;
    if ($lev =~ /^\d+$/) {
        my $prev = $lev - 1;
        if (-f $base.$prev and -M $base.$prev < -M $stagefile) {
            unlink $stagefile;
        }
    }
    if (defined $timeout and 86400 * -M $stagefile > $timeout) {
        unlink $stagefile;
    }
    if (-f $stagefile) {
        my $file = readfile $stagefile;
        my $root = $loader->($file);
        wantarray ? @$root : $root
    } else {
        if (wantarray) {
            my @rebuilt = $cb->();
            writefile $stagefile, $dumper->(\@rebuilt)."\n";
            @rebuilt
        } else {
            my $rebuilt = $cb->();
            writefile $stagefile, $dumper->($rebuilt)."\n";
            $rebuilt
        }
    }
}

1