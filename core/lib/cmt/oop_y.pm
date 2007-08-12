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
		DEFAULT => -29
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
		DEFAULT => -30
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
		DEFAULT => -31
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
		DEFAULT => -34
	},
	{#State 13
		DEFAULT => -35
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
		DEFAULT => -20
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
			"<" => -33,
			"=" => -32
		},
		DEFAULT => -31
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
		DEFAULT => -19
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
			"{" => 22
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
		DEFAULT => -21
	},
	{#State 28
		ACTIONS => {
			'_string' => 18,
			"?" => 40,
			";" => 38,
			"+" => 37,
			'_id' => 21,
			"{" => 22,
			'_char' => 24,
			"(" => 26,
			"|" => 41,
			'_rw_cntl' => 27,
			"*" => 39,
			'_code' => 31
		},
		GOTOS => {
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 42,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 29
		ACTIONS => {
			"<" => 43
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
			'rule_exp' => 44,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 35
		ACTIONS => {
			'_string' => 18,
			"?" => 40,
			"+" => 37,
			'_id' => 21,
			"{" => 22,
			"," => 45,
			'_char' => 24,
			"|" => 41,
			"(" => 26,
			'_rw_cntl' => 27,
			"*" => 39,
			'_code' => 31
		},
		GOTOS => {
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 42,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 36
		ACTIONS => {
			'_string' => 18,
			"?" => 40,
			"+" => 37,
			'_id' => 21,
			"{" => 22,
			'_char' => 24,
			"|" => 41,
			"(" => 26,
			'_rw_cntl' => 27,
			"*" => 39,
			'_code' => 31,
			")" => 46
		},
		GOTOS => {
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 42,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 37
		DEFAULT => -24
	},
	{#State 38
		DEFAULT => -8
	},
	{#State 39
		DEFAULT => -23
	},
	{#State 40
		DEFAULT => -22
	},
	{#State 41
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
			'rule_exp' => 47,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 42
		ACTIONS => {
			'_string' => 18,
			"?" => 40,
			"+" => 37,
			'_id' => 21,
			"{" => 22,
			'_char' => 24,
			"(" => 26,
			'_rw_cntl' => 27,
			"*" => 39,
			'_code' => 31
		},
		DEFAULT => -17,
		GOTOS => {
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 42,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
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
			'explist' => 48,
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 49,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 44
		ACTIONS => {
			'_string' => 18,
			"?" => 40,
			"+" => 37,
			'_id' => 21,
			"{" => 22,
			'_char' => 24,
			"(" => 26,
			'_rw_cntl' => 27,
			"*" => 39,
			'_code' => 31
		},
		DEFAULT => -16,
		GOTOS => {
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 42,
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
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 50,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 46
		DEFAULT => -15
	},
	{#State 47
		ACTIONS => {
			'_string' => 18,
			"?" => 40,
			"+" => 37,
			'_id' => 21,
			"{" => 22,
			'_char' => 24,
			"(" => 26,
			'_rw_cntl' => 27,
			"*" => 39,
			'_code' => 31
		},
		DEFAULT => -18,
		GOTOS => {
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 42,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 48
		ACTIONS => {
			"," => 51,
			">" => 52
		}
	},
	{#State 49
		ACTIONS => {
			'_string' => 18,
			"?" => 40,
			"+" => 37,
			'_id' => 21,
			"{" => 22,
			'_char' => 24,
			"|" => 41,
			"(" => 26,
			'_rw_cntl' => 27,
			"*" => 39,
			'_code' => 31
		},
		DEFAULT => -27,
		GOTOS => {
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 42,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 50
		ACTIONS => {
			'_string' => 18,
			"}" => 53,
			"?" => 40,
			"+" => 37,
			'_id' => 21,
			"{" => 22,
			'_char' => 24,
			"|" => 41,
			"(" => 26,
			'_rw_cntl' => 27,
			"*" => 39,
			'_code' => 31
		},
		GOTOS => {
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 42,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 51
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
			'rule_exp' => 54,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
	},
	{#State 52
		DEFAULT => -26
	},
	{#State 53
		DEFAULT => -25
	},
	{#State 54
		ACTIONS => {
			'_string' => 18,
			"?" => 40,
			"+" => 37,
			'_id' => 21,
			"{" => 22,
			'_char' => 24,
			"|" => 41,
			"(" => 26,
			'_rw_cntl' => 27,
			"*" => 39,
			'_code' => 31
		},
		DEFAULT => -28,
		GOTOS => {
			'quantifiers' => 25,
			'alias_name' => 19,
			'symbol_name' => 20,
			'rule_exp' => 42,
			'function_name' => 29,
			'rw_exp' => 30,
			'call' => 32,
			'term' => 23
		}
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
#line 18 "oop_y.y"
{ { 'header' => $_[1],
                                        'rules'  => $_[2],
                                        'footer' => $_[3], } }
	],
	[#Rule 2
		 'header', 2,
sub
#line 24 "oop_y.y"
{ $_[1] }
	],
	[#Rule 3
		 'footer', 0, undef
	],
	[#Rule 4
		 'footer', 1,
sub
#line 28 "oop_y.y"
{ '' }
	],
	[#Rule 5
		 'footer', 2,
sub
#line 29 "oop_y.y"
{ $_[2] }
	],
	[#Rule 6
		 'rules', 1, undef
	],
	[#Rule 7
		 'rules', 2,
sub
#line 34 "oop_y.y"
{ my $h = {%{$_[1]}, %{$_[2]}} }
	],
	[#Rule 8
		 'rule', 4,
sub
#line 39 "oop_y.y"
{ my $h = { $_[1] => $_[3] } }
	],
	[#Rule 9
		 'rule_exp', 1, undef
	],
	[#Rule 10
		 'rule_exp', 1,
sub
#line 44 "oop_y.y"
{ ['ref',       $_[1]] }
	],
	[#Rule 11
		 'rule_exp', 1, undef
	],
	[#Rule 12
		 'rule_exp', 1,
sub
#line 46 "oop_y.y"
{ ['code',      $_[1]] }
	],
	[#Rule 13
		 'rule_exp', 1, undef
	],
	[#Rule 14
		 'rule_exp', 1, undef
	],
	[#Rule 15
		 'rule_exp', 3,
sub
#line 49 "oop_y.y"
{ ['group',     $_[2]] }
	],
	[#Rule 16
		 'rule_exp', 3,
sub
#line 50 "oop_y.y"
{ ['alias',     $_[1], $_[3]] }
	],
	[#Rule 17
		 'rule_exp', 2,
sub
#line 51 "oop_y.y"
{ $_[1]->[0] eq 'concat'
                                        ? [@{$_[1]}, $_[2]]
                                        : ['concat',$_[1], $_[2]] }
	],
	[#Rule 18
		 'rule_exp', 3,
sub
#line 54 "oop_y.y"
{ $_[1]->[0] eq 'or'
                                        ? [@{$_[1]}, $_[3]]
                                        : ['or',    $_[1], $_[3]] }
	],
	[#Rule 19
		 'term', 1,
sub
#line 60 "oop_y.y"
{ ['char',      $_[1]] }
	],
	[#Rule 20
		 'term', 1,
sub
#line 61 "oop_y.y"
{ ['string',    $_[1]] }
	],
	[#Rule 21
		 'rw_exp', 1,
sub
#line 65 "oop_y.y"
{ ['rw_cntl',   $_[1]] }
	],
	[#Rule 22
		 'quantifiers', 2,
sub
#line 69 "oop_y.y"
{ ['q',         $_[1], 0, 1] }
	],
	[#Rule 23
		 'quantifiers', 2,
sub
#line 70 "oop_y.y"
{ ['q',         $_[1], 0] }
	],
	[#Rule 24
		 'quantifiers', 2,
sub
#line 71 "oop_y.y"
{ ['q',         $_[1], 1] }
	],
	[#Rule 25
		 'quantifiers', 5,
sub
#line 72 "oop_y.y"
{ ['repeat',    $_[2], $_[4]] }
	],
	[#Rule 26
		 'call', 4,
sub
#line 76 "oop_y.y"
{ ['call',      $_[1], @{$_[3]}] }
	],
	[#Rule 27
		 'explist', 1,
sub
#line 80 "oop_y.y"
{ [$_[1]] }
	],
	[#Rule 28
		 'explist', 3,
sub
#line 81 "oop_y.y"
{ [@{$_[1]}, $_[3]] }
	],
	[#Rule 29
		 'text', 1,
sub
#line 85 "oop_y.y"
{ [$_[1]] }
	],
	[#Rule 30
		 'text', 2,
sub
#line 86 "oop_y.y"
{ [@{$_[1]}, $_[2]] }
	],
	[#Rule 31
		 'symbol_name', 1, undef
	],
	[#Rule 32
		 'alias_name', 1, undef
	],
	[#Rule 33
		 'function_name', 1, undef
	],
	[#Rule 34
		 'ruledef_op', 1, undef
	],
	[#Rule 35
		 'ruledef_op', 1, undef
	]
],
                                  @_);
    bless($self,$class);
}

#line 94 "oop_y.y"


1;
