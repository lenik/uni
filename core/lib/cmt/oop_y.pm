####################################################################
#
#    This file was generated using Parse::Yapp version 1.05.
#
#        Don't edit this file, use source file instead.
#
#             ANY CHANGE MADE HERE WILL BE LOST !
#
####################################################################
package cmt::oop_y;
use vars qw ( @ISA );
use strict;

@ISA= qw ( Parse::Yapp::Driver );
use Parse::Yapp::Driver;

#line 17 "oop_y.y"

    my @S;

    sub _H($) { $_[0] }
    sub _J {
        my $t = shift;
        $t eq $_[0]->[0]
            ? [@{$_[0]}, $_[1]]
            : $t eq $_[1]->[0]
                ? [$t, $_[0], @{$_[1]}[1..$#{$_[1]}]]
                : [$t, $_[0], $_[1]]
    }
    sub _M { _H { map { $_ => 1 } @_ } }
    sub _S { push @S, $_[0]; $_[0] }


sub new {
        my($class)=shift;
        ref($class)
    and $class=ref($class);

    my($self)=$class->SUPER::new( yyversion => '1.05',
                                  yystates =>
[
	{#State 0
		ACTIONS => {
			'_string' => 1
		},
		DEFAULT => -4,
		GOTOS => {
			'text' => 2,
			'textl' => 3,
			'start' => 5,
			'header' => 4
		}
	},
	{#State 1
		DEFAULT => -6
	},
	{#State 2
		ACTIONS => {
			'_sect_delim' => 6
		}
	},
	{#State 3
		ACTIONS => {
			'_string' => 7
		},
		DEFAULT => -5
	},
	{#State 4
		ACTIONS => {
			'_id' => 9
		},
		GOTOS => {
			'symbol_name' => 8,
			'rule' => 10,
			'rules' => 11
		}
	},
	{#State 5
		ACTIONS => {
			'' => 12
		}
	},
	{#State 6
		DEFAULT => -2
	},
	{#State 7
		DEFAULT => -7
	},
	{#State 8
		ACTIONS => {
			":" => 13,
			'_ruledef_op' => 14
		},
		GOTOS => {
			'ruledef_op' => 15
		}
	},
	{#State 9
		DEFAULT => -41
	},
	{#State 10
		DEFAULT => -8
	},
	{#State 11
		ACTIONS => {
			'_id' => 9,
			'_sect_delim' => 16
		},
		GOTOS => {
			'symbol_name' => 8,
			'rule' => 17,
			'footer' => 18
		}
	},
	{#State 12
		DEFAULT => 0
	},
	{#State 13
		DEFAULT => -44
	},
	{#State 14
		DEFAULT => -45
	},
	{#State 15
		ACTIONS => {
			'_string' => 19,
			'_id' => 23,
			"{" => 24,
			"%" => 25,
			'_char' => 27,
			"(" => 29,
			'_rw_cntl' => 30,
			'_code' => 34
		},
		GOTOS => {
			'quantifiers' => 28,
			'alias_name' => 20,
			'symbol_name' => 22,
			'spec' => 21,
			'rule_exp' => 31,
			'function_name' => 32,
			'rw_exp' => 33,
			'call' => 35,
			'term' => 26
		}
	},
	{#State 16
		ACTIONS => {
			'_string' => 1
		},
		DEFAULT => -4,
		GOTOS => {
			'text' => 36,
			'textl' => 3
		}
	},
	{#State 17
		DEFAULT => -9
	},
	{#State 18
		DEFAULT => -1
	},
	{#State 19
		DEFAULT => -25
	},
	{#State 20
		ACTIONS => {
			"=" => 37
		}
	},
	{#State 21
		DEFAULT => -23
	},
	{#State 22
		DEFAULT => -12
	},
	{#State 23
		ACTIONS => {
			"<" => -43,
			"=" => -42
		},
		DEFAULT => -41
	},
	{#State 24
		ACTIONS => {
			'_string' => 19,
			'_id' => 23,
			"{" => 24,
			"%" => 25,
			'_char' => 27,
			"(" => 29,
			'_rw_cntl' => 30,
			'_code' => 34
		},
		GOTOS => {
			'quantifiers' => 28,
			'alias_name' => 20,
			'symbol_name' => 22,
			'spec' => 21,
			'rule_exp' => 38,
			'function_name' => 32,
			'rw_exp' => 33,
			'call' => 35,
			'term' => 26
		}
	},
	{#State 25
		ACTIONS => {
			'_id' => 39
		}
	},
	{#State 26
		DEFAULT => -11
	},
	{#State 27
		DEFAULT => -24
	},
	{#State 28
		DEFAULT => -14
	},
	{#State 29
		ACTIONS => {
			'_string' => 19,
			'_id' => 23,
			"{" => 24,
			"%" => 25,
			'_char' => 27,
			"(" => 29,
			'_rw_cntl' => 30,
			'_code' => 34,
			")" => 40
		},
		GOTOS => {
			'quantifiers' => 28,
			'alias_name' => 20,
			'spec' => 21,
			'symbol_name' => 22,
			'rule_exp' => 41,
			'function_name' => 32,
			'rw_exp' => 33,
			'call' => 35,
			'term' => 26
		}
	},
	{#State 30
		DEFAULT => -38
	},
	{#State 31
		ACTIONS => {
			'_string' => 19,
			"?" => 45,
			";" => 43,
			"+" => 42,
			'_id' => 23,
			"{" => 46,
			"/" => 47,
			"%" => 25,
			'_char' => 27,
			"|" => 48,
			"(" => 29,
			'_rw_cntl' => 30,
			"*" => 44,
			'_code' => 34
		},
		GOTOS => {
			'quantifiers' => 28,
			'alias_name' => 20,
			'spec' => 21,
			'symbol_name' => 22,
			'rule_exp' => 49,
			'function_name' => 32,
			'rw_exp' => 33,
			'call' => 35,
			'term' => 26
		}
	},
	{#State 32
		ACTIONS => {
			"<" => 50
		}
	},
	{#State 33
		DEFAULT => -22
	},
	{#State 34
		DEFAULT => -13
	},
	{#State 35
		DEFAULT => -15
	},
	{#State 36
		DEFAULT => -3
	},
	{#State 37
		ACTIONS => {
			'_string' => 19,
			'_id' => 23,
			"{" => 24,
			"%" => 25,
			'_char' => 27,
			"(" => 29,
			'_rw_cntl' => 30,
			'_code' => 34
		},
		GOTOS => {
			'quantifiers' => 28,
			'alias_name' => 20,
			'spec' => 21,
			'symbol_name' => 22,
			'rule_exp' => 51,
			'function_name' => 32,
			'rw_exp' => 33,
			'call' => 35,
			'term' => 26
		}
	},
	{#State 38
		ACTIONS => {
			'_string' => 19,
			"?" => 45,
			"+" => 42,
			'_id' => 23,
			"{" => 46,
			"/" => 47,
			"," => 52,
			"%" => 25,
			'_char' => 27,
			"|" => 48,
			"(" => 29,
			'_rw_cntl' => 30,
			"*" => 44,
			'_code' => 34
		},
		GOTOS => {
			'quantifiers' => 28,
			'alias_name' => 20,
			'spec' => 21,
			'symbol_name' => 22,
			'rule_exp' => 49,
			'function_name' => 32,
			'rw_exp' => 33,
			'call' => 35,
			'term' => 26
		}
	},
	{#State 39
		ACTIONS => {
			'_char' => 54,
			'_id' => 53
		}
	},
	{#State 40
		DEFAULT => -16
	},
	{#State 41
		ACTIONS => {
			'_string' => 19,
			"?" => 45,
			"+" => 42,
			'_id' => 23,
			"{" => 46,
			"/" => 47,
			"%" => 25,
			'_char' => 27,
			"|" => 48,
			"(" => 29,
			'_rw_cntl' => 30,
			"*" => 44,
			'_code' => 34,
			")" => 55
		},
		GOTOS => {
			'quantifiers' => 28,
			'alias_name' => 20,
			'spec' => 21,
			'symbol_name' => 22,
			'rule_exp' => 49,
			'function_name' => 32,
			'rw_exp' => 33,
			'call' => 35,
			'term' => 26
		}
	},
	{#State 42
		DEFAULT => -28
	},
	{#State 43
		DEFAULT => -10
	},
	{#State 44
		DEFAULT => -27
	},
	{#State 45
		DEFAULT => -26
	},
	{#State 46
		ACTIONS => {
			'_string' => 19,
			'_id' => 23,
			"{" => 24,
			"%" => 25,
			'_number' => 57,
			'_char' => 27,
			"(" => 29,
			'_rw_cntl' => 30,
			'_code' => 34,
			"." => 58
		},
		GOTOS => {
			'alias_name' => 20,
			'spec' => 21,
			'symbol_name' => 22,
			'range' => 56,
			'term' => 26,
			'quantifiers' => 28,
			'rule_exp' => 38,
			'function_name' => 32,
			'rw_exp' => 33,
			'call' => 35
		}
	},
	{#State 47
		ACTIONS => {
			'_id' => 60
		},
		GOTOS => {
			'alias_name' => 59
		}
	},
	{#State 48
		ACTIONS => {
			'_string' => 19,
			'_id' => 23,
			"{" => 24,
			"%" => 25,
			'_char' => 27,
			"(" => 29,
			'_rw_cntl' => 30,
			'_code' => 34
		},
		GOTOS => {
			'quantifiers' => 28,
			'alias_name' => 20,
			'spec' => 21,
			'symbol_name' => 22,
			'rule_exp' => 61,
			'function_name' => 32,
			'rw_exp' => 33,
			'call' => 35,
			'term' => 26
		}
	},
	{#State 49
		ACTIONS => {
			"+" => 42,
			'_id' => 23,
			"%" => 25,
			"*" => 44,
			'_string' => 19,
			"?" => 45,
			"{" => 46,
			"/" => 47,
			'_char' => 27,
			"(" => 29,
			'_rw_cntl' => 30,
			'_code' => 34
		},
		DEFAULT => -20,
		GOTOS => {
			'quantifiers' => 28,
			'alias_name' => 20,
			'spec' => 21,
			'symbol_name' => 22,
			'rule_exp' => 49,
			'function_name' => 32,
			'rw_exp' => 33,
			'call' => 35,
			'term' => 26
		}
	},
	{#State 50
		ACTIONS => {
			'_string' => 19,
			'_id' => 23,
			"{" => 24,
			"%" => 25,
			'_char' => 27,
			"(" => 29,
			'_rw_cntl' => 30,
			'_code' => 34
		},
		GOTOS => {
			'explist' => 62,
			'quantifiers' => 28,
			'alias_name' => 20,
			'spec' => 21,
			'symbol_name' => 22,
			'rule_exp' => 63,
			'function_name' => 32,
			'rw_exp' => 33,
			'call' => 35,
			'term' => 26
		}
	},
	{#State 51
		ACTIONS => {
			"+" => 42,
			'_id' => 23,
			"%" => 25,
			"*" => 44,
			'_string' => 19,
			"?" => 45,
			"{" => 46,
			'_char' => 27,
			"(" => 29,
			'_rw_cntl' => 30,
			'_code' => 34
		},
		DEFAULT => -18,
		GOTOS => {
			'quantifiers' => 28,
			'alias_name' => 20,
			'spec' => 21,
			'symbol_name' => 22,
			'rule_exp' => 49,
			'function_name' => 32,
			'rw_exp' => 33,
			'call' => 35,
			'term' => 26
		}
	},
	{#State 52
		ACTIONS => {
			'_string' => 19,
			'_id' => 23,
			"{" => 24,
			"%" => 25,
			'_char' => 27,
			"(" => 29,
			'_rw_cntl' => 30,
			'_code' => 34
		},
		GOTOS => {
			'quantifiers' => 28,
			'alias_name' => 20,
			'spec' => 21,
			'symbol_name' => 22,
			'rule_exp' => 64,
			'function_name' => 32,
			'rw_exp' => 33,
			'call' => 35,
			'term' => 26
		}
	},
	{#State 53
		DEFAULT => -39
	},
	{#State 54
		DEFAULT => -40
	},
	{#State 55
		DEFAULT => -17
	},
	{#State 56
		ACTIONS => {
			"}" => 65
		}
	},
	{#State 57
		ACTIONS => {
			"." => 66
		},
		DEFAULT => -31
	},
	{#State 58
		ACTIONS => {
			"." => 67
		}
	},
	{#State 59
		DEFAULT => -19
	},
	{#State 60
		DEFAULT => -42
	},
	{#State 61
		ACTIONS => {
			"+" => 42,
			'_id' => 23,
			"%" => 25,
			"*" => 44,
			'_string' => 19,
			"?" => 45,
			"{" => 46,
			"/" => 47,
			'_char' => 27,
			"(" => 29,
			'_rw_cntl' => 30,
			'_code' => 34
		},
		DEFAULT => -21,
		GOTOS => {
			'quantifiers' => 28,
			'alias_name' => 20,
			'spec' => 21,
			'symbol_name' => 22,
			'rule_exp' => 49,
			'function_name' => 32,
			'rw_exp' => 33,
			'call' => 35,
			'term' => 26
		}
	},
	{#State 62
		ACTIONS => {
			"," => 68,
			">" => 69
		}
	},
	{#State 63
		ACTIONS => {
			'_string' => 19,
			"?" => 45,
			"+" => 42,
			'_id' => 23,
			"{" => 46,
			"/" => 47,
			"%" => 25,
			'_char' => 27,
			"|" => 48,
			"(" => 29,
			'_rw_cntl' => 30,
			"*" => 44,
			'_code' => 34
		},
		DEFAULT => -36,
		GOTOS => {
			'quantifiers' => 28,
			'alias_name' => 20,
			'spec' => 21,
			'symbol_name' => 22,
			'rule_exp' => 49,
			'function_name' => 32,
			'rw_exp' => 33,
			'call' => 35,
			'term' => 26
		}
	},
	{#State 64
		ACTIONS => {
			'_string' => 19,
			"}" => 70,
			"?" => 45,
			"+" => 42,
			'_id' => 23,
			"{" => 46,
			"/" => 47,
			"%" => 25,
			'_char' => 27,
			"|" => 48,
			"(" => 29,
			'_rw_cntl' => 30,
			"*" => 44,
			'_code' => 34
		},
		GOTOS => {
			'quantifiers' => 28,
			'alias_name' => 20,
			'spec' => 21,
			'symbol_name' => 22,
			'rule_exp' => 49,
			'function_name' => 32,
			'rw_exp' => 33,
			'call' => 35,
			'term' => 26
		}
	},
	{#State 65
		DEFAULT => -29
	},
	{#State 66
		ACTIONS => {
			"." => 71
		}
	},
	{#State 67
		ACTIONS => {
			'_number' => 72
		}
	},
	{#State 68
		ACTIONS => {
			'_string' => 19,
			'_id' => 23,
			"{" => 24,
			"%" => 25,
			'_char' => 27,
			"(" => 29,
			'_rw_cntl' => 30,
			'_code' => 34
		},
		GOTOS => {
			'quantifiers' => 28,
			'alias_name' => 20,
			'spec' => 21,
			'symbol_name' => 22,
			'rule_exp' => 73,
			'function_name' => 32,
			'rw_exp' => 33,
			'call' => 35,
			'term' => 26
		}
	},
	{#State 69
		DEFAULT => -35
	},
	{#State 70
		DEFAULT => -30
	},
	{#State 71
		ACTIONS => {
			'_number' => 74
		},
		DEFAULT => -32
	},
	{#State 72
		DEFAULT => -33
	},
	{#State 73
		ACTIONS => {
			'_string' => 19,
			"?" => 45,
			"+" => 42,
			'_id' => 23,
			"{" => 46,
			"/" => 47,
			"%" => 25,
			'_char' => 27,
			"|" => 48,
			"(" => 29,
			'_rw_cntl' => 30,
			"*" => 44,
			'_code' => 34
		},
		DEFAULT => -37,
		GOTOS => {
			'quantifiers' => 28,
			'alias_name' => 20,
			'spec' => 21,
			'symbol_name' => 22,
			'rule_exp' => 49,
			'function_name' => 32,
			'rw_exp' => 33,
			'call' => 35,
			'term' => 26
		}
	},
	{#State 74
		DEFAULT => -34
	}
],
                                  yyrules  =>
[
	[#Rule 0
		 '$start', 2, undef
	],
	[#Rule 1
		 'start', 3,
sub
#line 35 "oop_y.y"
{ { 'header' => $_[1],
                                        'rules'  => $_[2],
                                        'footer' => $_[3],
                                        'seq'    => [ @S ],
                                        'seqm'   => _M @S } }
	],
	[#Rule 2
		 'header', 2,
sub
#line 43 "oop_y.y"
{ undef @S; $_[1] }
	],
	[#Rule 3
		 'footer', 2,
sub
#line 47 "oop_y.y"
{ $_[2] }
	],
	[#Rule 4
		 'text', 0,
sub
#line 51 "oop_y.y"
{ '' }
	],
	[#Rule 5
		 'text', 1,
sub
#line 52 "oop_y.y"
{ join('', @{$_[1]}) }
	],
	[#Rule 6
		 'textl', 1,
sub
#line 56 "oop_y.y"
{ [$_[1]] }
	],
	[#Rule 7
		 'textl', 2,
sub
#line 57 "oop_y.y"
{ [@{$_[1]}, $_[2]] }
	],
	[#Rule 8
		 'rules', 1,
sub
#line 61 "oop_y.y"
{ _H { _S($_[1]->[0]) => $_[1]->[1] } }
	],
	[#Rule 9
		 'rules', 2,
sub
#line 62 "oop_y.y"
{ $_[1]->{_S($_[2]->[0])} = $_[2]->[1];
                                      $_[1] }
	],
	[#Rule 10
		 'rule', 4,
sub
#line 68 "oop_y.y"
{ [ $_[1], $_[3] ] }
	],
	[#Rule 11
		 'rule_exp', 1, undef
	],
	[#Rule 12
		 'rule_exp', 1,
sub
#line 73 "oop_y.y"
{ ['ref',       $_[1]] }
	],
	[#Rule 13
		 'rule_exp', 1,
sub
#line 74 "oop_y.y"
{ ['code',      $_[1]] }
	],
	[#Rule 14
		 'rule_exp', 1, undef
	],
	[#Rule 15
		 'rule_exp', 1, undef
	],
	[#Rule 16
		 'rule_exp', 2,
sub
#line 77 "oop_y.y"
{ ['empty'] }
	],
	[#Rule 17
		 'rule_exp', 3,
sub
#line 78 "oop_y.y"
{ ['group',     $_[2]] }
	],
	[#Rule 18
		 'rule_exp', 3,
sub
#line 79 "oop_y.y"
{ ['alias',     $_[1], $_[3]] }
	],
	[#Rule 19
		 'rule_exp', 3,
sub
#line 80 "oop_y.y"
{ ['alias',     $_[3], $_[1]] }
	],
	[#Rule 20
		 'rule_exp', 2,
sub
#line 81 "oop_y.y"
{ _J('concat',  $_[1], $_[2]) }
	],
	[#Rule 21
		 'rule_exp', 3,
sub
#line 82 "oop_y.y"
{ _J('or',      $_[1], $_[3]) }
	],
	[#Rule 22
		 'rule_exp', 1, undef
	],
	[#Rule 23
		 'rule_exp', 1, undef
	],
	[#Rule 24
		 'term', 1,
sub
#line 88 "oop_y.y"
{ ['char',      $_[1]] }
	],
	[#Rule 25
		 'term', 1,
sub
#line 89 "oop_y.y"
{ ['string',    $_[1]] }
	],
	[#Rule 26
		 'quantifiers', 2,
sub
#line 93 "oop_y.y"
{ ['qt',        $_[1], 0, 1] }
	],
	[#Rule 27
		 'quantifiers', 2,
sub
#line 94 "oop_y.y"
{ ['qt',        $_[1], 0] }
	],
	[#Rule 28
		 'quantifiers', 2,
sub
#line 95 "oop_y.y"
{ ['qt',        $_[1], 1] }
	],
	[#Rule 29
		 'quantifiers', 4,
sub
#line 96 "oop_y.y"
{ ['qt',        $_[1], @{$_[3]}] }
	],
	[#Rule 30
		 'quantifiers', 5,
sub
#line 97 "oop_y.y"
{ ['repeat',    $_[2], $_[4]] }
	],
	[#Rule 31
		 'range', 1,
sub
#line 101 "oop_y.y"
{ [ $_[1], $_[1] ] }
	],
	[#Rule 32
		 'range', 3,
sub
#line 102 "oop_y.y"
{ [ $_[1], ] }
	],
	[#Rule 33
		 'range', 3,
sub
#line 103 "oop_y.y"
{ [ 0, $_[3] ] }
	],
	[#Rule 34
		 'range', 4,
sub
#line 104 "oop_y.y"
{ [ $_[1], $_[4] ] }
	],
	[#Rule 35
		 'call', 4,
sub
#line 108 "oop_y.y"
{ ['call',      $_[1], @{$_[3]}] }
	],
	[#Rule 36
		 'explist', 1,
sub
#line 112 "oop_y.y"
{ [$_[1]] }
	],
	[#Rule 37
		 'explist', 3,
sub
#line 113 "oop_y.y"
{ [@{$_[1]}, $_[3]] }
	],
	[#Rule 38
		 'rw_exp', 1,
sub
#line 117 "oop_y.y"
{ ['rw_cntl',   $_[1]] }
	],
	[#Rule 39
		 'spec', 3,
sub
#line 121 "oop_y.y"
{ ['raw',       '%'.$_[2].' '.$_[3]] }
	],
	[#Rule 40
		 'spec', 3,
sub
#line 122 "oop_y.y"
{ ['raw',       '%'.$_[2].' '.$_[3]] }
	],
	[#Rule 41
		 'symbol_name', 1, undef
	],
	[#Rule 42
		 'alias_name', 1, undef
	],
	[#Rule 43
		 'function_name', 1, undef
	],
	[#Rule 44
		 'ruledef_op', 1, undef
	],
	[#Rule 45
		 'ruledef_op', 1, undef
	]
],
                                  @_);
    bless($self,$class);
}

#line 130 "oop_y.y"


1;
