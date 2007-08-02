package fun::asknet;

use strict;
use cmt::netutil;
use cmt::pp;
use cmt::util;
use cmt::utree;
use cmt::vcs;
use fun::_booter(':info');
use Data::Dumper;
use Exporter;
use Getopt::Long;
use LWP::Simple;

our @ISA                = qw(Exporter);
our @EXPORT             = qw(where
                             );

sub boot;
sub info;
sub info2;
sub version;
sub help;

our $opt_verbtitle      = 'asknet';
our $opt_verbtime       = 0;
our $opt_verbose        = 1;

sub boot {
    GetOptions('quiet|q'    => sub { $opt_verbose-- },
               'verbose|v'  => sub { $opt_verbose++ },
               'version'    => sub { version; exit },
               'help|h'     => sub { help; exit },
               );
}

sub info {
    return if $opt_verbose < 1;
    my $text = shift;
    print datetime.' ' if $opt_verbtime;
    print "[$opt_verbtitle] $text\n";
}

sub info2 {
    return if $opt_verbose < 2;
    my $text = shift;
    print datetime.' ' if $opt_verbtime;
    print "[$opt_verbtitle] $text\n";
}

sub version {
    my %id = parse_id('$Id: asknet.pm,v 1.2 2007-08-02 22:49:15 lenik Exp $');
    print "[$opt_verbtitle] find specific answers from the internet \n";
    print "Written by Lenik,  Version $id{rev},  Last updated at $id{date}\n";
}

sub help {
    version unless shift;
    print <<"EOM";

Syntax:
    $funpack->{name} ~command <options>...

Common options:
    -h, --help              show this help page
    -q, --quiet             repeat to get less info
    -v, --verbose           repeat to get more info
        --version           print the version info

Commands:
    ~where NUM      find address info about a number (phone, etc.)
EOM
}

sub netdb_query($\%) {
    my ($entry, $vars) = @_;
    my $url = ppvar %$vars, $entry->{url};
    my $post;
       $post = ppvar %$vars, $entry->{post} if defined $entry->{post};
    info2 "loadurl: $url".(defined $post ? " (post: $post)" : '');
    my $cnt = loadurl($url, $post);
    my $res;
    if (defined $cnt) {
        my $t = new cmt::utree();
        $t->parse($cnt);
        $t->eof;
        my $sel = $entry->{sel};
        for (keys %$sel) {
            my ($addr, $part) = split(':', $sel->{$_});
            my $node = $t->address($addr);
            my $val;
            if ($part ne '') {
                if ($node->attr($part)) {
                    $val = $node->attr($part);
                } else {
                    # die "unknown part";
                }
            } else {
                $val = $node->as_text();
            }
            $res->{$_} = $val;
        }
    }
    return $res;
}

my $where_db = {
    'phone:imobile' => {
        url => 'http://www.imobile.com.cn/search2005.php?searchkeyword=$what',
        sel => {
            echo    => '@0.1.17.0.0.0.0.0.0.1.1.1',
            loc     => '@0.1.17.0.0.0.0.0.0.2.1.1',
            ctype   => '@0.1.17.0.0.0.0.0.0.3.1.1',
            po      => '@0.1.17.0.0.0.0.0.0.4.1.1',
            pzone   => '@0.1.17.0.0.0.0.0.0.5.1.1',
        }},
    '.phone:ip138' => {
        url => 'http://www.ip138.com:8080/search.asp',
        post=> 'action=mobile&mobile=$what',
        sel => {
            echo    => '@0.1.4.1.1',
            loc     => '@0.1.4.2.1',
            ctype   => '@0.1.4.3.1',
        }},
    };
sub where {
    my $what = shift;
    my %vars = (
        what    => $what,
    );
    for (keys %$where_db) {
        next if /^\./;
        my $entry = $where_db->{$_};
        my $res   = netdb_query($entry, %vars);
        print "$_\n\t";
        for (keys %$res) {
            print "$_=$res->{$_} ";
        }
        print "\n";
    }
}

1