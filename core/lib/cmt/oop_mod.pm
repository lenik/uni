package cmt::oop_mod;

use strict;
use constant(
    C_GA    => '[@_[1..$#_]]',
    );
use cmt::english;
use cmt::lang;
use cmt::lexutil;
use cmt::util;
use Data::Dumper;
use Exporter;
use Parse::Lex;

our @ISA    = qw(Exporter);
our @EXPORT = qw();

my %PERLKW; $PERLKW{$_} = '_'.$_ for
    qw(continue do else elsif exit for foreach goto if last local my next
       no our package redo require return sub unless until use while);
sub _perlkw { $PERLKW{$_[0]} || $_[0] }

sub _strid  { my $s = shift; $s =~ s/\s+|\W/_/g;
                 $s =~ /^\d/ ? '__'.$s : '_'.$s }

sub _uniq(\%$;$) {
    my $t = $_[1].$_[2];
    exists $_[0]->{$t} ? _uniq($_[0], $_[1], $_[2] + 1) : $t
}

sub _uniqs {
    my $n = shift;
  W:while (1) {
      E:for (@_) {
            my $m = _uniq(%$_, $n);
            if ($m ne $n) { $n = $m; next W }
        }
        last
    }
    $n
}

my %_res_token = (
    _id     => '[a-zA-Z_][a-zA-Z_0-9]*',
    _number => '\d+',
    _char   => [ qw(' (?:\\.|[^'])* ') ],
    _string => [ qw(" (?:\\.|[^"])* "), sub { substr($_[1], 1, -1) } ],
    _nl     => '\n',
);

sub new {
    my $class   = shift;
    my $this = bless {
        # dom   => $_[0],
        ruledef => {},
        rule    => {},
        token   => { %_res_token },
        tokidx  => {},
        rc      => {},      # ref-count
    }, $class;
    my $init_defs = $_[0]->{'rules'};
       $this->add_ruledefs($init_defs) if defined $init_defs;
    $this
}

# No (empty) in rule-def
sub _Ne     { $_[0]->[0] ne 'empty' or die "(empty) isn't allowed in ".$_[1];
              $_[0] }

sub _Rc     { $_[0]->{rc}->{$_[1]}++; $_[1] }

sub add_ruledefs {
    my ($this, $defs) = @_;
    for (keys %$defs) {
        $this->{'ruledef'}->{$_} = 1;
    }
    for (keys %$defs) {
        my $def = $defs->{$_};
        my $rule = $this->add_ruledef($_, $def);
    }
}

sub add_ruledef {
    my ($this, $nam, $def) = @_;
    $this->{ruledef}->{$nam} = $def;
    my  $rule = $this->call(@$def);
    if ($rule->[0] ne ':') {
        $rule = $this->flat($rule);
    }
    $this->{rule}->{$nam} = $rule
}

sub mk_rule {
    my ($this, $nampref, $def) = @_;
    my $nam = $this->newnam($nampref);
    $this->add_ruledef($nam, $def);
    [ '.', $nam ]
}

sub call {
    my ($this, $tag) = splice @_, 0, 2;
    # return: [ symbol, alias ]
    # print "parsing $tag - ".Dumper(\@_);
    die "unknown pattern name: $tag" unless $this->can($tag);
    $this->$tag(@_)
}

sub newnam {
    my ($this, $nampref) = @_;
    _uniqs $nampref, $this->{ruledef}, $this->{token}
}

sub toknam { _or($_[0]->{tokidx}->{$_[1]}, $_[0]->newnam(_strid $_[1])) }

sub guessnam {
    my ($this, $d) = @_;
    my $tag = $d->[0];
    if ($tag eq 'alias') {
        return $d->[1];
    } elsif ($tag eq 'ref') {
        return $d->[1];
    } elsif ($tag eq 'string') {
        return $this->toknam($d->[1]);
    } elsif ($tag eq 'concat') {
        return guessnam($d->[1]);
    } elsif ($tag eq 'group') {
        return guessnam($d->[1]);
    } elsif ($tag eq 'qt') {
        return guessnam($d->[1]);
    } elsif ($tag eq 'repeat') {
        return guessnam($d->[1]);
    } else {
        return $tag;
    }
}

