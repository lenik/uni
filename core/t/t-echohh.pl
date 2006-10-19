
use strict;
use LWP::UserAgent;
use HTTP::Response;
use HTTP::Cookies;
use XML::Simple;
use Exporter;
use vars qw/@ISA @EXPORT/;
use Data::Dumper;

    my $url = 'http://lenik.vicp.net:700/~dansei/echohh.php';
    # my $url = 'http://xjl.sourceforge.net/service/echohh.php';

    my $agent = LWP::UserAgent->new();
    my $response;

    $response = $agent->get($url);

    print "Error: " . $response->status_line . "\n";

    my $title = $response->title;
    my $content = $response->content;
    my $headers = $response->headers;

    print "Title: " . $title . "\n";
    print "Content-Type: " . $headers->{'content-type'} . "\n";
    # print "Content: " . $content . "\n";


sub xsmerge {
    my $items = shift;
    my %hash = ();
    for my $k (keys %$items) {
        my $v = $items->{$k}->{content};
        $hash{$k} = $v;
    }
    return \%hash;
}

    my $ref = XMLin($content);
    my $remote = $ref->{server}->{item}->{REMOTE_ADDR}->{content};
    # print "Remote: $remote\n";

    my $server = xsmerge($ref->{server}->{item});
    print Dumper($server);

