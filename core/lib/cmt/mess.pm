package cmt::mess;

=head1 NAME

mess - Print mess information about warnings

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
    $LOGNAME    = __PACKAGE__;
    $LOGLEVEL   = 2;
use cmt::log(2);
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id: .pm,v 1.7 2007-09-14 16:09:45 lenik Exp $');
    our $VER    = "0.$RCSID{rev}";
use Data::Dumper;
use Exporter;

# INITIALIZORS
$SIG{__WARN__} = sub { *__ANON__ = '<mess-warn>';
    require Carp;
    if ($LOGLEVEL > 1) {
        Carp::cluck $_[0];
    } else {
        Carp::carp $_[0];
    }
};

=head1 SYNOPSIS

    use mess;
    mysub(arguments...)

=head1 DESCRIPTION

B<mess> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-mess-RESOLVES.

=head1 HISTORY

=over

=item 0.x

The initial version.

=back

=head1 SEE ALSO

The L<cmt/"Perl_simple_module_template">

=head1 AUTHOR

Xima Lenik <name@mail.box>

=cut
1