package fun::i18n;

use strict;
use cmt::util;
use cmt::vcs;
use Getopt::Long;
use Exporter;
use vars qw/@ISA @EXPORT/;

sub boot;
sub info;
sub info2;
sub version;
sub help;

our $PACKAGE            = ':i18n';

our $opt_verbtitle      = 'i18n';
our $opt_verbtime       = 0;
our $opt_verbose        = 1;
our $opt_file;

sub boot {
    GetOptions('quiet|q'    => sub { $opt_verbose-- },
               'verbose|v'  => sub { $opt_verbose++ },
               'version'    => sub { version; exit },
               'help|h'     => sub { help; exit },
               'file=s',
               );
}

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

sub version {
    my %id = parse_id('$Id$');
    print "[$opt_verbtitle] I18n utilities\n";
    print "Written by Lenik,  Version $id{rev},  Last updated at $id{date}\n";
}

sub help {
    version;
    print <<"EOM";

Syntax:
        fun ~$0 <options> ...

Options:
        --quiet (q)
        --verbose (v, repeat twice give you more verbose info)
        --version
        --help (h)
EOM
}

sub is_utf8 {
    $_ = <>;
    my $bom = m/^\xEF\xBB\xBF/;
    info "found utf-8 bom (EFBBBF)" if $bom;
    return $bom;
}

@ISA = qw(Exporter);
@EXPORT = qw(
	is_utf8
	);

1;
