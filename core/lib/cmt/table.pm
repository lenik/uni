package cmt::table;

use strict;
use Carp;

my %SKIP; $SKIP{$_} = 1 for qw(DESTROY);
sub AUTOLOAD {
    my $this = shift;
    local $_ = our $AUTOLOAD;
    my $i = rindex($_, '::');
       $_ = substr($_, $i + 2) if $i >= 0;
    return if $SKIP{$_};
    my $attr = $this->();
    if ($attr->{__lock} and !exists $attr->{$_}) {
        croak "no such attribute: $_ (in object $this)";
    }
    @_ ? $attr->{$_} = $_[0] : $attr->{$_}
}

sub new {
    my $class = shift;
    my $attr = { data => [], tr => 0 };
    my $this;
       $this = sub { @_ ? range($this, @_) : $attr };
	bless $this, $class;
}

sub _lock       { shift->__lock(2) }
sub _unlock     { shift->__lock(0) }

sub cell {
    my $this = shift;
    my $row  = shift;
    my $col  = shift;
    my $data = $this->data;
    print "Cell[$row,$col]: ", join(', ', @_), "\n";
    if ($this->tr) {
        @_ ? $data->[$col]->[$row] = shift : $data->[$col]->[$row]
    } else {
        @_ ? $data->[$row]->[$col] = shift : $data->[$row]->[$col]
    }
}

sub range {
    my $this = shift;
    return $this unless @_;
    my $row  = shift;
    if (ref $row) {
        _cut1($this, $row, @_)
    } elsif (defined $row) {
        return sub { range($this, $row, @_) } unless @_;
        my $col = shift;
        if (ref $col) {
            my $C = _cut2($this, $col);
            my @_P = @_;
            my $f; $f = sub {
                return $f unless @_;
                my $col = shift;
                if (ref $col) {
                    _cut2($f, $col, @_P, @_)
                } else {
                    # $C->($col)->($row)
                    $this->call($row, $col, @_P, @_)
                }
            }
        } elsif (defined $col) {
            $this->cell($row, $col, @_)
        } else {
            # auto-filler
        }
    } else {

    }
}

sub _cut1 {
    my $A = shift;
    return $A unless @_;
    my $x1 = shift;
    if (ref $x1) {
        my $map1 = $x1;
        my @_P = @_;
        my $f; $f = sub {
            return $f unless @_;
            my $x1 = shift;
            if (ref $x1) {
                _cut1($f, $x1, @_P, @_)
            } else {
                $A->($map1->[$x1], @_P, @_)
            }
        }
    } elsif (defined $x1) {
        $A->($x1, @_)
    } else {
        # auto-filler
    }
}

sub _cut2 {
    my $A = shift;
    return $A unless @_;
    my $x2 = shift;
    my @_P = @_;
    if (ref $x2) {
        my $map2 = $x2;
        my $f; $f = sub {
            return $f unless @_;
            my $x1 = shift;
            return sub { $f->($x1, @_) } unless @_;
            my $x2 = shift;
            if (ref $x1) {
                my $A_0 = _cut1($A, $x1);       # matrix by array  x1
                my $f_0 = _cut2($A_0, $map2);   # matrix by array  map2
                _cut2($f_0, $x2, @_P, @_)       # matrix/vector
            } elsif (defined $x1) {
                my $A_1 = _cut1($A, $x1);       # vector by scalar x1
                my $f_1 = _cut1($A_1, $map2);   # vector by array  map2
                _cut1($f_1, $x2, @_P, @_)       # vector/scalar
            } else {
                # auto-filler
            }
        }
    } elsif (defined $x2) {
        my $f; $f = sub {
            return $f unless @_;
            my $x1 = shift;
            _cut1($A, $x1, $x2, @_P, @_)
        }
    } else {
        # auto-filler
    }
}

sub tr {
    my $range = shift;
    my $f; $f = sub {
        my $row = shift;
        my $col = shift;
        $range->($col, $row, @_)
    }
}

1