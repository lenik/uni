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

#line 21 "oop_y.y"

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
		DEFAULT => -46
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
		DEFAULT => -49
	},
	{#State 14
		DEFAULT => -50
	},
	{#State 15
		ACTIONS => {
			'_string' => 27,
			'_id' => 21,
			"{" => 29,
			"%" => 22,
			'_char' => 34,
			"(" => 35,
			'_rw_cntl' => 36,
			'_code' => 38
		},
		DEFAULT => -13,
		GOTOS => {
			'ruleexp' => 26,
			'alias_name' => 19,
			'spec' => 28,
			'symbol_name' => 20,
			'concat' => 30,
			'ruledefs' => 31,
			'ruledef' => 32,
			'term' => 23,
			'quantifiers' => 33,
			'nonempty' => 24,
			'function_name' => 37,
			'rw_exp' => 39,
			'call' => 25
		}
	},
	{#State 16
		ACTIONS => {
			'_string' => 1
		},
		DEFAULT => -4,
		GOTOS => {
			'text' => 40,
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
		ACTIONS => {
			"=" => 41
		}
	},
	{#State 20
		DEFAULT => -20
	},
	{#State 21
		ACTIONS => {
			"<" => -48,
			"=" => -47
		},
		DEFAULT => -46
	},
	{#State 22
		ACTIONS => {
			'_id' => 42
		}
	},
	{#State 23
		DEFAULT => -19
	},
	{#State 24
		DEFAULT => -14
	},
	{#State 25
		DEFAULT => -23
	},
	{#State 26
		ACTIONS => {
			"+" => 43,
			'_id' => 21,
			"%" => 22,
			"*" => 44,
			'_string' => 27,
			"?" => 46,
			"{" => 47,
			"/" => 48,
			'_char' => 34,
			"(" => 35,
			'_rw_cntl' => 36,
			'_code' => 38
		},
		DEFAULT => -15,
		GOTOS => {
			'quantifiers' => 33,
			'ruleexp' => 45,
			'alias_name' => 19,
			'spec' => 28,
			'symbol_name' => 20,
			'function_name' => 37,
			'rw_exp' => 39,
			'call' => 25,
			'term' => 23
		}
	},
	{#State 27
		DEFAULT => -30
	},
	{#State 28
		DEFAULT => -28
	},
	{#State 29
		ACTIONS => {
			'_string' => 27,
			'_id' => 21,
			"{" => 29,
			"%" => 22,
			'_char' => 34,
			"(" => 35,
			'_rw_cntl' => 36,
			'_code' => 38
		},
		DEFAULT => -13,
		GOTOS => {
			'ruleexp' => 26,
			'alias_name' => 19,
			'spec' => 28,
			'symbol_name' => 20,
			'concat' => 30,
			'ruledefs' => 49,
			'ruledef' => 32,
			'term' => 23,
			'quantifiers' => 33,
			'nonempty' => 24,
			'function_name' => 37,
			'rw_exp' => 39,
			'call' => 25
		}
	},
	{#State 30
		ACTIONS => {
			'_string' => 27,
			'_id' => 21,
			"{" => 29,
			"%" => 22,
			'_char' => 34,
			"(" => 35,
			'_rw_cntl' => 36,
			'_code' => 38
		},
		DEFAULT => -16,
		GOTOS => {
			'quantifiers' => 33,
			'ruleexp' => 50,
			'alias_name' => 19,
			'spec' => 28,
			'symbol_name' => 20,
			'function_name' => 37,
			'rw_exp' => 39,
			'call' => 25,
			'term' => 23
		}
	},
	{#State 31
		ACTIONS => {
			"|" => 52,
			";" => 51
		}
	},
	{#State 32
		DEFAULT => -11
	},
	{#State 33
		DEFAULT => -22
	},
	{#State 34
		DEFAULT => -29
	},
	{#State 35
		ACTIONS => {
			'_string' => 27,
			'_id' => 21,
			"{" => 29,
			"%" => 22,
			'_char' => 34,
			"(" => 35,
			'_rw_cntl' => 36,
			'_code' => 38
		},
		DEFAULT => -13,
		GOTOS => {
			'ruleexp' => 26,
			'alias_name' => 19,
			'spec' => 28,
			'symbol_name' => 20,
			'concat' => 30,
			'ruledefs' => 53,
			'ruledef' => 32,
			'term' => 23,
			'quantifiers' => 33,
			'nonempty' => 24,
			'function_name' => 37,
			'rw_exp' => 39,
			'call' => 25
		}
	},
	{#State 36
		DEFAULT => -43
	},
	{#State 37
		ACTIONS => {
			"<" => 54
		}
	},
	{#State 38
		DEFAULT => -21
	},
	{#State 39
		DEFAULT => -27
	},
	{#State 40
		DEFAULT => -3
	},
	{#State 41
		ACTIONS => {
			'_string' => 27,
			'_id' => 21,
			"{" => 29,
			"%" => 22,
			'_char' => 34,
			"(" => 35,
			'_rw_cntl' => 36,
			'_code' => 38
		},
		GOTOS => {
			'quantifiers' => 33,
			'ruleexp' => 55,
			'alias_name' => 19,
			'spec' => 28,
			'symbol_name' => 20,
			'function_name' => 37,
			'rw_exp' => 39,
			'call' => 25,
			'term' => 23
		}
	},
	{#State 42
		ACTIONS => {
			'_char' => 57,
			'_id' => 56
		}
	},
	{#State 43
		DEFAULT => -33
	},
	{#State 44
		DEFAULT => -32
	},
	{#State 45
		ACTIONS => {
			"+" => 43,
			"*" => 44,
			"?" => 46,
			"{" => 58,
			"/" => 48
		},
		DEFAULT => -17
	},
	{#State 46
		DEFAULT => -31
	},
	{#State 47
		ACTIONS => {
			'_string' => 27,
			'_id' => 21,
			"{" => 29,
			"%" => 22,
			'_number' => 60,
			'_char' => 34,
			"(" => 35,
			'_rw_cntl' => 36,
			'_code' => 38,
			"." => 61
		},
		DEFAULT => -13,
		GOTOS => {
			'ruleexp' => 26,
			'alias_name' => 19,
			'spec' => 28,
			'symbol_name' => 20,
			'concat' => 30,
			'range' => 59,
			'ruledefs' => 49,
			'ruledef' => 32,
			'term' => 23,
			'quantifiers' => 33,
			'nonempty' => 24,
			'function_name' => 37,
			'rw_exp' => 39,
			'call' => 25
		}
	},
	{#State 48
		ACTIONS => {
			'_id' => 63
		},
		GOTOS => {
			'alias_name' => 62
		}
	},
	{#State 49
		ACTIONS => {
			"|" => 52,
			"," => 64
		}
	},
	{#State 50
		ACTIONS => {
			"+" => 43,
			"*" => 44,
			"?" => 46,
			"{" => 58,
			"/" => 48
		},
		DEFAULT => -18
	},
	{#State 51
		DEFAULT => -10
	},
	{#State 52
		ACTIONS => {
			'_string' => 27,
			'_id' => 21,
			"{" => 29,
			"%" => 22,
			'_char' => 34,
			"(" => 35,
			'_rw_cntl' => 36,
			'_code' => 38
		},
		DEFAULT => -13,
		GOTOS => {
			'ruleexp' => 26,
			'alias_name' => 19,
			'spec' => 28,
			'symbol_name' => 20,
			'concat' => 30,
			'ruledef' => 65,
			'term' => 23,
			'quantifiers' => 33,
			'nonempty' => 24,
			'function_name' => 37,
			'rw_exp' => 39,
			'call' => 25
		}
	},
	{#State 53
		ACTIONS => {
			"|" => 52,
			")" => 66
		}
	},
	{#State 54
		ACTIONS => {
			'_string' => 27,
			'_id' => 21,
			"{" => 29,
			"%" => 22,
			'_char' => 34,
			"(" => 35,
			'_rw_cntl' => 36,
			'_code' => 38
		},
		GOTOS => {
			'ruleexp' => 67,
			'alias_name' => 19,
			'spec' => 28,
			'symbol_name' => 20,
			'term' => 23,
			'quantifiers' => 33,
			'explist' => 68,
			'function_name' => 37,
			'rw_exp' => 39,
			'call' => 25
		}
	},
	{#State 55
		ACTIONS => {
			"+" => 43,
			"*" => 44,
			"?" => 46,
			"{" => 58
		},
		DEFAULT => -25
	},
	{#State 56
		DEFAULT => -44
	},
	{#State 57
		DEFAULT => -45
	},
	{#State 58
		ACTIONS => {
			"." => 61,
			'_number' => 60
		},
		GOTOS => {
			'range' => 59
		}
	},
	{#State 59
		ACTIONS => {
			"}" => 69
		}
	},
	{#State 60
		ACTIONS => {
			"." => 70
		},
		DEFAULT => -36
	},
	{#State 61
		ACTIONS => {
			"." => 71
		}
	},
	{#State 62
		DEFAULT => -26
	},
	{#State 63
		DEFAULT => -47
	},
	{#State 64
		ACTIONS => {
			'_string' => 27,
			'_id' => 21,
			"{" => 29,
			"%" => 22,
			'_char' => 34,
			"(" => 35,
			'_rw_cntl' => 36,
			'_code' => 38
		},
		GOTOS => {
			'ruleexp' => 26,
			'alias_name' => 19,
			'spec' => 28,
			'symbol_name' => 20,
			'concat' => 30,
			'term' => 23,
			'quantifiers' => 33,
			'nonempty' => 72,
			'function_name' => 37,
			'rw_exp' => 39,
			'call' => 25
		}
	},
	{#State 65
		DEFAULT => -12
	},
	{#State 66
		DEFAULT => -24
	},
	{#State 67
		ACTIONS => {
			"?" => 46,
			"*" => 44,
			"+" => 43,
			"{" => 58,
			"/" => 48
		},
		DEFAULT => -41
	},
	{#State 68
		ACTIONS => {
			"," => 73,
			">" => 74
		}
	},
	{#State 69
		DEFAULT => -34
	},
	{#State 70
		ACTIONS => {
			"." => 75
		}
	},
	{#State 71
		ACTIONS => {
			'_number' => 76
		}
	},
	{#State 72
		ACTIONS => {
			"}" => 77
		}
	},
	{#State 73
		ACTIONS => {
			'_string' => 27,
			'_id' => 21,
			"{" => 29,
			"%" => 22,
			'_char' => 34,
			"(" => 35,
			'_rw_cntl' => 36,
			'_code' => 38
		},
		GOTOS => {
			'quantifiers' => 33,
			'ruleexp' => 78,
			'alias_name' => 19,
			'spec' => 28,
			'symbol_name' => 20,
			'function_name' => 37,
			'rw_exp' => 39,
			'call' => 25,
			'term' => 23
		}
	},
	{#State 74
		DEFAULT => -40
	},
	{#State 75
		ACTIONS => {
			'_number' => 79
		},
		DEFAULT => -37
	},
	{#State 76
		DEFAULT => -38
	},
	{#State 77
		DEFAULT => -35
	},
	{#State 78
		ACTIONS => {
			"?" => 46,
			"*" => 44,
			"+" => 43,
			"{" => 58,
			"/" => 48
		},
		DEFAULT => -42
	},
	{#State 79
		DEFAULT => -39
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
#line 39 "oop_y.y"
{ { 'header' => $_[1],
                                        'rules'  => $_[2],
                                        'footer' => $_[3],
                                        'seq'    => [ @S ],
                                        'seqm'   => _M @S } }
	],
	[#Rule 2
		 'header', 2,
sub
#line 47 "oop_y.y"
{ undef @S; $_[1] }
	],
	[#Rule 3
		 'footer', 2,
sub
#line 51 "oop_y.y"
{ $_[2] }
	],
	[#Rule 4
		 'text', 0,
sub
#line 55 "oop_y.y"
{ '' }
	],
	[#Rule 5
		 'text', 1,
sub
#line 56 "oop_y.y"
{ join('', @{$_[1]}) }
	],
	[#Rule 6
		 'textl', 1,
sub
#line 60 "oop_y.y"
{ [$_[1]] }
	],
	[#Rule 7
		 'textl', 2,
sub
#line 61 "oop_y.y"
{ [@{$_[1]}, $_[2]] }
	],
	[#Rule 8
		 'rules', 1,
sub
#line 65 "oop_y.y"
{ _H { _S($_[1]->[0]) => $_[1]->[1] } }
	],
	[#Rule 9
		 'rules', 2,
sub
#line 66 "oop_y.y"
{ $_[1]->{_S($_[2]->[0])} = $_[2]->[1];
                                      $_[1] }
	],
	[#Rule 10
		 'rule', 4,
sub
#line 72 "oop_y.y"
{ [ $_[1], $_[3] ] }
	],
	[#Rule 11
		 'ruledefs', 1, undef
	],
	[#Rule 12
		 'ruledefs', 3,
sub
#line 77 "oop_y.y"
{ _J('or',      $_[1], $_[3]) }
	],
	[#Rule 13
		 'ruledef', 0,
sub
#line 80 "oop_y.y"
{ ['empty'] }
	],
	[#Rule 14
		 'ruledef', 1, undef
	],
	[#Rule 15
		 'nonempty', 1, undef
	],
	[#Rule 16
		 'nonempty', 1,
sub
#line 86 "oop_y.y"
{ ['concat',    @{$_[1]}] }
	],
	[#Rule 17
		 'concat', 2,
sub
#line 90 "oop_y.y"
{ [ $_[1], $_[2] ] }
	],
	[#Rule 18
		 'concat', 2,
sub
#line 91 "oop_y.y"
{ [ @{$_[1]}, $_[2] ] }
	],
	[#Rule 19
		 'ruleexp', 1, undef
	],
	[#Rule 20
		 'ruleexp', 1,
sub
#line 96 "oop_y.y"
{ ['ref',       $_[1]] }
	],
	[#Rule 21
		 'ruleexp', 1,
sub
#line 97 "oop_y.y"
{ ['code',      $_[1]] }
	],
	[#Rule 22
		 'ruleexp', 1, undef
	],
	[#Rule 23
		 'ruleexp', 1, undef
	],
	[#Rule 24
		 'ruleexp', 3,
sub
#line 100 "oop_y.y"
{ ['group',     $_[2]] }
	],
	[#Rule 25
		 'ruleexp', 3,
sub
#line 101 "oop_y.y"
{ ['alias',     $_[1], $_[3]] }
	],
	[#Rule 26
		 'ruleexp', 3,
sub
#line 102 "oop_y.y"
{ ['alias',     $_[3], $_[1]] }
	],
	[#Rule 27
		 'ruleexp', 1, undef
	],
	[#Rule 28
		 'ruleexp', 1, undef
	],
	[#Rule 29
		 'term', 1,
sub
#line 108 "oop_y.y"
{ ['char',      $_[1]] }
	],
	[#Rule 30
		 'term', 1,
sub
#line 109 "oop_y.y"
{ ['string',    $_[1]] }
	],
	[#Rule 31
		 'quantifiers', 2,
sub
#line 113 "oop_y.y"
{ ['qt',        $_[1], 0, 1] }
	],
	[#Rule 32
		 'quantifiers', 2,
sub
#line 114 "oop_y.y"
{ ['qt',        $_[1], 0] }
	],
	[#Rule 33
		 'quantifiers', 2,
sub
#line 115 "oop_y.y"
{ ['qt',        $_[1], 1] }
	],
	[#Rule 34
		 'quantifiers', 4,
sub
#line 116 "oop_y.y"
{ ['qt',        $_[1], @{$_[3]}] }
	],
	[#Rule 35
		 'quantifiers', 5,
sub
#line 118 "oop_y.y"
{ ['repeat',    $_[2], $_[4]] }
	],
	[#Rule 36
		 'range', 1,
sub
#line 122 "oop_y.y"
{ [ $_[1], $_[1] ] }
	],
	[#Rule 37
		 'range', 3,
sub
#line 123 "oop_y.y"
{ [ $_[1], ] }
	],
	[#Rule 38
		 'range', 3,
sub
#line 124 "oop_y.y"
{ [ 0, $_[3] ] }
	],
	[#Rule 39
		 'range', 4,
sub
#line 125 "oop_y.y"
{ [ $_[1], $_[4] ] }
	],
	[#Rule 40
		 'call', 4,
sub
#line 129 "oop_y.y"
{ ['call',      $_[1], @{$_[3]}] }
	],
	[#Rule 41
		 'explist', 1,
sub
#line 133 "oop_y.y"
{ [$_[1]] }
	],
	[#Rule 42
		 'explist', 3,
sub
#line 134 "oop_y.y"
{ [@{$_[1]}, $_[3]] }
	],
	[#Rule 43
		 'rw_exp', 1,
sub
#line 138 "oop_y.y"
{ ['rw_cntl',   $_[1]] }
	],
	[#Rule 44
		 'spec', 3,
sub
#line 142 "oop_y.y"
{ ['raw',       '%'.$_[2].' '.$_[3]] }
	],
	[#Rule 45
		 'spec', 3,
sub
#line 143 "oop_y.y"
{ ['raw',       '%'.$_[2]." '$_[3]'"] }
	],
	[#Rule 46
		 'symbol_name', 1, undef
	],
	[#Rule 47
		 'alias_name', 1, undef
	],
	[#Rule 48
		 'function_name', 1, undef
	],
	[#Rule 49
		 'ruledef_op', 1, undef
	],
	[#Rule 50
		 'ruledef_op', 1, undef
	]
],
                                  @_);
    bless($self,$class);
}

#line 151 "oop_y.y"


1;
