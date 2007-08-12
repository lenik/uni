package cmt::oop_mod;

use strict;
use Exporter;

our @ISA    = qw(Exporter);
our @EXPORT = qw();

our $opt_verbtitle      = __PACKAGE__;
our $opt_verbtime       = 0;
our $opt_verbose        = 1;

sub new {
    my $class   = shift;
    bless {
        dom     => $_[0],
        rules   => $_[0]->{'rules'},
        tokens  => {},
    }, $class
}

my %PERLKW; $PERLKW{$_} = '_'.$_ for
    qw(continue do else elsif exit for foreach goto if last local my next
       no our package redo require return sub unless until use while);
sub _perlkw { $PERLKW{$_[0]} || $_[0] }

sub _strid  { my $s = shift; $s =~ s/\s+|\W/_/g; $s =~ /^\d/ ? '_'.$s : $s }

sub _uniq(\%$) { exists $_[0]->{$_[1]} ? _uniq($_[0], '_'.$_[1]) : $_[1] }

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

sub _sel    { '$_['.$_[1].']' }

# ref. group. alias.. concat.. or.. char. string. rw_cntl. q... repeat.. call.*

sub prep_rules {
    my $this = @_;
    my $rules = $this->{rules};
    my @orig_names = keys %$rules;
    for my $name (@orig_names) {
        my $rule = $rules->{$name};
        # assert ref $rule
        $rule = $this->call(@$rule)
        $rules->{$name} = $rule;
    }
}

sub call {
    my ($this, $tag) = splice @_, 2;
    # return: [ symbol, alias ]
    $this->tag(@_)
}

sub newnam {
    my ($this, $prefix) = @_;
    _uniqs $prefix, $this->{rules}, $this->{tokens}
}

sub mkrule {
    my ($this, $prefix) = splice @_, 2;
}

sub ref     { [ $_[1], $_[1] ]}
sub alias   { [ $_[2]->[0], $_[1] ] }   # ignore any exists: $_[2]->[1]
sub rw_cntl { undef }
sub char    { [ $_[1], 'ch' ] }
sub string  {
    my ($this, $s) = @_;
    my $toks    = $this->{tokens};
    my $toknam  = $toks->{$s};
    if ($toknam eq '') {
        $toknam = _strid $s;
        $toknam = $this->newnam($toknam);
        $toks->{$s} = $toknam;
    }
    [ $toknam, $toknam ]
}

sub concat {
    my $this = shift;
    @_ = map { $this->call(@$_) } @_;
    [ @_ ]
}

sub or {
    my $this = shift;
    @_ = map { $this->call(@$_) } @_;
    @_
}

sub group {
    my ($this, $node) = @_;
    $node = $this->call(@$node);
    my $rules = $this->{rules};
    my $gnam = $this->newnam('group');
    $rules->{$gnam} = [ $node ];
    [ $gnam, $gnam ]
}

sub q {
    my ($this, $node, $min, $max) = @_;
    # assert 0 <= min <= max
    $node = $this->call(@$node);
    my $qnam = $this->newnam('quant');
    if (defined $max) {
        my $comb = [];
        for (my $i = $min; $i <= $max; $i++) {
            my $fixed = $node; # x $i;
            push @$comb, $fixed;
        }
        $rules->{$qnam} = $comb;
    } else {
        my $fixed = $node; # x $min;
        my $qnam_a = $this->newnam('quant_a');
        # qnam_a: (empty)
        #       | qnam_a $node
        $rules->{$qnam} = [ $fixed, $any ];
        $rules->{$qnam_a} = [ $any ];
    }
    [ $qnam, $qnam ]
}

sub repeat {
    my ($this, $ker, $delim) = @_;
    # repeat: ker
    #       | repeat delim ker
    my $repnam = $this->newnam('repeat');
    $this->mkrule(
        'or', $ker,
              [ 'concat', [ 'ref', $repnam ],
                          $delim, $ker ])
}

sub dump    {
    my ($this, $dom) = @_;
    my $f = $dom->{'header'} . "\n%%\n";
    my $rules = $dom->{'rules'};
    for my $name (keys %$rules) {
        my $or_list = $rules->{$name};
        $f .= $name . ": \n    ";
        for my $i (0..$#$or_list) {
            my $items = $or_list->[$i];
            $f .= "\n  | " if $i;
            $f .= join(' ', @$items);
        }
        $f .= "\n  ;\n";
    }
    $f . "%%\n" . $dom->{'footer'}
}

1