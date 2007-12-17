package cmt::fileproc;

=head1 NAME

cmt::fileproc - Batch file process framework

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
    $LOGNAME    = __PACKAGE__;
    $LOGLEVEL   = 1;
use cmt::fswalk('fswalk');
use cmt::log(2, '_sigx');
use cmt::util();
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id: .pm 764 2007-12-04 14:20:23Z Lenik $');
    our $VER    = "0.$RCSID{rev}";
use Exporter;
use File::Copy;
use File::Temp('tempfile');
use Text::Diff;

our @ISA    = qw(Exporter);
our @EXPORT = qw(batch_main
                 cmp_f
                 print_diff
                 );
our @EXPORT_OK = qw(
    %COMOPT $COMOPT $WALKOPT
    $opt_force %opt_fswalk $opt_filter $opt_ascii $opt_binary $opt_stdout
    $opt_backup $opt_ignore_case $opt_diff $opt_diff_ext $opt_diff_style
    $opt_dry_run);

# INITIALIZORS
{
    package simdiff;
    our @ISA = ('Text::Diff::Base');
    sub hunk {
        shift;
        my $buf;
        for (@{$_[2]}) {    # ops
            my $op = $_->[Text::Diff::OPCODE];
            my $fl = $_->[Text::Diff::FLAG];
            $op = $fl if defined $fl;
            next if $op eq ' ';
            my $sel = $op eq '+' ? 1 : 0;
            my $line = $_->[$sel];
            $buf .= sprintf "%7d$op%s", $line, $_[$sel][$line]
        }
        $buf
    }
}

our $opt_force;
our %opt_fswalk         = (-depth => 0);
our $opt_filter;
our $opt_ascii          = 1;
our $opt_binary;
our $opt_stdout;
our $opt_backup;
our $opt_ignore_case;
our $opt_diff;
our $opt_diff_ext;
our $opt_diff_style;
our $opt_dry_run;

our %COMOPT = ( # bcdfikrtw
               'backup|k:s',    => sub { shift; $opt_backup = shift || 'bak' },
               'binary-only|b'  => sub { $opt_binary = 1; $opt_ascii = shift eq 'b' },
               'both'           => sub { $opt_binary = 1; $opt_ascii = 1 },
               'diff|d:s'       => sub {
                    shift; $opt_diff = 1; $opt_diff_ext = shift;
                    unless (defined $opt_diff_style) {
                        $opt_diff_style = $opt_diff_ext ? 'Unified'
                            : ($LOGLEVEL > 2 ? 'Unified' : 'simdiff');
                    } },
               'diff-style|ds=s'=> \$opt_diff_style,
               'dry-run'        => \$opt_dry_run,
               'filter|t=s'     => \$opt_filter,
               'force|f'        => \$opt_force,
               'ignore-case|i'  => \$opt_ignore_case,
               'recursive|r:n'  => sub { shift; $opt_fswalk{-depth} = shift || 100 },
               'stdout|c'       => \$opt_stdout,
               'walkopt|w=n'    => \%opt_fswalk,
               );
our $COMOPT = <<'EOM';
    -B, --binary-only       only binary files are processed
    -b, --both              both text files and binary files are processed
    -c, --stdout            send the replaced file to stdout, don't change file
    -d, --diff[=EXT]        show diff to change, or dump diff to files.EXT
        --diff-style=STYLE  set diff style: Unified(default), Table, Context
    -t, --filter=REGEXP     => --walkopt=-filter=REGEXP
    -f, --force             force change read-only files
    -i, --ignore-case       ignore case
    -r, --recursive[=DEPTH] => --walkopt=-depth=DEPTH
    -w, --walkopt=OPTION    extra options for directory iterator (see follow)
    -k, --backup[=EXT]      backup the original files (default EXT=bak)
EOM

our $WALKOPT = <<'EOM';
Walker Options:
        -depth=NUMBER       how deep recursive into the directory
        -filter=REGEXP      only files with basename matches (perl-)REGEXP
        -hidden             whether hidden files are iterated
                            (both .* files and files with hidden attribute)
        -order=[bd]         breadth-first scanning if specified -order=b
EOM

=head1 SYNOPSIS

    use cmt::fileproc;
    mysub(arguments...)

=head1 Batch file process framework

B<cmt::fileproc> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-cmt::fileproc-RESOLVES.

=head1 FUNCTIONS

=cut
=head2 batch_main(CALLBACK, FILES)

    CALLBACK(FILE, TEMP-FILE-HANDLE, TEMP-FILE-PATH, PARAMETERS)

CALLBACK may be CODEREF or [CODEREF, PARAMETERS].

return value:
    undef   compare FILE with TEMP-FILE to determine write or not
    1       explicit write back TEMP-FILE to PATH

