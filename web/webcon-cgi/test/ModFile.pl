#!/usr/bin/perl -I../../

package webcon::test::ModFile;

use strict;
use webcon::Config;
use webcon::test::Base;
use webcon::FileBase;
use Data::Dumper;

# print Dumper(\%WConf);

$SLASH_CHR = '/';

my $tests = [
	{
		'type' => 'function',
		'name' => 'path_normalize: Normalize path string',
		'entry' => 'webcon::FileBase::path_normalize($)',
		'cases' => [
			['a/b/c/d/e/f/g/']
				=> 'a/b/c/d/e/f/g',
			['a/b/../../c/d/e/']
				=> 'c/d/e',
			['../../a/b/../c/./d']
				=> '../../a/c/d',
			['/a/b/../../../../c/d']
				=> '/c/d',
			['/a/b/../../../../c/d/../../../e/f']
				=> '/e/f',
			['']
				=> '.',
			['.///']
				=> '.',
			['///c/d']
				=> '/c/d',
			['././abc/../../xyz']
				=> '../xyz',
			],
	}, {
		'type' => 'function',
		'name' => 'path_getext: Get the extension name of given path',
		'entry' => 'webcon::FileBase::path_getext($)',
		'cases' => [
			['filename.ext']
				=> '.ext',
			['/a/b/../../../../c/d/../../../e/f.abc.def']
				=> '.def',
			['././abc/../../xyz']
				=> '',
			['././abc/../../xyz.']
				=> '.',
			],
	}, {
		'type' => 'function',
		'name' => 'path_join: Join linked parts of path',
		'entry' => 'webcon::FileBase::path_join(@)',
		'cases' => [
			['a/b/c', 'd/e/f/g'],
				=> 'a/b/c/d/e/f/g',
			['a/b/c/d', '../../e/f/g', './h/i/j'],
				=> 'a/b/e/f/g/h/i/j',
			['/a/b/../c', '', ''],
				=> '/a/c',
			['']
				=> '.',
			['../../a/b/c', '/d/e/f', '/g/h/i']
				=> '/g/h/i',
			],
	}
	];

test_batch $tests;

1;