sub flat {
    my $this = shift;

    # (empty)
    return [ ':', '' ] if ($#_ == 0 && ! defined $_[0]->[1]);

    my %scope;
    my @alias;
    my $buf;
    my $defcode = 1;
    for (0..$#_) {
        my $t = $_[$_];
        my $alias = _or($t->[2], $t->[1]);
           $alias = _uniq %scope, $alias;
        $scope{$alias}++;
        if ($t->[0] eq '!') {
            my $decl = 'my ($' . join(', $', @alias) . ') = @_; '
                if @alias and !defined $t->[3];
            $t->[1] = '{ '. $decl . $t->[1] . ' }';
            undef $defcode if $_ == $#_;
        }
        push @alias, $alias;
        $buf .= ' ' if defined $buf;
        $buf .= $t->[1];
    }
    if ($defcode) {
        # my $decl = 'my ($' . join(', $', @alias) . ') = @_; ' if @alias;
        if (@_ > 1) {
            $defcode = '{ '. C_GA . ' }';
            $buf .= ' ' if defined $buf;
            $buf .= $defcode;
        } else {
            # yapp: the default semantic of a rule is equal to $_[1]
        }
    }
    [ ':', $buf, @alias ]
}

sub empty   { [ '.', undef ] }
sub ref     { [ '.', $_[0]->_Rc($_[1]) ] }
sub code    { [ '!', $_[1], 'code', $_[2] ] }
sub alias   { $_[2]->[2] = $_[1]; $_[2] }
sub char    { [ '.', "'$_[1]'", 'ch' ] }

sub rw_cntl { undef }
sub raw     { [ '.', $_[1] ] }

sub string  {
    my ($this, $s) = @_;
    my $nam = $this->{tokidx}->{$s} = $this->toknam($s);
    $this->{token}->{$nam} = $s;
    [ '.', $this->_Rc($nam) ]
}

sub concat {
    my $this = shift;
    @_ = map { _Ne($_, 'concat'); $this->call(@$_) } @_;
    $this->flat(@_)
}

sub or {
    my $this = shift;
    @_ = map { $this->call(@$_) } @_;
    my $buf;
    my @used_aliases;
    for (@_) {
        my $t = $_;
           $t = $this->flat($t) if $t->[0] ne ':';
        $buf .= "\n  | " if defined $buf;
        $buf .= $t->[1];
        push @used_aliases, @$t[2..$#$t];
    }
    [ ':', $buf, @used_aliases ]
}

sub group {
    my ($this, $d) = @_;
    $this->mk_rule('group', _Ne($d, 'group'))
}

sub qt {
    my ($this, $d, $min, $max) = @_;    # assert 0 <= min <= max
    _Ne($d, 'quantifier');
    my $nampref = plural($this->guessnam($d), 'quant');
    my $nam = $this->newnam($nampref);
    my $def;
    my $prefix = [ 'concat' ];
    for (my $i = 0; $i < $min; $i++) {
        push @$prefix, $d;
    }
    if (defined $max) {
        $def = [ 'or', $min == 0
                        ? [ 'code', '[]', 'RAW' ]
                        : [ @$prefix, [ 'code', C_GA, 'RAW' ]]];
        my $varlen = $max - $min + 1;
        for (my $i = 1; $i < $varlen; $i++) {
            my @vfixed = @$prefix;
            for (my $j = 0; $j < $i; $j++) {
                push @vfixed, $d;
            }
            push @$def, [ @vfixed, [ 'code', C_GA, 'RAW' ] ];
        }
    } elsif ($min == 0) {
        # q:    (empty)
        #     | q node
        $def = [ 'or', [ 'code', '[]', 'RAW' ],
                       [ 'concat', [ 'ref', $nam ],
                                   $d,
                                   [ 'code', '[ @{$_[1]}, $_[2] ]', 'RAW' ]]];
    } else {
        # q:    node ... node (x $min)
        #     | q qe
        # qe:   (empty)
        #     | qe node
        my $nam2 = $this->newnam($nampref.'_a');
        my $def2 = [ 'or', [ 'code', '[]', 'RAW' ],
                           [ 'concat',
                                [ 'ref', $nam2 ],
                                $d,
                                [ 'code', '[ @{$_[1]}, $_[2] ]', 'RAW' ]]];
        $this->add_ruledef($nam2, $def2);
        $def = [ 'or', [ @$prefix, [ 'code', C_GA, 'RAW' ] ],
                       [ 'concat',
                            [ 'ref', $nam ],
                            [ 'ref', $nam2 ],
                            [ 'code', '[ @{$_[1]}, @{$_[2]} ]', 'RAW' ]]];
    }
    my $rule = $this->add_ruledef($nam, $def);
    # my $alias = $rule->[2] if $#$rule == 2;
    [ '.', $nam ]
}

sub _imgrp {
    my $d = shift;
    _Ne($d, 'implied-grouping');
    if ($d->[0] eq 'concat' or $d->[0] eq 'or') {
        $d = [ 'group', $d ]
    }
    $d
}

sub repeat {
    my ($this, $ker, $delim) = @_;
    $ker    = _imgrp _Ne($ker, 'repeat/ker');
    $delim  = _imgrp _Ne($delim, 'repeat/delim');

    # repeat: ker
    #       | repeat delim ker
    my $nam = $this->newnam('repeat');
    my $def = [ 'or', [ 'concat', $ker,
                                  [ 'code', '[$_[1]]', 'RAW' ]],
                      [ 'concat', [ 'ref', $nam ], $delim, $ker,
                                  [ 'code', '[@{$_[1]}, $_[3]]', 'RAW' ]]];
    $this->add_ruledef($nam, $def);
    [ '.', $nam ]
}

sub newlexer {
    my ($this, $lexer) = @_;
    my $tokens = $this->{token};
    my @Toks = qw(
    );
    for (keys %$tokens) {
        next unless exists $this->{rc}->{$_};
        my $tokdef = $tokens->{$_};
        my $toknam = substr($_, 1);     # remove the leading _(underline)
        if (ref $tokdef eq 'ARRAY') {
            my @xtok = @$tokdef;
            my $code = pop @xtok if ref $xtok[-1] eq 'CODE';
            my $tokg = @xtok > 1 ? \@xtok : $xtok[0];
            if (defined $code) {
                push @Toks, ($toknam, $tokg, $code);
            } else {
                push @Toks, ($toknam, $tokg);
            }
        } else {
            push @Toks, ($toknam => $tokdef);
        }
    }
    push @Toks, (
        qw(
            _op         .
        ));
    # print Dumper(\@Toks);
    if (defined $lexer) {
        $lexer->defineTokens(@Toks);
    } else {
        $lexer = new Parse::Lex(@Toks);
    }
    yylex2 $lexer
}

sub dump {
    my ($this, $dom) = @_;
    my $header = $dom->{'header'} . "\n" if defined $dom;
    my $footer = $dom->{'footer'} . "\n" if defined $dom;
    my $seq    = $dom->{'seq'};
    my $seqm   = $dom->{'seqm'};
    my $rules  = $this->{rule};
    my $f      = '';
    my @namseq = @$seq if defined $seq;
    for (keys %$rules) {
        push @namseq, $_ unless defined $seqm and exists $seqm->{$_};
    }
    for (@namseq) {
        my $rule = $rules->{$_};
        my $flat = $rule->[1];
        $f .= "$_: \n    $flat\n  ;\n\n";
    }
    $header . "%%\n" . $f . "%%\n" . $footer
}

1