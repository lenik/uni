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

#line 15 "oop_y.y"

    sub _J {
        my $t = shift;
        $t eq $_[0]->[0]
            ? [@{$_[0]}, $_[1]]
            : $t eq $_[1]->[0]
                ? [$t, $_[0], @{$_[1]}[1..$#{$_[1]}]]
                : [$t, $_[0], $_[1]]
    }


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
		GOTOS => {
			'text' => 2,
			'start' => 4,
			'header' => 3
		}
	},
	{#State 1
		DEFAULT => -35
	},
	{#State 2
		ACTIONS => {
			'_string' => 5,
			'_sect_delim' => 6
		}
	},
	{#State 3
		ACTIONS => {
			'_id' => 8
		},
		GOTOS => {
			'symbol_name' => 7,
			'rule' => 9,
			'rules' => 10
		}
	},
	{#State 4
		ACTIONS => {
			'' => 11
		}
	},
	{#State 5
		DEFAULT => -36
	},
	{#State 6
		DEFAULT => -2
	},
	{#State 7
		ACTIONS => {
			":" => 12,
			'_ruledef_op' => 13
		},
		GOTOS => {
			'ruledef_op' => 14
		}
	},
	{#State 8
		DEFAULT => -37
	},
	{#State 9
		DEFAULT => -6
	},
	{#State 10
		ACTIONS => {
			'_id' => 8,
			'_sect_delim' => 15
		},
		DEFAULT => -3,
		GOTOS => {
			'symbol_name' => 7,
			'rule' => 16,
			'footer' => 17
		}
	},
	{#State 11
		DEFAULT => 0
	},
	{#State 12
		DEFAULT => -40
	},
	{#State 13
		DEFAULT => -41
	},
	{#State 14
		ACTIONS => {
			'_char' => 24,
			'_string' => 18,
			"(" => 26,
			'_rw_cntl' => 27,
			'_id' => 21,
			'_code' => 31,
			"{" => 22
		},
		GOTOS => {
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 28,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 15
		ACTIONS => {
			'_string' => 1
		},
		DEFAULT => -4,
		GOTOS => {
			'text' => 33
		}
	},
	{#State 16
		DEFAULT => -7
	},
	{#State 17
		DEFAULT => -1
	},
	{#State 18
		DEFAULT => -21
	},
	{#State 19
		ACTIONS => {
			"=" => 34
		}
	},
	{#State 20
		DEFAULT => -10
	},
	{#State 21
		ACTIONS => {
			"<" => -39,
			"=" => -38
		},
		DEFAULT => -37
	},
	{#State 22
		ACTIONS => {
			'_char' => 24,
			'_string' => 18,
			"(" => 26,
			'_rw_cntl' => 27,
			'_id' => 21,
			'_code' => 31,
			"{" => 22
		},
		GOTOS => {
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 35,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 23
		DEFAULT => -9
	},
	{#State 24
		DEFAULT => -20
	},
	{#State 25
		DEFAULT => -13
	},
	{#State 26
		ACTIONS => {
			'_char' => 24,
			'_string' => 18,
			"(" => 26,
			'_rw_cntl' => 27,
			'_id' => 21,
			'_code' => 31,
			"{" => 22,
			")" => 37
		},
		GOTOS => {
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 36,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 27
		DEFAULT => -22
	},
	{#State 28
		ACTIONS => {
			'_string' => 18,
			"?" => 41,
			";" => 39,
			"+" => 38,
			'_id' => 21,
			"{" => 42,
			'_char' => 24,
			"(" => 26,
			"|" => 43,
			'_rw_cntl' => 27,
			"*" => 40,
			'_code' => 31
		},
		GOTOS => {
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 44,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 29
		ACTIONS => {
			"<" => 45
		}
	},
	{#State 30
		DEFAULT => -11
	},
	{#State 31
		DEFAULT => -12
	},
	{#State 32
		DEFAULT => -14
	},
	{#State 33
		ACTIONS => {
			'_string' => 5
		},
		DEFAULT => -5
	},
	{#State 34
		ACTIONS => {
			'_char' => 24,
			'_string' => 18,
			"(" => 26,
			'_rw_cntl' => 27,
			'_id' => 21,
			'_code' => 31,
			"{" => 22
		},
		GOTOS => {
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 46,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 35
		ACTIONS => {
			'_string' => 18,
			"?" => 41,
			"+" => 38,
			'_id' => 21,
			"{" => 42,
			"," => 47,
			'_char' => 24,
			"|" => 43,
			"(" => 26,
			'_rw_cntl' => 27,
			"*" => 40,
			'_code' => 31
		},
		GOTOS => {
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 44,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 36
		ACTIONS => {
			'_string' => 18,
			"?" => 41,
			"+" => 38,
			'_id' => 21,
			"{" => 42,
			'_char' => 24,
			"|" => 43,
			"(" => 26,
			'_rw_cntl' => 27,
			"*" => 40,
			'_code' => 31,
			")" => 48
		},
		GOTOS => {
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 44,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 37
		DEFAULT => -15
	},
	{#State 38
		DEFAULT => -25
	},
	{#State 39
		DEFAULT => -8
	},
	{#State 40
		DEFAULT => -24
	},
	{#State 41
		DEFAULT => -23
	},
	{#State 42
		ACTIONS => {
			'_string' => 18,
			'_id' => 21,
			"{" => 22,
			'_number' => 50,
			'_char' => 24,
			"(" => 26,
			'_rw_cntl' => 27,
			'_code' => 31,
			"." => 51
		},
		GOTOS => {
			'alias_name' => 19,
			'symbol_name' => 20,
			'range' => 49,
			'term' => 23,
			'quantifiers' => 25,
			'rule_exp' => 35,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32
		}
	},
	{#State 43
		ACTIONS => {
			'_char' => 24,
			'_string' => 18,
			"(" => 26,
			'_rw_cntl' => 27,
			'_id' => 21,
			'_code' => 31,
			"{" => 22
		},
		GOTOS => {
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 52,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 44
		ACTIONS => {
			'_string' => 18,
			"?" => 41,
			"+" => 38,
			'_id' => 21,
			"{" => 42,
			'_char' => 24,
			"(" => 26,
			'_rw_cntl' => 27,
			"*" => 40,
			'_code' => 31
		},
		DEFAULT => -18,
		GOTOS => {
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 44,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 45
		ACTIONS => {
			'_char' => 24,
			'_string' => 18,
			"(" => 26,
			'_rw_cntl' => 27,
			'_id' => 21,
			'_code' => 31,
			"{" => 22
		},
		GOTOS => {
			'explist' => 53,
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 54,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 46
		ACTIONS => {
			'_string' => 18,
			"?" => 41,
			"+" => 38,
			'_id' => 21,
			"{" => 42,
			'_char' => 24,
			"(" => 26,
			'_rw_cntl' => 27,
			"*" => 40,
			'_code' => 31
		},
		DEFAULT => -17,
		GOTOS => {
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 44,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 47
		ACTIONS => {
			'_char' => 24,
			'_string' => 18,
			"(" => 26,
			'_rw_cntl' => 27,
			'_id' => 21,
			'_code' => 31,
			"{" => 22
		},
		GOTOS => {
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 55,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 48
		DEFAULT => -16
	},
	{#State 49
		ACTIONS => {
			"}" => 56
		}
	},
	{#State 50
		ACTIONS => {
			"." => 57
		},
		DEFAULT => -28
	},
	{#State 51
		ACTIONS => {
			"." => 58
		}
	},
	{#State 52
		ACTIONS => {
			'_string' => 18,
			"?" => 41,
			"+" => 38,
			'_id' => 21,
			"{" => 42,
			'_char' => 24,
			"(" => 26,
			'_rw_cntl' => 27,
			"*" => 40,
			'_code' => 31
		},
		DEFAULT => -19,
		GOTOS => {
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 44,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 53
		ACTIONS => {
			"," => 59,
			">" => 60
		}
	},
	{#State 54
		ACTIONS => {
			'_string' => 18,
			"?" => 41,
			"+" => 38,
			'_id' => 21,
			"{" => 42,
			'_char' => 24,
			"|" => 43,
			"(" => 26,
			'_rw_cntl' => 27,
			"*" => 40,
			'_code' => 31
		},
		DEFAULT => -33,
		GOTOS => {
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 44,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 55
		ACTIONS => {
			'_string' => 18,
			"}" => 61,
			"?" => 41,
			"+" => 38,
			'_id' => 21,
			"{" => 42,
			'_char' => 24,
			"|" => 43,
			"(" => 26,
			'_rw_cntl' => 27,
			"*" => 40,
			'_code' => 31
		},
		GOTOS => {
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 44,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 56
		DEFAULT => -26
	},
	{#State 57
		ACTIONS => {
			"." => 62
		}
	},
	{#State 58
		ACTIONS => {
			'_number' => 63
		}
	},
	{#State 59
		ACTIONS => {
			'_char' => 24,
			'_string' => 18,
			"(" => 26,
			'_rw_cntl' => 27,
			'_id' => 21,
			'_code' => 31,
			"{" => 22
		},
		GOTOS => {
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 64,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 60
		DEFAULT => -32
	},
	{#State 61
		DEFAULT => -27
	},
	{#State 62
		ACTIONS => {
			'_number' => 65
		},
		DEFAULT => -29
	},
	{#State 63
		DEFAULT => -30
	},
	{#State 64
		ACTIONS => {
			'_string' => 18,
			"?" => 41,
			"+" => 38,
			'_id' => 21,
			"{" => 42,
			'_char' => 24,
			"|" => 43,
			"(" => 26,
			'_rw_cntl' => 27,
			"*" => 40,
			'_code' => 31
		},
		DEFAULT => -34,
		GOTOS => {
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 44,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 65
		DEFAULT => -31
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
#line 28 "oop_y.y"
{ { 'header' => $_[1],
                                        'rules'  => $_[2],
                                        'footer' => $_[3], } }
	],
	[#Rule 2
		 'header', 2,
sub
#line 34 "oop_y.y"
{ $_[1] }
	],
	[#Rule 3
		 'footer', 0, undef
	],
	[#Rule 4
		 'footer', 1,
sub
#line 38 "oop_y.y"
{ '' }
	],
	[#Rule 5
		 'footer', 2,
sub
#line 39 "oop_y.y"
{ $_[2] }
	],
	[#Rule 6
		 'rules', 1, undef
	],
	[#Rule 7
		 'rules', 2,
sub
#line 44 "oop_y.y"
{ my $h = {%{$_[1]}, %{$_[2]}} }
	],
	[#Rule 8
		 'rule', 4,
sub
#line 49 "oop_y.y"
{ my $h = { $_[1] => $_[3] } }
	],
	[#Rule 9
		 'rule_exp', 1, undef
	],
	[#Rule 10
		 'rule_exp', 1,
sub
#line 54 "oop_y.y"
{ ['ref',       $_[1]] }
	],
	[#Rule 11
		 'rule_exp', 1, undef
	],
	[#Rule 12
		 'rule_exp', 1,
sub
#line 56 "oop_y.y"
{ ['code',      $_[1]] }
	],
	[#Rule 13
		 'rule_exp', 1, undef
	],
	[#Rule 14
		 'rule_exp', 1, undef
	],
	[#Rule 15
		 'rule_exp', 2,
sub
#line 59 "oop_y.y"
{ ['empty'] }
	],
	[#Rule 16
		 'rule_exp', 3,
sub
#line 60 "oop_y.y"
{ ['group',     $_[2]] }
	],
	[#Rule 17
		 'rule_exp', 3,
sub
#line 61 "oop_y.y"
{ ['alias',     $_[1], $_[3]] }
	],
	[#Rule 18
		 'rule_exp', 2,
sub
#line 62 "oop_y.y"
{ _J('concat',  $_[1], $_[2]) }
	],
	[#Rule 19
		 'rule_exp', 3,
sub
#line 63 "oop_y.y"
{ _J('or',      $_[1], $_[3]) }
	],
	[#Rule 20
		 'term', 1,
sub
#line 67 "oop_y.y"
{ ['char',      $_[1]] }
	],
	[#Rule 21
		 'term', 1,
sub
#line 68 "oop_y.y"
{ ['string',    $_[1]] }
	],
	[#Rule 22
		 'rw_exp', 1,
sub
#line 72 "oop_y.y"
{ ['rw_cntl',   $_[1]] }
	],
	[#Rule 23
		 'quantifiers', 2,
sub
#line 76 "oop_y.y"
{ ['q',         $_[1], 0, 1] }
	],
	[#Rule 24
		 'quantifiers', 2,
sub
#line 77 "oop_y.y"
{ ['q',         $_[1], 0] }
	],
	[#Rule 25
		 'quantifiers', 2,
sub
#line 78 "oop_y.y"
{ ['q',         $_[1], 1] }
	],
	[#Rule 26
		 'quantifiers', 4,
sub
#line 79 "oop_y.y"
{ ['q',         $_[1], @{$_[3]}] }
	],
	[#Rule 27
		 'quantifiers', 5,
sub
#line 80 "oop_y.y"
{ ['repeat',    $_[2], $_[4]] }
	],
	[#Rule 28
		 'range', 1,
sub
#line 84 "oop_y.y"
{ [ $_[1], $_[1] ] }
	],
	[#Rule 29
		 'range', 3,
sub
#line 85 "oop_y.y"
{ [ $_[1], ] }
	],
	[#Rule 30
		 'range', 3,
sub
#line 86 "oop_y.y"
{ [ 0, $_[3] ] }
	],
	[#Rule 31
		 'range', 4,
sub
#line 87 "oop_y.y"
{ [ $_[1], $_[4] ] }
	],
	[#Rule 32
		 'call', 4,
sub
#line 91 "oop_y.y"
{ ['call',      $_[1], @{$_[3]}] }
	],
	[#Rule 33
		 'explist', 1,
sub
#line 95 "oop_y.y"
{ [$_[1]] }
	],
	[#Rule 34
		 'explist', 3,
sub
#line 96 "oop_y.y"
{ [@{$_[1]}, $_[3]] }
	],
	[#Rule 35
		 'text', 1,
sub
#line 100 "oop_y.y"
{ [$_[1]] }
	],
	[#Rule 36
		 'text', 2,
sub
#line 101 "oop_y.y"
{ [@{$_[1]}, $_[2]] }
	],
	[#Rule 37
		 'symbol_name', 1, undef
	],
	[#Rule 38
		 'alias_name', 1, undef
	],
	[#Rule 39
		 'function_name', 1, undef
	],
	[#Rule 40
		 'ruledef_op', 1, undef
	],
	[#Rule 41
		 'ruledef_op', 1, undef
	]
],
                                  @_);
    bless($self,$class);
}

#line 109 "oop_y.y"


1;
