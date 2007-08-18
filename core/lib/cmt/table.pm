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
    my $data = shift || [];
    my $attr = { data => $data, _tr => 0 };
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
    my $data = $this->()->{'data'}; # OPT
    # print "Cell[$row,$col]: ", join(', ', @_), "\n";
    if ($this->_tr) {
        @_ ? $data->[$col]->[$row] = shift : $data->[$col]->[$row]
    } else {
        @_ ? $data->[$row]->[$col] = shift : $data->[$row]->[$col]
    }
}

sub range {
    my $this = shift;
    return $this unless @_;
    my $row = shift;
    if (ref $row) {
        _cut1($this, $row, @_)
    } elsif (defined $row) {
        return sub { range($this, $row, @_) } unless @_;
        my $col = shift;
        if (ref $col) {
            my $V_1 = _cut1($this, $row);
            _cut1($V_1, $col, @_)
        } elsif (defined $col) {
            $this->cell($row, $col, @_)
        } else {
            # auto filler
        }
    } else {
        return sub { range($this, undef, @_) } unless @_;
        my $col = shift;
        if (defined $col) {
            _cut2($this, $col, @_)
        } else {
            # auto filler
        }
    }
}

sub _copy1($$;$) {
    my $A = shift;
    my $len = shift;
       $len = scalar(@$len) if ref $len;
    my $off = shift || 0;
    my @d;
    my $end = $off + $len;
    for (my $i = $off; $i < $end; $i++) {
        push @d, $A->($i);
    }
    \@d
}

sub _copy2($$$;$$) {
    my $A = shift;
    my $len1 = shift;
       $len1 = scalar(@$len1) if ref $len1;
    my $len2 = shift;
       $len2 = scalar(@$len2) if ref $len2;
    my $off1 = shift || 0;
    my $off2 = shift || 0;
    my @d;
    my $end1 = $off1 + $len1;
    my $end2 = $off2 + $len2;
  print "copying...\n";
    for (my $i = $off1; $i < $end1; $i++) {
        my @v;
        for (my $j = $off2; $j < $end2; $j++) {
            push @v, $A->($i, $j);
        }
        push @d, \@v;
    }
    \@d
}

sub _cut1 {
    my $A = shift;
    return $A unless @_;
    my $x1 = shift;
    if (ref $x1) {
        my $map1 = $x1;
        my @_P = @_;
        my $f; $f = sub {
            return _copy1($f, $map1) unless @_;
            my $x1 = shift;
            if (ref $x1) {
                _cut1($f, $x1, @_P, @_)
            } elsif (defined $x1) {
                return undef if $x1 >= @$map1;
                $A->($map1->[$x1], @_P, @_)
            } else {
                my @_P = @_;
                my $g; $g = sub {
                    return $g unless @_;
                    my $x1 = shift;
                    _cut1($f, $x1, @_P, @_)
                }
            }
        }
    } elsif (defined $x1) {
        $A->($x1, @_)
    } else {
        # auto-filler
        # sub { my @I = splice(@_, 0, 1); _cut1($A, @I, @_P, @_) }                      die 'cut1(undef)';
    }
}

#
# XXX: when _cut1 returned in _cut2, the _cut1 couldn't back to _cut2.
#
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
            if (ref $x1) {
                my $f_0 = _cut1($f, $x1);
                return $f_0 unless @_;
                my $x2 = shift;
                _cut2($f_0, $x2, @_P, @_)
            } elsif (defined $x1) {
                my $A_1 = _cut1($A, $x1);
                my $f_1 = _cut1($A_1, $map2);
                return $f_1 unless @_;
                my $x2 = shift;
                if (defined $x2) {
                    _cut1($f_1, $x2, @_P, @_)
                } else {
                    # auto-filler
                    die 'cut2->(x1,undef)';
                }
            } elsif (@_) {
                my $x2 = shift;
                _cut2($f, $x2, @_P, @_)
            } else {
                # auto-filler
                die 'cut2->(undef)';
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
        # sub { my @I = splice(@_, 0, 2); _cut1($A, @I, @_P, @_) }                      die 'cut2(undef)';
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