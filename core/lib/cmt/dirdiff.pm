package cmt::dirdiff;

=head1 NAME

dirdiff - Perl_simple_module_template

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
    $LOGNAME    = __PACKAGE__;
    $LOGLEVEL   = 1;
use cmt::log(2);
use cmt::util('readfile', 'writefile');
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id: dirdiff.pm,v 1.1 2007-09-14 16:18:45 lenik Exp $');
    our $VER    = "0.$RCSID{rev}";
use Exporter;

our @ISA    = qw(Exporter);
our @EXPORT = qw(dirdiff
                 );

our $opt_digest_cache   = '.digest/';
our $opt_digest_method  = sub { -s shift };

sub dirdiff;
sub get_digest;

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
    # assert @_ > 1
    my @digest = map \&get_digest @_;
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

=head1 DIAGNOSTICS

(No Information)

=cut
# (HELPER FUNCTIONS)
=head2 get_digest PATH

=cut
sub get_digest {
    my $path = shift;
    return undef unless -e $path;
    return '/' if -d $path;
    my $digfile;
    if (defined $opt_digest_cache) {
        my ($dir, $base) = path_split $path;
        $digfile = path_join($dir, $opt_digest_cache, $base);
        return readfile $digfile if -f $digfile and -M $digfile > -M $path;
    }
    my $digest = $opt_digest_method->($path);
    writefile $digfile, $digest if defined $opt_digest_cache;
    return $digest;
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