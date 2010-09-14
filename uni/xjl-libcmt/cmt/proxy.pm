package cmt::proxy;

sub new {
    my ($class, $proxy) = @_;
    # ignore this "cmt::proxy"
    return $proxy;
}

sub TIESCALAR {
    my ($class, $proxy) = @_;
    # ignore this "cmt::proxy"
    return $proxy;
}

sub TIEARRAY {
    my ($class, $proxy) = @_;
    # ignore this "cmt::proxy"
    return $proxy;
}

sub TIEHASH {
    my ($class, $proxy) = @_;
    # ignore this "cmt::proxy"
    return $proxy;
}

sub TIEHANDLE {
    my ($class, $proxy) = @_;
    # ignore this "cmt::proxy"
    return $proxy;
}

1