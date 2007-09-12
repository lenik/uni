package cmt::utree;

use strict;
use Encode;
use Exporter;
use HTML::TreeBuilder;

our @ISA    = qw(HTML::TreeBuilder);
our @EXPORT = qw();

sub new {
    my $class   = shift;
    my $self    = $class->SUPER::new(@_);
    #$self->{method}     = \&method;
    #$self->{attribute}  = undef;
    $self;
}

sub hdecode {
    my ($enc, $bin) = @_;
    my $buf;
    while (length $bin) {
        $buf .= decode($enc, $bin, Encode::FB_QUIET);
        if (length $bin) {
            my $c = substr($bin, 0, 1);
            $buf .= '&' . ord($c) . ';';
            $bin = substr($bin, 1);
        }
    }
    return $buf;
}

sub hencode {
    my ($enc, $bin) = @_;
    my $buf;
    while (length $bin) {
        $buf .= encode($enc, $bin, Encode::FB_QUIET);
        if (length $bin) {
            my $c = substr($bin, 0, 1);
            $buf .= '&' . ord($c) . ';';
            $bin = substr($bin, 1);
        }
    }
    return $buf;
}

sub iconv {
    my $self = shift;
    my ($from, $to) = @_;
    $to ||= $from;
    $self->traverse( sub {
            my ($node, $start) = @_;
            return unless $start;
            my $c = $node->{_content};
            for my $i ($0..$#$c) {
                my $t = $c->[$i];
                unless (ref $t) {
                    # print "$node,$i,$start-$t\n";
                    my $decoded = decode($from, $t);
                    $t = encode($to, $decoded);
                    $c->[$i] = $t;
                }
            }
            1
        }, 1);
}

1

__END__

#
#   THE PROXY METHOD, NOT USED.
#

sub encoding {
    my $self = shift;
    if (@_) {
        my $e = shift;
        if ($self->{_encoding} = $e) {
            $self->u_enable;
        } else {
            $self->u_disable;
        }
    }
    $self->{_encoding};
}

sub u_enable {
    my $self = shift;
    return if $self->{u_enabled};
    $self->{u_enabled} = 1;
    $self->{u_oldtext} = $self->handler('text');
    $self->handler('text' => \&h_text);
}

sub u_disable {
    my $self = shift;
    return unless $self->{u_enabled};
    $self->{u_enabled} = 0;
    $self->handler('text' => $self->{u_oldtext});
}

sub h_text {
    my $self = shift;
    my ($origtext) = @_;
    my $enc = $self->{_encoding};
    my $u = decode($enc, $origtext);
    $self->text($u);
}

sub address {
    my $self = shift;
    my $ret = $self->SUPER::address(@_);
    return $self->_wrap($ret);
}

sub as_text {
    my $self = shift;
    my $t = $self->SUPER::as_text;
    print "T0=$t\n";
    $t = encode($self->{_encoding}, $t) if $self->{u_enabled};
    print "T1=$t\n";
    return $t;
}