=cut
sub batch_main {
    my $iterator = shift;
    my @param;
    ($iterator, @param) = @$iterator if ref $iterator eq 'ARRAY';

    unless (defined $opt_fswalk{-filter}) {
        $opt_fswalk{-filter} = qr/$opt_filter/ if defined $opt_filter;
    }

    if ($LOGLEVEL >= 2) {
        _log2 "fswalk$_: $opt_fswalk{$_}" for keys %opt_fswalk;
        _log2 "filetype:    ".($opt_ascii?'ascii':'').' '.($opt_binary?'binary':'');
        _log2 "ignore-case" if $opt_ignore_case;
        _log2 "diff:        $opt_diff_ext/$opt_diff_style" if $opt_diff;
        _log2 "dry-run"     if $opt_dry_run;
    }

    my ($tmph, $tmpf);
    #if ($opt_mode eq 'exec') {
    #    $tmpf = tmpnam();
    #} else {
        # using handle/path pair for atomic purpose.
        ($tmph, $tmpf) = tempfile(CLEANUP => 1, SUFFIX => '.tmp');
        close $tmph;    # ... is there any better style?
    #}
    my $n_all  = 0;
    my $n_diff = 0;
    my $n_save = 0;

    for (@_) {
        fswalk { local *__ANON__ = '<fileproc-iterator>';
            my $path = shift;
            if (-d $path) {
                _sig1 'dir', $path;
                return 1;
            }

            my $T = -T $path;
            return 0 if !$opt_ascii and $T;
            return 0 if !$opt_binary and !$T;

            $n_all++;
            _sig1 'file', $LOGLEVEL > 1 ? $path."\n" : $path;

            open($tmph, '+>', $tmpf)
                or die "can't open temp file $tmpf: $!";
            my $dirty = $iterator->($path, $tmph, $tmpf, @param);
            close $tmph;

            $dirty = cmp_f($tmpf, $path) unless defined $dirty;
            if ($dirty) {
                $n_diff++;
                print_diff($path, $tmpf) if $opt_diff;
                _sig1 'save', $path."\n";
                eval { copy_ex($tmpf, $path) };
                if ($@ ne '') {
                    _log1 "failed to save $path: $@";
                } else {
                    $n_save++;
                }
            }
        } -start => $_, %opt_fswalk;
    }
    _sig1 'done', "total $n_diff/$n_all files changed, $n_save files saved.";
    unlink $tmpf;
}

=head1 DIAGNOSTICS

(No Information)

=cut
# (HELPER FUNCTIONS)
sub cmp_f {
    my ($a, $b) = @_;
    return  0 if !-f $a and !-f $b;
    return -1 if !-f $a;
    return  1 if !-f $b;
    open(A, '<', $a) or die "can't open $a: $!";
    open(B, '<', $b) or die "can't open $b: $!";
    # the file may be deleted, and if this happens, do replace.
    my ($x, $y);
    my $ret;
    while (1) {
        $x = <A>;
        $y = <B>;
        if (defined $x) {
            if (defined $y) {
                $ret = $x cmp $y;
                next if $ret == 0;
            }
            $ret = 1;
        } else {
            $ret = defined $y ? -1 : 0;
        }
        last
    }
    close A;
    close B;
    return $ret;
}

sub move_ex {
    my ($src, $dst) = @_;
    if ($opt_dry_run) {
        _log1 "move $src to $dst";
        return 1;
    } else {
        move $src, $dst;
    }
}

sub writefile_ex {
    my ($file, $data) = @_;
    if ($opt_stdout) {
        _log1 "write to $file";
        _P2 $data;
    } elsif ($opt_dry_run) {
        _log1 "write to $file";
    } else {
        writefile $file, $data;
    }
}

sub copy_ex {
    my ($src, $dst) = @_;
    die "file $src isn't existed" if !-f $src;
    if ($opt_stdout) {
        open(SRC, '<', $src) or die "can't open $src to read: $!";
        print while <SRC>;
        close SRC;
    } else {
        my $oldmode = undef;
        unless (-w $dst) {
            _sigx "file $dst isn't writable" unless $opt_force;
            (undef, undef, $oldmode) = stat $dst;
            chmod(0777, $dst) or die "failed to chmod on $dst: $!";
        }
        if (-f $dst and $opt_backup) {
            my $bak = $dst.'.'.$opt_backup;
            _sig1 'bak', $bak;
            move_ex($dst, $bak) or die "can't backup file $dst: $!";
        }
        open(SRC, '<', $src) or die "can't open $src to read: $!";
        open(DST, '>', $dst) or die "can't open $dst to write: $!";
        print DST while <SRC>;
        close DST;
        chmod $oldmode, $dst if defined $oldmode;
    }
}

sub print_diff {
    my ($a, $b) = @_;
    die "file $a isn't existed" if !-f $a;
    die "file $b isn't existed" if !-f $b;
    open(A, '<', $a) or die "can't open $a: $!";
    open(B, '<', $b) or die "can't open $b: $!";
    my $diff = diff \*A, \*B, { STYLE => $opt_diff_style };
    if ($opt_diff_ext) {
        writefile_ex "$a.$opt_diff_ext", $diff;
    } else {
        print STDERR "\n", $diff;
    }
    close A;
    close B;
}

=head1 HISTORY

=over

=item 0.x

The initial version.

=back

=head1 SEE ALSO

The L<cmt/"Perl_simple_module_template">

=head1 AUTHOR

Xima Lenik <name@mail.box>

=cut
1