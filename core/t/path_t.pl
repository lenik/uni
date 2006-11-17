#!perl

use strict;
use cmt::path qw(
                path_normalize
                path_split
                path_splitext
		path_segs
		path_comp
		path_join
		);
use cmt::test;


my $tests = [
	{
		'type' => 'function',
		'name' => 'path_normalize: Normalize path string',
		'entry' => 'cmt::path::path_normalize($)',
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
		'entry' => 'cmt::path::path_splitext($)',
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
		'entry' => 'cmt::path::path_join(@)',
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
	}, {
	    'type' => 'function',
	    'name' => 'path_split: Split path into directory-name and file-name',
	    'entry' => 'cmt::path::path_split($)',
	    'cases' => [
	        ['']
	            => ['', ''],
	        ['onlyname.ext']
	            => ['', 'onlyname.ext'],
	        ['/rootname']
	            => ['/', 'rootname'],
	        ['../../relative/relativename.ext']
	            => ['../../relative/', 'relativename.ext'],
	        ['/a/b/c/noname/']
	            => ['/a/b/c/noname/', ''],
	        ['/']
	            => ['/', ''],
	        ['../ends-with-specials/..']
	            => ['../ends-with-specials/', '..'],
	        ],
	},
	];

test_batch $tests;

1;
