package cmt::i18n;      # Internationalization

use strict;
use cmt::lang;
# use cmt::util;
use encoding();
use Encode;
use Encode::Alias;
use Exporter;

our $opt_verbtitle      = __PACKAGE__;
our $opt_verbtime       = 0;
our $opt_verbose        = 1;

our @ISA    = qw(Exporter);
our @EXPORT = qw(hencode
                 hdecode
                 hiconv
                 hremoveents
                 );

our $DFL_ENC    = encoding::_get_locale_encoding() || do {
                    'gb2312'        # TODO: get system default encoding.
                };
our $DFL_FB     = '?';

our $ESC_LEAD   = '&#';
our $ESC_FINL   = ';';

sub esc_code { ord shift }

sub hdecode {
    my ($enc, $bin) = @_;
    return undef unless defined $bin;
    my $buf = '';
    while (length $bin) {
        $buf .= decode($enc, $bin, Encode::FB_QUIET);
        if (length $bin) {
            my $c = substr($bin, 0, 1);
            print "C-$c\n";
            $buf .= $ESC_LEAD . esc_code($c) . $ESC_FINL;
            $bin = substr($bin, 1);
        }
    }
    return $buf;
}

sub hencode {
    my ($enc, $wstr) = @_;
    return undef unless defined $wstr;
    my $buf = '';
    while (length $wstr) {
        $buf .= encode($enc, $wstr, Encode::FB_QUIET);
        if (length $wstr) {
            my $c = substr($wstr, 0, 1);
            $buf .= $ESC_LEAD . esc_code($c) . $ESC_FINL;
            $wstr = substr($wstr, 1);
        }
    }
    return $buf;
}

sub hiconv {
    my $bin     = shift;
    return undef unless defined $bin;
    my $from    = _or(shift, 'iso-8859-1');     # from raw ascii
    my $to      = _or(shift, $DFL_ENC);         # to locale default encoding
    my $wstr    = hdecode($from, $bin);
    my $cbin    = hencode($to, $wstr);
    return $cbin;
}

sub _encode {
    my ($enc, $wstr, $fallback) = @_;
    return undef unless defined $wstr;
    my $buf = '';
    while (length $wstr) {
        $buf .= encode($enc, $wstr, Encode::FB_QUIET);
        if (length $wstr) {
            # my $wc = substr($wstr, 0, 1);
            $buf .= $fallback;
            $wstr = substr($wstr, 1);
        }
    }
    return $buf;
}

sub hremoveents {   # remove entities in html
    my $str     = shift;
    my $enc     = _or(shift, $DFL_ENC);
    my $fallback= _or(shift, $DFL_FB);
    $str =~ s/&#(\d+);/ my $c = chr($1); my $bin = encode($enc, $c, Encode::FB_QUIET);
                        $bin = $fallback if (length $c); $bin /seg;
    return $str;
}

1