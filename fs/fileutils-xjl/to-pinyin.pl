#!/usr/bin/perl

use strict;
use Lingua::Han::PinYin;

my $h2p = Lingua::Han::PinYin->new();

for my $name (@ARGV) {
    my $pinyin = $h2p->han2pinyin($name);

    print "Rename $name to $pinyin\n";
    rename $name, $pinyin;
}

