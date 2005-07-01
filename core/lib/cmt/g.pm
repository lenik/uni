package cmt::g;

use strict;
use Exporter;
use vars qw/@ISA @EXPORT/;


sub DNA;
sub DNA_decode;

our $opt_protect_scheme;
our $opt_protect;
our $opt_cipher;


sub DNA {
    my $dna_slices = shift;
    my $program = DNA_decode $dna_slices;
    my $ret = eval "package main; $program";
        die "Gene Error: $@\n" if ($@);
    return $program;
}


sub DNA_decode {
    my @slice_lines;
       @slice_lines = split("\n", shift);
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

    use cmt::codec;
    use Crypt::DES;
    use Crypt::CBC;
    use Term::ReadKey;

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



@ISA = qw/Exporter/;
@EXPORT = qw/DNA DNA_decode/;



__END__

=head1 NAME

gene - Graphical Script Encoder Vol.gene

=head1 SYNOPSIS

    use gene;

    DNA << DNA;
        AT
        C G
        ...
    DNA

=head1 DESCRIPTION

    No description.

