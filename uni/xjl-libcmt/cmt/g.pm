package cmt::g;

=head1 NAME

cmt::g - Graphical Script Encoder Vol.gene

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
use cmt::codec;
use cmt::log(2);
    our $LOGNAME    = __PACKAGE__;
    our $LOGLEVEL   = 1;
use cmt::util();
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id$');
    our $VER    = "0.$RCSID{rev}";
use Crypt::DES;
use Crypt::CBC;
use Exporter;
use Term::ReadKey;

our @ISA    = qw(Exporter);
our @EXPORT = qw(DNA
                 DNA_decode
                 );

# INITIALIZORS

sub DNA;
sub DNA_decode;

our $opt_protect_scheme;
our $opt_protect;
our $opt_cipher;

=head1 SYNOPSIS

    use cmt::g;

    DNA << DNA;
        AT
        C G
        ...
    DNA

=head1 Graphical Script Encoder Vol.gene

B<cmt::g> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-cmt::g-RESOLVES.

=head1 FUNCTIONS

=cut
=head2 mysub(arguments)

=cut

sub DNA {
    my $dna_slices = shift;
    my $program = DNA_decode $dna_slices;
    my $ret = eval "package main; $program";
        die "Gene Error: $@\n" if ($@);
    return $program;
}

sub DNA_decode {
    my @slice_lines;
       @slice_lines = split(/\n/, shift);
    my $slice_line;
    my @decoded_lines;
    my $decoded_line = '';
    my $current_slice = 0;
    my $i_ord = 0;
    my $demess = 0;
    #my $state = 'PARTIAL';

    while (@slice_lines) {
        my $slice_line = shift @slice_lines;

        #if ($state eq 'COPY') {
        #    $state = 'PARTIAL';
        #    next;
        #}

        next if $slice_line !~ m/^\s*(\w)/;

        my $bit2 = index('ATCG', $1);
        $bit2 = ($bit2 + $demess) & 3;
        $demess = 3 if --$demess == -1;
        $i_ord = ($i_ord << 2) | $bit2;

        if ($current_slice == 3) {
            # Submit a byte
            my $byte = $i_ord;
            $i_ord = 0;
            $current_slice = 0;

            if ($byte == 13) {
                # ignore CR
            } elsif ($byte == 10) {
                if ($opt_cipher) {
                    $decoded_line = $opt_cipher->decrypt(hexbin($decoded_line));
                    my $len = unpack('n', $decoded_line);
                    $decoded_line = substr($decoded_line, 2, $len);
                }

                if ($decoded_line =~ m/^\#\@DNA::(\w+)\s*(.*)$/) {
                    my ($_cmd, $_args) = ($1, $2);

                    if ($_cmd eq 'PARTIAL') {
                        #$state = $_cmd;
                    } elsif ($_cmd eq 'COPY') {
                        #$state = $_cmd;
                    } elsif ($_cmd eq 'PROTECT') {
                        &DNA_init_protect;
                        $decoded_line = '';     # ignore #@DNA::PROTECT
                        next;
                    }
                }

                push @decoded_lines, $decoded_line;
                $decoded_line = '';
            } else {
                $decoded_line .= chr $byte;
            }

        } else {
            $current_slice++;
        }
    }

    if ($decoded_line) {
        if ($opt_cipher) {
            $decoded_line = $opt_cipher->decrypt(hexbin $decoded_line);
            my $len = unpack('n', $decoded_line);
            $decoded_line = substr($decoded_line, 2, $len);
        }
        push @decoded_lines, $decoded_line;
        $decoded_line = '';
    }

    if (wantarray) {
        return @decoded_lines;
    } else {
        return join("\n", @decoded_lines);
    }
}

sub DNA_init_protect {
    my $args = shift;

    $opt_protect_scheme = $args;
    if (not defined $opt_protect) {
        ReadMode 'noecho';
        print STDERR "Enter passphrase: \n";
        $opt_protect = ReadLine 0;
        chomp $opt_protect;
        ReadMode 'normal';
        $opt_cipher = new Crypt::CBC($opt_protect, 'DES');
    }
}

=head1 DIAGNOSTICS

(No Information)

=cut
# (HELPER FUNCTIONS)

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