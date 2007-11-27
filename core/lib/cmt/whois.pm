package cmt::whois;

=head1 NAME

cmt::whois - replace Net::Whois

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
    $LOGNAME    = __PACKAGE__;
    $LOGLEVEL   = 2;
use cmt::inet('tcp_connect');
use cmt::log(2);
use cmt::stream;
use cmt::util;
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id$');
    our $VER    = "0.$RCSID{rev}";
use Data::Dumper;
use Exporter;

our @ISA    = qw(Exporter);
our @EXPORT = qw(whois
                 );

sub whois_exec;
sub whois_inet;
sub whois;      *whois = *whois_inet;
sub parse_whois;

our %TLD_SERVER = (
    'com'       => 'whois.tucows.com',  # whois.internic.com',
    'net'       => 'whois.internic.com',
    'org'       => 'whois.internic.com',
    'edu'       => 'whois.internic.com',
    'cn'        => 'whois.cnnic.net.cn',
    'biz'       => 'whois.biz',         #'whois.neulevel.biz',
    'bz'        => 'mhpwhois1.verisign-grs.net',
    'to'        => 'monarch.tonic.to',
    'gs'        => 'whois.adamsnames.tc',
    'ms'        => 'whois.adamsnames.tc',
    'tc'        => 'whois.adamsnames.tc',
    'tf'        => 'whois.adamsnames.tc',
    'vg'        => 'whois.adamsnames.tc',
    'aero'      => 'whois.aero',
    'info'      => 'whois.afilias.net',
    'am'        => 'whois.amnic.net',
    'au'        => 'whois.aunic.net',   # whois.apnic.net',
    'com.au'    => 'whois.ausregistry.net.au',
    'si'        => 'whois.arnes.si',
    'eu.lv'     => 'whois.biz',
    'cd'        => 'whois.cd',
    'br.com'    => 'whois.centralnic.com',
    'cn.com'    => 'whois.centralnic.com',
    'eu.com'    => 'whois.centralnic.com',
    'hu.com'    => 'whois.centralnic.com',
    'no.com'    => 'whois.centralnic.com',
    'qc.com'    => 'whois.centralnic.com',
    'sa.com'    => 'whois.centralnic.com',
    'se.com'    => 'whois.centralnic.com',
    'se.net'    => 'whois.centralnic.com',
    'us.com'    => 'whois.centralnic.com',
    'uy.com'    => 'whois.centralnic.com',
    'za.com'    => 'whois.centralnic.com',
    'uk.com'    => 'whois.centralnic.com',
    'uk.net'    => 'whois.centralnic.com',
    'gb.com'    => 'whois.centralnic.com',
    'gb.net'    => 'whois.centralnic.com',
    'gg.je'     => 'whois.channelisles.net',
    'ca'        => 'whois.cira.ca',
    'ug'        => 'whois.co.ug',
    'za'        => 'whois.co.za',
    'de'        => 'whois.denic.de',
    'dk'        => 'whois.dk-hostmaster.dk',
    'be'        => 'whois.dns.be',
    'lu'        => 'whois.dns.lu',
    'pl'        => 'whois.dns.pl',
    'nl'        => 'whois.domain-registry.nl',
    'kz'        => 'whois.domain.kz',
    'ie'        => 'whois.domainregistry.ie',
    'nz'        => 'whois.domainz.net.nz',
    'tk'        => 'whois.dot.tk',
    'gov'       => 'whois.dotgov.gov',
    'edu'       => 'whois.educause.edu',
    'ee'        => 'whois.eenet.ee',
    'hk'        => 'whois.hkirc.net',
    'int'       => 'whois.iana.org',
    'in'        => 'whois.iisc.ernet.in',
    'mil'       => 'whois.internic.net',
    'pro'       => 'whois.internic.net',
    'is'        => 'whois.isnic.is',
    'il'        => 'whois.isoc.org.il',
    'ke'        => 'whois.kenic.or.ke',
    'ec'        => 'whois.lac.net',
    'my'        => 'whois.mynic.net.my',
    'museum'    => 'whois.meseum',
    'ua'        => 'whois.net.ua',
    'jp'        => 'whois.nic.ad.jp',
    'org'       => 'whois.pir.org',
    'tw'        => 'whois.twnic.net.tw',
    'tv'        => 'whois.www.tv',
    );

