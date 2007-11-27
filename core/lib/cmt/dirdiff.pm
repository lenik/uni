package cmt::dirdiff;

=head1 NAME

dirdiff - Perl_simple_module_template

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
use cmt::lang('_def');
    $LOGNAME    = __PACKAGE__;
    $LOGLEVEL   = 1;
use cmt::log(2);
use cmt::path('path_join', 'path_split');
use cmt::util('readfile', 'writefile', 'listdir');
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id$');
    our $VER    = "0.$RCSID{rev}";
use Exporter;
use File::Path('mkpath');
use Digest::SHA1;

our @ISA    = qw(Exporter);
our @EXPORT = qw(dirdiff
                 dump_plain
                 );

our $opt_digest_cache   = '.digest';

sub dirdiff;
sub dump_plain;

sub gname;
sub sha1_hex_f;
sub new_diglist;
sub load_diglist;
sub save_diglist;
sub calc_diglist;
sub flatten_diglist;
sub get_digest;

sub shortdig    { my $t = shift; $t =~ s/:.+$/:/; $t }

# INITIALIZORS

=head1 SYNOPSIS

    use dirdiff;
    mysub(arguments...)

=head1 DESCRIPTION

B<dirdiff> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-dirdiff-RESOLVES.

=head1 FUNCTIONS

=cut

=head2 dirdiff LIST

=cut
sub dirdiff {
    _sig1 '1', $_[0];
    # assert @_ > 1
    my @digest = map { get_digest $_ } @_;
    my %digest;
        for (0..$#_) {
            my $dig = $digest[$_];
            next unless defined $dig;
            push @{$digest{$dig}}, $_[$_];
        }
    my @detail;
    for (keys %digest) {
        next unless defined $_; # unexisted items are not contained in detail.
        my $group = $digest{$_};
        if (@$group == 1) {
            # single => digest
            push @detail, (@$group, $_);
        } else {
            if ($_ eq '/') {
                my %union;
                my @subdiff;
                for my $g (@$group) {
                    for my $f (listdir $g, undef, qr/^\.\.?$/) {
                        next if $f eq $opt_digest_cache;
                        next if exists $union{$f};
                        $union{$f} = 1;
                        my $fdet = dirdiff map { path_join($_, $f) } @$group;
                        push @subdiff, ($f, $fdet);
                    }
                }
                $_ = \@subdiff;
            }
            return $_ if @$group == @_;
            # [group] => digest
            push @detail, ($group, $_);
        }
    }
    return \@detail;
}

sub dump_plain {
    my ($det, $lev) = @_;
    my $lead = '    'x$lev;
    my $glead = sub { sprintf '%'.(8 + length $lead).'s',
                        @_ ? gname(shift).'> ' : '' };
    my $i = 0;
    while ($i < @$det) {
        my $file = $det->[$i++];
        my $dig = $det->[$i++];
        if (ref $dig) {
            my $j = 0;
            while ($j < @$dig) {
                my $g = $dig->[$j++];
                my $sub = $dig->[$j++];
                if (ref $sub) {
                    print $glead->($g).$file.'/'."\n";
                    dump_plain($sub, $lev + 1);
                } else {
                    $sub = ' ('.shortdig($sub).')' if $sub ne '/';
                    if (ref $g or $g =~ /[\/\\]/) {
                        # the same in group or single-group
                        # (partial same or distinct different??)
                        _P0 $glead->($g).$file.$sub;
                    } else {
                        # the same in whole
                        _P0 '    '.$glead->().$g.$sub;
                    }
                }
            }
        } else { # the same in whole
            $dig = ' ('.shortdig($dig).')' if $dig ne '/';
            _P0 $glead->().$file.$dig;
        }
    }
}

=head1 DIAGNOSTICS

(No Information)

=cut
# (HELPER FUNCTIONS)

# group-name: concat of the outmost dirname of members
sub gname {
    my $g = shift;
    if (ref $g) {
        my @d = map { /^([^\/\\]+)/; $1} @$g;
        join(', ', @d);
    } else {
        $g =~ /^([^\/\\]+)/; $1
    }
}

sub sha1_hex_f {
    my $f = shift;
    open(FH, '<', $f) or die "error open $f for read";
    my $sd = new Digest::SHA1;
    $sd->addfile(*FH);
    my $sha1 = $sd->hexdigest;
    close FH;
    $sha1
}

sub new_diglist {
    my $f = shift;
    [ -s $f, sub { sha1_hex_f($f) } ]
}

sub load_diglist {
    my $digfile = shift;
    my $digs = readfile $digfile;
    my @list = split(/\n/, $digs);
    \@list
}

sub save_diglist {
    my $digfile = shift;
    my $list = shift;
    calc_diglist $list;
    writefile $digfile, join("\n", @$list);
}

sub calc_diglist {
    my $list = shift;
    my $i = 0;
    while ($i <= $#$list) {
        my $v = $list->[$i];
        if (ref $v eq 'CODE') {
            splice @$list, $i, 1, $v->();
        } else {
            $i++;
        }
    }
}

sub flatten_diglist {
    my $list = shift;
    calc_diglist $list;
    join(':', @$list)
}

sub get_digest {
    my $path = shift;
    return undef unless -e $path;
    return '/' if -d $path;
    my $digdir;
    my $digfile;
    my $diglist;
    if (defined $opt_digest_cache) {
        my ($dir, $base) = path_split $path;
        $digdir = path_join($dir, $opt_digest_cache);
        $digfile = path_join($digdir, $base);
        $diglist = load_diglist($digfile) if -f $digfile and -M $digfile > -M $path;
    }
    unless (defined $diglist) {
        $diglist = new_diglist($path);
        if (defined $opt_digest_cache) {
            mkpath $digdir;
            save_diglist $digfile, $diglist;
        }
    }
    return flatten_diglist($diglist);
}

=head1 HISTORY

=over

=item 0.x

The initial version.

=back

=head1 SEE ALSO

The L<cmt/"Perl_simple_module_template">

=head1 AUTHOR

Xima Lenik <lenik@bodz.net>

=cut
1