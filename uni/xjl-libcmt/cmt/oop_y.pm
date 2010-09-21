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

#line 22 "oop_y.y"

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
            '_id' => 9,
            "%" => 10
        },
        GOTOS => {
            'rule_name' => 8,
            'rule' => 11,
            'rules' => 12
        }
    },
    {#State 5
        ACTIONS => {
            '' => 13
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
            ":" => 14,
            '_ruledef_op' => 15
        },
        GOTOS => {
            'ruledef_op' => 16
        }
    },
    {#State 9
        DEFAULT => -48
    },
    {#State 10
        ACTIONS => {
            '_id' => 17
        }
    },
    {#State 11
        DEFAULT => -8
    },
    {#State 12
        ACTIONS => {
            '_id' => 9,
            '_sect_delim' => 18,
            "%" => 10
        },
        GOTOS => {
            'rule_name' => 8,
            'rule' => 19,
            'footer' => 20
        }
    },
    {#State 13
        DEFAULT => 0
    },
    {#State 14
        DEFAULT => -53
    },
    {#State 15
        DEFAULT => -54
    },
    {#State 16
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
        DEFAULT => -14,
        GOTOS => {
            'ruleexp' => 26,
            'spec' => 28,
            'concat' => 30,
            'ruledefs' => 31,
            'ruledef' => 32,
            'term' => 23,
            'quantifiers' => 33,
            'nonempty' => 24,
            'function_name' => 37,
            'rw_exp' => 39,
            'call' => 25,
            'alias' => 40
        }
    },
    {#State 17
        ACTIONS => {
            ":" => 14,
            '_ruledef_op' => 15
        },
        GOTOS => {
            'ruledef_op' => 41
        }
    },
    {#State 18
        ACTIONS => {
            '_string' => 1
        },
        DEFAULT => -4,
        GOTOS => {
            'text' => 42,
            'textl' => 3
        }
    },
    {#State 19
        DEFAULT => -9
    },
    {#State 20
        DEFAULT => -1
    },
    {#State 21
        ACTIONS => {
            "<" => -52,
            "=" => -51
        },
        DEFAULT => -22
    },
    {#State 22
        ACTIONS => {
            '_id' => 43
        }
    },
    {#State 23
        DEFAULT => -21
    },
    {#State 24
        ACTIONS => {
            ":" => 44
        },
        DEFAULT => -15
    },
    {#State 25
        DEFAULT => -25
    },
    {#State 26
        ACTIONS => {
            "+" => 45,
            '_id' => 21,
            "%" => 22,
            "*" => 46,
            '_string' => 27,
            "?" => 48,
            "{" => 49,
            "/" => 50,
            '_char' => 34,
            "(" => 35,
            '_rw_cntl' => 36,
            '_code' => 38
        },
        DEFAULT => -17,
        GOTOS => {
            'quantifiers' => 33,
            'ruleexp' => 47,
            'spec' => 28,
            'function_name' => 37,
            'rw_exp' => 39,
            'call' => 25,
            'alias' => 40,
            'term' => 23
        }
    },
    {#State 27
        DEFAULT => -32
    },
    {#State 28
        DEFAULT => -30
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
        DEFAULT => -14,
        GOTOS => {
            'ruleexp' => 26,
            'spec' => 28,
            'concat' => 30,
            'ruledefs' => 51,
            'ruledef' => 32,
            'term' => 23,
            'quantifiers' => 33,
            'nonempty' => 24,
            'function_name' => 37,
            'rw_exp' => 39,
            'call' => 25,
            'alias' => 40
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
        DEFAULT => -18,
        GOTOS => {
            'quantifiers' => 33,
            'ruleexp' => 52,
            'spec' => 28,
            'function_name' => 37,
            'rw_exp' => 39,
            'call' => 25,
            'alias' => 40,
            'term' => 23
        }
    },
    {#State 31
        ACTIONS => {
            "|" => 54,
            ";" => 53
        }
    },
    {#State 32
        DEFAULT => -12
    },
    {#State 33
        DEFAULT => -24
    },
    {#State 34
        DEFAULT => -31
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
        DEFAULT => -14,
        GOTOS => {
            'ruleexp' => 26,
            'spec' => 28,
            'concat' => 30,
            'ruledefs' => 55,
            'ruledef' => 32,
            'term' => 23,
            'quantifiers' => 33,
            'nonempty' => 24,
            'function_name' => 37,
            'rw_exp' => 39,
            'call' => 25,
            'alias' => 40
        }
    },
    {#State 36
        DEFAULT => -45
    },
    {#State 37
        ACTIONS => {
            "<" => 56
        }
    },
    {#State 38
        DEFAULT => -23
    },
    {#State 39
        DEFAULT => -29
    },
    {#State 40
        ACTIONS => {
            "=" => 57
        }
    },
    {#State 41
        ACTIONS => {
            '_code' => 58
        }
    },
    {#State 42
        DEFAULT => -3
    },
    {#State 43
        ACTIONS => {
            '_char' => 60,
            '_id' => 59
        }
    },
    {#State 44
        ACTIONS => {
            '_id' => 61,
            '_number' => 62
        },
        GOTOS => {
            'rule_alias' => 63
        }
    },
    {#State 45
        DEFAULT => -35
    },
    {#State 46
        DEFAULT => -34
    },
    {#State 47
        ACTIONS => {
            "+" => 45,
            "*" => 46,
            "?" => 48,
            "{" => 64,
            "/" => 50
        },
        DEFAULT => -19
    },
    {#State 48
        DEFAULT => -33
    },
    {#State 49
        ACTIONS => {
            '_string' => 27,
            '_id' => 21,
            "{" => 29,
            "%" => 22,
            '_number' => 66,
            '_char' => 34,
            "(" => 35,
            '_rw_cntl' => 36,
            '_code' => 38,
            "." => 67
        },
        DEFAULT => -14,
        GOTOS => {
            'ruleexp' => 26,
            'spec' => 28,
            'concat' => 30,
            'range' => 65,
            'ruledefs' => 51,
            'ruledef' => 32,
            'term' => 23,
            'quantifiers' => 33,
            'nonempty' => 24,
            'function_name' => 37,
            'rw_exp' => 39,
            'call' => 25,
            'alias' => 40
        }
    },
    {#State 50
        ACTIONS => {
            '_id' => 68
        },
        GOTOS => {
            'alias' => 69
        }
    },
    {#State 51
        ACTIONS => {
            "|" => 54,
            "," => 70
        }
    },
    {#State 52
        ACTIONS => {
            "+" => 45,
            "*" => 46,
            "?" => 48,
            "{" => 64,
            "/" => 50
        },
        DEFAULT => -20
    },
    {#State 53
        DEFAULT => -10
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
        DEFAULT => -14,
        GOTOS => {
            'ruleexp' => 26,
            'spec' => 28,
            'concat' => 30,
            'ruledef' => 71,
            'term' => 23,
            'quantifiers' => 33,
            'nonempty' => 24,
            'function_name' => 37,
            'rw_exp' => 39,
            'call' => 25,
            'alias' => 40
        }
    },
    {#State 55
        ACTIONS => {
            "|" => 54,
            ")" => 72
        }
    },
    {#State 56
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
            'explist' => 74,
            'quantifiers' => 33,
            'ruleexp' => 73,
            'spec' => 28,
            'function_name' => 37,
            'rw_exp' => 39,
            'call' => 25,
            'alias' => 40,
            'term' => 23
        }
    },
    {#State 57
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
            'ruleexp' => 75,
            'spec' => 28,
            'function_name' => 37,
            'rw_exp' => 39,
            'call' => 25,
            'alias' => 40,
            'term' => 23
        }
    },
    {#State 58
        DEFAULT => -11
    },
    {#State 59
        DEFAULT => -46
    },
    {#State 60
        DEFAULT => -47
    },
    {#State 61
        DEFAULT => -49
    },
    {#State 62
        DEFAULT => -50
    },
    {#State 63
        DEFAULT => -16
    },
    {#State 64
        ACTIONS => {
            "." => 67,
            '_number' => 66
        },
        GOTOS => {
            'range' => 65
        }
    },
    {#State 65
        ACTIONS => {
            "}" => 76
        }
    },
    {#State 66
        ACTIONS => {
            "." => 77
        },
        DEFAULT => -38
    },
    {#State 67
        ACTIONS => {
            "." => 78
        }
    },
    {#State 68
        DEFAULT => -51
    },
    {#State 69
        DEFAULT => -28
    },
    {#State 70
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
            'spec' => 28,
            'concat' => 30,
            'term' => 23,
            'quantifiers' => 33,
            'nonempty' => 79,
            'function_name' => 37,
            'rw_exp' => 39,
            'call' => 25,
            'alias' => 40
        }
    },
    {#State 71
        DEFAULT => -13
    },
    {#State 72
        DEFAULT => -26
    },
    {#State 73
        ACTIONS => {
            "?" => 48,
            "*" => 46,
            "+" => 45,
            "{" => 64,
            "/" => 50
        },
        DEFAULT => -43
    },
    {#State 74
        ACTIONS => {
            "," => 80,
            ">" => 81
        }
    },
    {#State 75
        ACTIONS => {
            "+" => 45,
            "*" => 46,
            "?" => 48,
            "{" => 64
        },
        DEFAULT => -27
    },
    {#State 76
        DEFAULT => -36
    },
    {#State 77
        ACTIONS => {
            "." => 82
        }
    },
    {#State 78
        ACTIONS => {
            '_number' => 83
        }
    },
    {#State 79
        ACTIONS => {
            "}" => 84
        }
    },
    {#State 80
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
            'ruleexp' => 85,
            'spec' => 28,
            'function_name' => 37,
            'rw_exp' => 39,
            'call' => 25,
            'alias' => 40,
            'term' => 23
        }
    },
    {#State 81
        DEFAULT => -42
    },
    {#State 82
        ACTIONS => {
            '_number' => 86
        },
        DEFAULT => -39
    },
    {#State 83
        DEFAULT => -40
    },
    {#State 84
        DEFAULT => -37
    },
    {#State 85
        ACTIONS => {
            "?" => 48,
            "*" => 46,
            "+" => 45,
            "{" => 64,
            "/" => 50
        },
        DEFAULT => -44
    },
    {#State 86
        DEFAULT => -41
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
#line 40 "oop_y.y"
{ { 'header' => $_[1],
                                        'rules'  => $_[2],
                                        'footer' => $_[3],
                                        'seq'    => [ @S ],
                                        'seqm'   => _M @S } }
    ],
    [#Rule 2
         'header', 2,
sub
#line 48 "oop_y.y"
{ undef @S; $_[1] }
    ],
    [#Rule 3
         'footer', 2,
sub
#line 52 "oop_y.y"
{ $_[2] }
    ],
    [#Rule 4
         'text', 0,
sub
#line 56 "oop_y.y"
{ '' }
    ],
    [#Rule 5
         'text', 1,
sub
#line 57 "oop_y.y"
{ join('', @{$_[1]}) }
    ],
    [#Rule 6
         'textl', 1,
sub
#line 61 "oop_y.y"
{ [$_[1]] }
    ],
    [#Rule 7
         'textl', 2,
sub
#line 62 "oop_y.y"
{ [@{$_[1]}, $_[2]] }
    ],
    [#Rule 8
         'rules', 1,
sub
#line 66 "oop_y.y"
{ _H { $_[1]->[0] => $_[1]->[1] } }
    ],
    [#Rule 9
         'rules', 2,
sub
#line 67 "oop_y.y"
{ $_[1]->{$_[2]->[0]} = $_[2]->[1];
                                      $_[1] }
    ],
    [#Rule 10
         'rule', 4,
sub
#line 73 "oop_y.y"
{ [ _S($_[1]) => $_[3] ] }
    ],
    [#Rule 11
         'rule', 4,
sub
#line 74 "oop_y.y"
{ [ '%'.$_[2] => $_[4] ] }
    ],
    [#Rule 12
         'ruledefs', 1, undef
    ],
    [#Rule 13
         'ruledefs', 3,
sub
#line 79 "oop_y.y"
{ _J('or',      $_[1], $_[3]) }
    ],
    [#Rule 14
         'ruledef', 0,
sub
#line 82 "oop_y.y"
{ ['empty'] }
    ],
    [#Rule 15
         'ruledef', 1, undef
    ],
    [#Rule 16
         'ruledef', 3,
sub
#line 84 "oop_y.y"
{ $_[1] }
    ],
    [#Rule 17
         'nonempty', 1, undef
    ],
    [#Rule 18
         'nonempty', 1,
sub
#line 89 "oop_y.y"
{ ['concat',    @{$_[1]}] }
    ],
    [#Rule 19
         'concat', 2,
sub
#line 93 "oop_y.y"
{ [ $_[1], $_[2] ] }
    ],
    [#Rule 20
         'concat', 2,
sub
#line 94 "oop_y.y"
{ [ @{$_[1]}, $_[2] ] }
    ],
    [#Rule 21
         'ruleexp', 1, undef
    ],
    [#Rule 22
         'ruleexp', 1,
sub
#line 99 "oop_y.y"
{ ['ref',       $_[1]] }
    ],
    [#Rule 23
         'ruleexp', 1,
sub
#line 100 "oop_y.y"
{ ['code',      $_[1]] }
    ],
    [#Rule 24
         'ruleexp', 1, undef
    ],
    [#Rule 25
         'ruleexp', 1, undef
    ],
    [#Rule 26
         'ruleexp', 3,
sub
#line 103 "oop_y.y"
{ ['group',     $_[2]] }
    ],
    [#Rule 27
         'ruleexp', 3,
sub
#line 104 "oop_y.y"
{ ['alias',     $_[1], $_[3]] }
    ],
    [#Rule 28
         'ruleexp', 3,
sub
#line 105 "oop_y.y"
{ ['alias',     $_[3], $_[1]] }
    ],
    [#Rule 29
         'ruleexp', 1, undef
    ],
    [#Rule 30
         'ruleexp', 1, undef
    ],
    [#Rule 31
         'term', 1,
sub
#line 111 "oop_y.y"
{ ['char',      $_[1]] }
    ],
    [#Rule 32
         'term', 1,
sub
#line 112 "oop_y.y"
{ ['string',    $_[1]] }
    ],
    [#Rule 33
         'quantifiers', 2,
sub
#line 116 "oop_y.y"
{ ['qt',        $_[1], 0, 1] }
    ],
    [#Rule 34
         'quantifiers', 2,
sub
#line 117 "oop_y.y"
{ ['qt',        $_[1], 0] }
    ],
    [#Rule 35
         'quantifiers', 2,
sub
#line 118 "oop_y.y"
{ ['qt',        $_[1], 1] }
    ],
    [#Rule 36
         'quantifiers', 4,
sub
#line 119 "oop_y.y"
{ ['qt',        $_[1], @{$_[3]}] }
    ],
    [#Rule 37
         'quantifiers', 5,
sub
#line 121 "oop_y.y"
{ ['repeat',    $_[2], $_[4]] }
    ],
    [#Rule 38
         'range', 1,
sub
#line 125 "oop_y.y"
{ [ $_[1], $_[1] ] }
    ],
    [#Rule 39
         'range', 3,
sub
#line 126 "oop_y.y"
{ [ $_[1], ] }
    ],
    [#Rule 40
         'range', 3,
sub
#line 127 "oop_y.y"
{ [ 0, $_[3] ] }
    ],
    [#Rule 41
         'range', 4,
sub
#line 128 "oop_y.y"
{ [ $_[1], $_[4] ] }
    ],
    [#Rule 42
         'call', 4,
sub
#line 132 "oop_y.y"
{ ['call',      $_[1], @{$_[3]}] }
    ],
    [#Rule 43
         'explist', 1,
sub
#line 136 "oop_y.y"
{ [$_[1]] }
    ],
    [#Rule 44
         'explist', 3,
sub
#line 137 "oop_y.y"
{ [@{$_[1]}, $_[3]] }
    ],
    [#Rule 45
         'rw_exp', 1,
sub
#line 141 "oop_y.y"
{ ['rw_cntl',   $_[1]] }
    ],
    [#Rule 46
         'spec', 3,
sub
#line 145 "oop_y.y"
{ ['raw',       '%'.$_[2].' '.$_[3]] }
    ],
    [#Rule 47
         'spec', 3,
sub
#line 146 "oop_y.y"
{ ['raw',       '%'.$_[2]." '$_[3]'"] }
    ],
    [#Rule 48
         'rule_name', 1, undef
    ],
    [#Rule 49
         'rule_alias', 1, undef
    ],
    [#Rule 50
         'rule_alias', 1, undef
    ],
    [#Rule 51
         'alias', 1, undef
    ],
    [#Rule 52
         'function_name', 1, undef
    ],
    [#Rule 53
         'ruledef_op', 1, undef
    ],
    [#Rule 54
         'ruledef_op', 1, undef
    ]
],
                                  @_);
    bless($self,$class);
}

#line 155 "oop_y.y"


1;