=head1 SYNOPSIS

    use cmt::whois;
    mysub(arguments...)

=head1 DESCRIPTION

B<cmt::whois> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-cmt::whois-RESOLVES.

=head1 FUNCTIONS

=cut
=head2 mysub(arguments)

=cut
sub whois_exec {
    my $domain = shift;
    my $resp = `whois $domain`;
    my @lines = split(/\n/, $resp);
    parse_whois sub {  *__ANON__ = '<exec-input>'; shift @lines }
}

sub whois_inet {
    my $domain = shift;
    my ($tld) = $domain =~ m/\.([^\.]+)$/;
    my $server = $TLD_SERVER{$tld} || "whois.nic.$tld";

    die "WHOIS server undefined for TLD: $tld" unless defined $server;
    my @sendbuf = ($domain."\n", );
    my @recvbuf;
    my $eof;
    my $cn;
    my $try = 0;
    for (my $try = 0; $try < 10; $try++) {
        $cn = tcp_connect($server, 43, new cmt::stream(
            -binded => sub { *__ANON__ = '<binded>';
                my $s = shift;
                $s->write(shift @sendbuf);
            },
            -unbinded => sub { *__ANON__ = '<unbinded>';
                $eof = 1;
            },
            -gotdata => sub { *__ANON__ = '<gotdata>';
                my ($s, $data) = @_;
                # _sig2 'recv', $data;
                push @recvbuf, split(/\n/, $data);
            },
        )) and last;
    }
    die "failed to connect: $!" unless defined $cn;

    my $ctx = $cn->create_context;
    parse_whois sub { *__ANON__ = '<inet-input>';
        unless (@recvbuf) {
            for (my $iters = 0; $iters < 100; $iters++) {
                # _sig2 'iter', $iters;
                $ctx->iterate();
                last if @recvbuf;
            }
            die "timeout when wait to recv" unless @recvbuf;
        }
        shift @recvbuf
    }
}

sub parse_whois {
    my $input = shift;
    my $info = { domain => '[ ??? ]' };
    local $_;
    while (defined ($_ = $input->(@_))) {
        s/\s+$//g;
        _sig2 'parse', $_;
        if (m/^\s*Domain Name:\s*(.*)$/i) {
            $info->{domain} = $1;
        } elsif (m/^\s*Registrar:\s*(.*)$/i) {
            $info->{reg} = $1   unless defined $info->{reg};
        } elsif (m/^\s*Registrant Name:\s*(.*)$/i) {
            $info->{reg} = $1   unless defined $info->{reg};
        } elsif (m/^\s*Registrant Organization:\s*(.*)$/i) {
            $info->{reg} = $1   unless defined $info->{reg};
        } elsif (m/^\s*Name Server:\s*(.*)$/i) {
            $info->{dns0} = $1  unless defined $info->{dns0};
        } elsif (m/Registration Date:\s*(.*)$/i) {
            $info->{create} = $1;
        } elsif (m/Expiration Date:\s*(.*)$/i) {
            $info->{expire} = $1;
        } elsif (m/^No match(ing)?\b/i or m/^Not found\b/i) {
            $info = {};
            $info->{domain} = '[ empty ]';
            last;
        } elsif (m/^>>> /) {
            last;
        }
    }
    $info
}

=head1 DIAGNOSTICS

(No Information)

=cut
# (HELPER FUNCTIONS)

=head1 HISTORY

=over

=item 0.x

The initial version.

=back

=head1 SEE ALSO

The L<cmt/"replace Net::Whois">

=head1 AUTHOR

Xima Lenik <name@mail.box>

=cut
1