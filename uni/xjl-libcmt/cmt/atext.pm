package cmt::atext;

use strict;
use Exporter;
use UNIVERSAL   qw(isa);

our @ISA        = qw(Exporter);
our @EXPORT     = qw(atext
                     atext_cat
                     atext_gat
                     atext_tag
                     atext_sel
                     atext_test
                     atext_then
                     atext_or
                     atext_call
                     atext_simpl
                     atext_dump
                     );

sub atext       { @_ = map { isa($_, __PACKAGE__) ? $_ : new cmt::atext($_) } @_ = @_;
                  wantarray ? @_ : shift }
sub atext_cat   { new cmt::atext::Cat(@_)   }
sub atext_gat   { new cmt::atext::Gat(@_)   }
sub atext_tag   { new cmt::atext::Tag(@_)   }
sub atext_sel   { new cmt::atext::Sel(@_)   }
sub atext_test  { new cmt::atext::Test(@_)  }
sub atext_then  { new cmt::atext::Then(@_)  }
sub atext_or    { new cmt::atext::Or(@_)    }
sub atext_call  { new cmt::atext::Call(@_)  }

sub atext_simpl {
    my @v = (shift);
    for (@_) {
        if (ref $_ || ref $v[-1]) {
            push @v, $_;
        } else {
            $v[-1] .= $_;
        }
    }
    return @v;
}

sub atext_dump {
    my ($node, $indent) = @_;
    my $t = ref $node;
    if ($t ne '') {
        $t =~ s/^cmt::atext:*/%/;
    } else {
        $t = '$';
    }
    # $t .= ' 'x(8 - length $t) if length $t < 8;
    if (isa($node, 'ARRAY')) {
        my $buf = "$indent$t";
        for (@$node) {
            $buf .= "\n";
            $buf .= atext_dump($_, $indent.'  ');
        }
        return $buf;
    } elsif (isa($node, 'SCALAR')) {
        return "$indent$t \"$$node\"";
    } else {
        return "$indent$t `$node`";
    }
}

package cmt::atext::Cat;
package cmt::atext::Gat;
package cmt::atext::Tag;
package cmt::atext::Sel;
package cmt::atext::Test;
package cmt::atext::Then;
package cmt::atext::Or;
package cmt::atext::Call;

{
    package cmt::atext;         # abstract text
    sub new     { my $class = shift; bless \shift, $class }
    sub val     { ${(shift)} }
    sub len     { length shift->val }
    sub cat     { my $l = shift;
                  return new cmt::atext::Cat($l, @_) if grep { ref $_ } @_;
                  $$l .= join('', @_); $l }
    sub gat     { new cmt::atext::Gat(@_)   }
    # sub tag     { new cmt::atext::Tag(@_)   }
    sub sel     { new cmt::atext::Sel(@_)   }
    sub test    { new cmt::atext::Test(@_)  }
    sub then    { new cmt::atext::Then(@_)  }
    sub or      { new cmt::atext::Or(@_)    }
    sub call    { new cmt::atext::Call(@_)  }
}

{
    package cmt::atext::Cat;    # concat
    import  cmt::atext;
    our @ISA    = qw(cmt::atext);
    sub new     { my $class = shift; bless [ atext(@_) ], $class }
    sub val     { join('', map { $_->val } @{(shift)}) }
    sub cat     { my $l = shift; push @$l, atext(@_); $l }
}

{
    package cmt::atext::Gat;    # together
    import  cmt::atext;
    our @ISA    = qw(cmt::atext);
    sub new     { my $class = shift; bless [ atext(@_) ], $class }
    sub val     { my $l = shift; @_ = map { $_->val } @$l;
                  for (@_) { return undef unless defined $_ }
                  join('', @_) }
}

{
    package cmt::atext::Tag;    # tag
    import  cmt::atext;
    our @ISA    = qw(cmt::atext::Cat);
    sub new     { my $class = shift; bless [ shift, atext(@_) ], $class }
    sub tag     { ${(shift)}[0] }
    sub val     { my $l = shift; join('', map { $_->val } @$l[1..$#$l]) }
}

{
    package cmt::atext::Sel;    # select
    import  cmt::atext;
    our @ISA    = qw(cmt::atext);
    sub new     { my $class = shift; bless [ atext(@_) ], $class }
    sub val     { my ($i, @l) = @{(shift)}; $i = $i->val;
                  ref $l[$i] ? $l[$i]->val : $l[$i] }
}

{
    package cmt::atext::Test;    # if/then/else
    import  cmt::atext;
    our @ISA    = qw(cmt::atext);
    sub new     { my $class = shift; bless [ atext(@_) ], $class }
    sub val     { my ($t, $true, @l) = @{(shift)};
                  defined $$t ? $true->val : join('', map { $_->val } @l) }
}

{
    package cmt::atext::Then;    # if/then
    import  cmt::atext;
    our @ISA    = qw(cmt::atext);
    sub new     { my $class = shift; bless [ atext(@_) ], $class }
    sub val     { my ($t, @l) = @{(shift)};
                  defined $$t ? join('', map { $_->val } @l) : undef }
}

{
    package cmt::atext::Or;     # or-concat
    import  cmt::atext;
    our @ISA    = qw(cmt::atext);
    sub new     { my $class = shift; bless [ atext(@_) ], $class }
    sub val     { my ($t, @l) = @{(shift)};
                  defined $$t ? $$t : join('', map { $_->val } @l) }
}

{
    package cmt::atext::Call;   # call-sub
    import  cmt::atext;
    use     UNIVERSAL qw(isa);
    our @ISA    = qw(cmt::atext);
    sub new     { my $class = shift; my $f = shift;
                  $f = $f->val if isa $f, 'cmt::atext';
                  die "Not code" unless isa $f, 'CODE';
                  bless [ $f, @_ ], $class }
    sub f       { ${(shift)}[0] }
    sub val     { my $l = shift; my $f = $l->f; $f->(@$l[1..$#$l]) }
}

1