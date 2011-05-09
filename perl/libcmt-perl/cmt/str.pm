package cmt::str;

=head1 NAME

cmt::str - String Utilities

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
use cmt::log(2);
    our $LOGNAME    = __PACKAGE__;
    our $LOGLEVEL   = 1;
use cmt::util();
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id$');
    our $VER    = "0.$RCSID{rev}";
use Exporter;

our @ISA    = qw(Exporter);
our @EXPORT = qw(trim
                 ltrim
                 rtrim
                 indent
                 unindent_
                 unindent
                 abbrev
                 );

# INITIALIZORS

=head1 SYNOPSIS

    use cmt::str;
    mysub(arguments...)

=head1 String Utilities

B<cmt::str> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-cmt::str-RESOLVES.

=head1 FUNCTIONS

=cut
=head2 mysub(arguments)

=cut
sub trim {
    if (@_) {
        trim() for @_;
    } else {
        s/^\s+//s;
        s/\s+$//s;
        return $_;
    }
}

sub ltrim {
    if (@_) {
        ltrim() for @_;
    } else {
        s/^\s+//s;
        return $_;
    }
}

sub rtrim {
    if (@_) {
        rtrim() for @_;
    } else {
        s/\s+$//s;
        return $_;
    }
}

sub indent {
    my $prefix = shift;
        $prefix = ' 'x$prefix if $prefix =~ /^\d+$/;
    my @lines = split(/\n/, shift);
    join("\n", map { $prefix.$_ } @lines);
}

sub unindent_ {
    my $len     = shift;
    my @lines   = scalar @_ > 1 ? @_ : split(/\n/, shift);
    if ($len <= 0) {
        my ($s) = ($lines[0] =~ /^(\s*)/);
        $len    = length $s;
    }
    my $pattern = qr/^\s{1,$len}/;
    join("\n", map { s/$pattern//; $_ } @lines);
}

sub unindent {
    unindent_ 0, @_
}

sub abbrev {
    my $maxlen = shift;
    my $text = join(@_);
    $text =~ s/\n/ /g;
    if (length $text > $maxlen) {
        substr($text, $maxlen - 5) = '...';
    }
    return $text;
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

The L<cmt/"Perl_simple_module_template">

=head1 AUTHOR

Xima Lenik <name@mail.box>

=cut
1