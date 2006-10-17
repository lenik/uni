
package cmt::echohh;

use strict;
use LWP::UserAgent;
use HTTP::Response;
use XML::Simple;
use Exporter;
use vars qw/@ISA @EXPORT/;

sub xsmerge;
sub verbose;

# args:
#   method  => $string 'GET' or 'POST'
#   form    => \%hash { $name => $value }
#   headers => \%hash { $name => $value }
#   content => $string
#
# returns:
#   $string:    error text
#   \%hash:     info in sections
sub echohh {
    my ($url, %args) = @_;

    verbose "[echohh] $url\n";

    my $method = $args{'method'} || 'GET';
    my $formref = $args{'form'} || {};

    my $_headers = $args{'headers'} || {};
    my %headers = %$_headers;

    # Not used.
    my $content = $args{'content'} || '';

    my $agent = LWP::UserAgent->new();
    my $response;

    if ($method eq 'GET') {
        $response = $agent->get($url, %headers);
    } elsif ($method eq 'POST') {
        $response = $agent->post($url, $formref, %headers);
    } else {
        die "Invalid method: $method, neither GET nor POST. ";
    }

    if ($response->is_error) {
        verbose "[echohh] " . $response->status_line . "\n";
        return $response->status_line;
    }

    my $title = $response->title || "Unknown";
    $content = $response->content;
    my $headers = $response->headers;

    my %HEADERS = %$headers;

    my $ref = XMLin($content);

    my $ENV     = xsmerge($ref->{env});
    my $SERVER  = xsmerge($ref->{server});
    my $REQUEST = xsmerge($ref->{request});
    my $SESSION = xsmerge($ref->{session});
    my $USER    = xsmerge($ref->{user});

    my %ALL = (
        'title'     => $title,
        'headers'   => \%HEADERS,
        'env'       => $ENV,
        'server'    => $SERVER,
        'request'   => $REQUEST,
        'session'   => $SESSION,
        'user'      => $USER,
        );
    return \%ALL;
}


our @ECHOHH_URLS    = (
    'http://localhost/service/echohh.php',
    # 'http://lenik.vicp.net:700/~dansei/service/echohh.php',
    'http://xjl.sourceforge.net/service/echohh.php',
    'http://www.zjjxfp.com/service/echohh.php',
    'http://www.pfpf.net/service/echohh.php',
    );

sub echohh_addservice {
    my $url = shift;
    unshift @ECHOHH_URLS, $url;
}

sub echohh_get {
    my $name = shift || 'REMOTE_ADDR';
    my $section = shift || 'server';

    # copy urls, for random scan
    my @urls = @ECHOHH_URLS;

    # TODO - add random pick algor.
    for my $url (@urls) {
        my $all = echohh($url);
        if (ref $all) {
            my $sect = $all->{$section};
            return $sect->{$name};
        }
    }
    return undef;
}

sub echohh_env      { return echohh_get(shift, 'env'); }
sub echohh_server   { return echohh_get(shift, 'server'); }
sub echohh_request  { return echohh_get(shift, 'request'); }
sub echohh_session  { return echohh_get(shift, 'session'); }
sub echohh_user     { return echohh_get(shift, 'user'); }


sub verbose {
    print @_;
}

sub xsmerge {
    my $items = shift;
    my %hash = ();
    if ($items->{item}) {
        $items = $items->{item};
        for my $k (keys %$items) {
            my $v = $items->{$k}->{content};
            $hash{$k} = $v;
        }
    }
    return \%hash;
}


@ISA = qw(Exporter);
@EXPORT = qw(
	echohh
	echohh_addservice
	echohh_get
	echohh_env
	echohh_server
	echohh_request
	echohh_session
	echohh_user
	);

1;
