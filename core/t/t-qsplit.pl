#!/usr/bin/perl

use strict;
use cmt::util;
use cmt::test;
use Data::Dumper;

sub SP { qr/\s+/ }

my $tests = [
	{
		'type' => 'function',
		'name' => 'qsplit: split quoted string',
		'entry' => 'cmt::util::qsplit($$)',
		'cases' => [
			[SP, 'hello']
				=> ['hello'],
			#[SP, '']
			#	=> [],
			[SP, '   ']
				=> [],
			[SP, 'hello world']
				=> ['hello', 'world'],
			[SP, '  hello  world  ']
				=> ['', 'hello', 'world'],
			[SP, 'hello,  <my>  \'Full name\' is "Xime\' Lenik" !']
			    => ['hello,', '<my>', '\'Full name\'', 'is', '"Xime\' Lenik"', '!'],
            [SP, '\'Double in "Single", haha\', and "The \'Single\' in double, hoho"!']
                => ['\'Double in "Single", haha\',', 'and', '"The \'Single\' in double, hoho"!'],
			],
	},
	];

print Dumper($tests);
test_batch $tests;

1;
