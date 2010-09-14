package cmt::codec;

use strict;
use Crypt::CBC;
use Crypt::DES;
use Digest::MD5 qw/md5 md5_hex md5_base64/;
use Digest::SHA1 qw/sha1 sha1_hex sha1_base64/;
use Encode;
use Encode::Alias;
use MIME::Base64;
use Exporter;
use vars qw/@ISA @EXPORT/;

sub hex_encode {
    my $binstr = shift;
    my $len = length($binstr) * 2;
    unpack "H$len", $binstr;
}

sub hex_decode {
    my $hexstr = shift;
    my $len = length($hexstr);
    pack "H$len", $hexstr;
}

sub base64_encode {
    encode_base64 shift;
}

sub base64_decode {
    decode_base64 shift;
}

our %CODECS = (
    'hex'       => [\&hex_encode,       \&hex_decode],
    'base64'    => [\&base64_encode,    \&base64_decode],
    );

sub binhex; *binhex = *hex_encode;
sub hexbin; *hexbin = *hex_decode;
sub codec_sy;
sub codec_dq;
sub codec_dm;

sub codec_sy {
    my ($ph, $cat, $text) = @_;
    my $cipher = new Crypt::CBC(-key => $ph, -cipher => 'DES');
    my $result;
    if ($cat eq 'e') {
        $result = binhex $cipher->encrypt(pack('n', length($text)) . $text);
    } elsif ($cat eq 'd') {
        $result = $cipher->decrypt(hexbin $text);
        my $len = unpack('n', $result);
        $result = substr($result, 2, $len);
    } else {
        die "Illegal category: $cat";
    }
    $result;
}

sub codec_dq {
    my ($ph, $cat, $n) = @_;
    my @A = (
             (500000 + ($n % 4869) * (5465 - $n % 5466)) % 1000000,
             (782434 + ($n % 6368) * (3141 - $n % 3142)) % 1000000,
             (812953 + ($n % 6806) * (6299 - $n % 6300)) % 1000000,
             (110221 + ($n % 1947) * (1680 - $n % 1681)) % 1000000,
             );
    my @B = (
             ($A[0] + 10 * $A[1] + 100 * $A[2] + 1000 * $A[3]) % 1000000,
             ($A[1] + 10 * $A[2] + 100 * $A[3] + 1000 * $A[0]) % 1000000,
             ($A[2] + 10 * $A[3] + 100 * $A[0] + 1000 * $A[1]) % 1000000,
             ($A[3] + 10 * $A[0] + 100 * $A[1] + 1000 * $A[2]) % 1000000,
             );
    my @C = (
             ($B[0] + 10 * $B[1] + 100 * $B[2] + 1000 * $B[3]) % 1000000,
             ($B[1] + 10 * $B[2] + 100 * $B[3] + 1000 * $B[0]) % 1000000,
             ($B[2] + 10 * $B[3] + 100 * $B[0] + 1000 * $B[1]) % 1000000,
             ($B[3] + 10 * $B[0] + 100 * $B[1] + 1000 * $B[2]) % 1000000,
             );
    my %map = (
               'a1' => $A[0], 'a2' => $A[1], 'a3' => $A[2], 'a4' => $A[3],
               'b1' => $B[0], 'b2' => $B[1], 'b3' => $B[2], 'b4' => $B[3],
               'c1' => $C[0], 'c2' => $C[1], 'c3' => $C[2], 'c4' => $C[3],
               );
    $map{$cat};
}

sub codec_dm {
    my ($ph, $cat, $text) = @_;
    my $digest = md5_base64("$ph (at) $text");
    $digest =~ s/\+/o/g;
    $digest =~ s/\\/i/g;
    my $result = substr($digest, 0, $cat);
    $result;
}

@ISA    = qw(Exporter);
@EXPORT = qw(
             %CODECS
             binhex hexbin
             codec_sy codec_dq codec_dm
            );
__END__

=head1 NAME

usecode (internal use)

=head1 SYNOPSIS

    No synopsis.

=head1 DESCRIPTION

    No description.
