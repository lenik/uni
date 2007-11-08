package cmt::netutil;

use strict;
use vars                qw/@ISA @EXPORT @EXPORT_OK/;
use cmt::path;
use cmt::util;
use Exporter;
use Fcntl;
use Socket qw(:all);

our $opt_verbtitle      = __PACKAGE__;
our $opt_verbtime       = 0;
our $opt_verbose        = 1;
our $opt_strict         = 0;
our $opt_wget           = 0;

sub info {
    return if $opt_verbose < 1;
    my $text = shift;
    print cdatetime.' ' if $opt_verbtime;
    print "[$opt_verbtitle] $text\n";
}

sub info2 {
    return if $opt_verbose < 2;
    my $text = shift;
    print cdatetime.' ' if $opt_verbtime;
    print "[$opt_verbtitle] $text\n";
}

sub setnonblock {
    my $h = shift;
    eval {
        info2 "setnonblock $h by Fcntl";
        my $flags = 0;
        fcntl($h, F_GETFL, $flags) or return undef;
        $flags |= O_NONBLOCK;
        fcntl($h, F_SETFL, $flags) or return undef;
        1
    } or eval {
        info2 "setnonblock $h by ioctl";
        my $temp = 1;
        ioctl $h, 0x8004667E, \$temp;
        1
    } or eval {
        info2 "setnonblock $h by setsockopt";
        setsockopt $h, IPPROTO_TCP, TCP_NODELAY, 1;
        1
    } or die "Can't setnonblock, system doesn't support the operation";
}

sub nbread {
    my ($h, $size)  = @_;

    # set non-block
    setnonblock($h) or return undef;

    my $buf;
    my $size1 = defined $size ? $size : 4096;
    my $len = sysread($h, $buf, $size1);

    # May be need this?
    if (defined $len) {
        $buf = '' if $len == 0;
    } else {
        return undef;
    }

    if (! defined $size) {
        my $block;
        while (($len = sysread($h, $block, 4096)) > 0) {
            $buf .= $block;
        }
    }
    return $buf;
}

sub format_inaddr {
    my $sockaddr = shift;
    my ($port, $iaddr) = sockaddr_in($sockaddr);
    my $ip   = inet_ntoa($iaddr);
    my $host = gethostbyaddr($iaddr, AF_INET);
    return $host ne '' ? "$host:$port" : "$ip:$port";
}

sub sockinfo {
    my ($h, $name) = @_;
    if (defined (my $local = getsockname($h))) {
        my $peer = getpeername($h);
        $h = format_inaddr($local) . '->' . format_inaddr($peer);
    }
    defined $name ? "$name($h)" : $h;
}

sub post {
    require LWP::UserAgent;

    my $url = shift;
    my $cnt;
       $cnt = pop @_ if scalar(@_) & 1;     # odd
    my $agent = new LWP::UserAgent;
    my $resp = $agent->post($url, Content => $cnt);
    if ($resp->is_success) {
        return $resp->content;
    }
    return undef;
}

sub loadurl {
    my ($l, $post) = @_;
    my $cnt;

    if ($opt_wget) {
        my $tmp = temp_path;
        my $wget = "wget -q -O\"$tmp\"";
           $wget .= " --post-data=$post" if defined $post;
        system "$wget \"$l\"";
        if (-f $tmp) {
            $cnt = readfile $tmp;
            unlink $tmp;
        } else {
            die "Failed to load $l (errno of wget: $?)";
        }
    } else {
        if (defined $post) {
            $cnt = post($l, $post);
        } else {
            require LWP::Simple;
            $cnt = LWP::Simple::get($l);
        }
        die "Failed to load $l" unless defined $cnt;
    }
    return $cnt;
}

@ISA        = qw(Exporter);
@EXPORT     = qw(setnonblock
                 nbread
                 format_inaddr
                 sockinfo
                 loadurl);
@EXPORT_OK  = qw(post);

1