package cmt::util;

use strict;
use vars qw($LOGNAME $LOGLEVEL);
    $LOGNAME    = __PACKAGE__;
use cmt::lang;
use cmt::log(2);
use cmt::path;
use cmt::proxy;
use Cwd;
use Data::Dumper;
use Exporter;

our @ISA    = qw(Exporter);
our @EXPORT = qw(qr_literal
                 forx
                 php_perl
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
                 indent
                 unindent_
                 unindent
                 abbrev
                 fire_sub
                 fire_method
                 at_exit
                 _use
                 listdir
                 fswalk
                 ieval
                 );

our $opt_strict         = 0;

my %QRMODE = (
    'c'         => qr/[()\[\]{}?*+.|^\$\\]/,
    'o'         => qr/[()\[\]{}?*+.^\$\\]/,
);
sub qr_literal {
    my $text    = shift;
    my $mode    = shift || 'c';
       $mode    = $QRMODE{$mode} or die "Invalid mode $mode";
    $text       =~ s/$mode/\\$&/g;
    return qr/$text/;
}

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

sub php_perl {
    local $_ = join('', @_);
    s/<\? ( ( (\\.) | ([^\?]) | (\?[^>]) )* ) \?>/eval($1)/sgex;
    return $_;
}

sub qeval_perl {
    eval shift
}

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

# qeval [$string=$_ [\&evaluator [$qchars]]]
sub qeval {
    local $_= _or(shift, $_);
    my @mem = _qeval($_, @_);
    s/~(\d+);/$mem[$1]/g;
    s/~\.(\d+);/chr($1)/eg;
    s/~;/~/g;
    $_
}

# qsplit [$sep=SPC [$string=$_ [\&evaluator [$qchars]]]]
sub qsplit {
    my $sep = _or(shift, qr/\s+/);
    local $_= @_ ? shift : $_;
    my @mem = _qeval($_, @_);
    map {
            s/~(\d+);/$mem[$1]/g;
            s/~\.(\d+);/chr($1)/eg;
            s/~;/~/g;
            $_
        }
        split($sep, $_)
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

sub _eq { $_[0] eq $_[1] }

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

sub indent {
    my $prefix = shift;
        $prefix = ' 'x$prefix if $prefix =~ /^\d+$/;
    my @lines = split(/\n/, shift);
    join("\n", map { $prefix.$_ } @lines);
}

sub unindent_ {
    my $len     = shift;
    my @lines   = scalar @_ > 1 ? @_ : split(/\n/, shift);
    if ($len <= 0) {
        my ($s) = ($lines[0] =~ /^(\s*)/);
        $len    = length $s;
    }
    my $pattern = qr/^\s{1,$len}/;
    join("\n", map { s/$pattern//; $_ } @lines);
}

sub unindent {
    unindent_ 0, @_
}

sub abbrev {
    my $maxlen = shift;
    my $text = join(@_);
    $text =~ s/\n/ /g;
    if (length $text > $maxlen) {
        substr($text, $maxlen - 5) = '...';
    }
    return $text;
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
    my $par;
       $par = 'qw('.join(' ', @_).')' if defined $par;
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

sub fswalk(&;@) {
        no warnings('uninitialized');
    my $cb      = shift;
    my %cfg     = get_named_args @_;
    my $start   = _or($cfg{-start}, '.');
    my $filter  = $cfg{-filter};
    my $hidden  = $cfg{-hidden};
    my $depth   = _or($cfg{-depth}, 999);
    my $bfirst  = index('bw', $cfg{-order}); # breadth-first
    my $leave   = $cfg{-leave};
    my $sort    = $cfg{-sort};
    my $excl    = not $cfg{-inclusive};     # include the start file
    my $iter;
       $iter    = sub { *__ANON__ = '<iter>';
        my $start = shift;
        my $dir   = $start;
        my $level = shift;
        my @files;
        if (-d $start) {
            $dir = $start;
            @files = listdir($dir, undef, qr/^\.\.?$/,
                             $hidden ? sub { ishidden(path_join @_) } : undef,
                             $sort);
        } else {
            my $fpat;
            ($dir, $fpat) = path_split($start);
            $dir = '.' unless defined $dir;
            my $cwd = cwd();
            chdir($dir) or die "can't chdir to $dir: $!";
            @files = grep { -e "$dir/$_" } glob $fpat;
            chdir($cwd) or die "can't chdir to $cwd: $!";
        }
        @files = grep { $filter->() } @files if defined $filter;
        my $ret;
        my $count = 0;
        my @dirs;
        for (@files) {
            my $path = path_join($dir, $_);
            if (-d $path) {
                next if $level >= $depth;
                if ($bfirst) {
                    push @dirs, $path;
                } else {
                    # BREADTH-FIRST-COPY-BEGIN
                    $ret = $cb->($path);
                    return -1 if $ret == -1;    # break
                    next      if $ret == 0;     # ignore
                    $count += $ret - 1;
                    $ret = $iter->($path, $level + 1);
                    return -1 if $ret == -1;    # break
                    $count += $ret;
                    $cb->($dir, 1) if $leave;
                    # BREADTH-FIRST-COPY-END
                }
            } else {
                $ret = $cb->($path);
                return -1 if $ret == -1;        # break
                $count += $ret;
            }
        }
        for (@dirs) {
            # BREADTH-FIRST-COPY-BEGIN
            $ret = $cb->($_);
            return -1 if $ret == -1;    # break
            next      if $ret == 0;     # ignore
            $count += $ret - 1;
            $ret = $iter->($_, $level + 1);
            return -1 if $ret == -1;    # break
            $count += $ret;
            $cb->($dir, 1) if $leave;
            # BREADTH-FIRST-COPY-END
        }
        $count
    };
    if ($excl) {
        $iter->($start, 0)
    } else { # include the start file
        my $count = $cb->($start);
        return $count if $count <= 0;       # break or ignore
        my $ret = $iter->($start, 0);
        return -1 if $ret == -1;            # break
        $cb->($start, 1) if $leave;
        $count = $count - 1 + $ret
    }
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