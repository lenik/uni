package cmt::subst;

=head1 NAME

cmt::subst - Command line substition support

=cut
use strict;
use vars qw($LOGNAME $LOGLEVEL);
use cmt::log(2);
    our $LOGNAME    = __PACKAGE__;
    our $LOGLEVEL   = 1;
use cmt::util();
use cmt::vcs('parse_id');
    my %RCSID   = parse_id('$Id: perllib.pm -1   $');
    our $VER    = "0.$RCSID{rev}";
use Exporter;

our @ISA    = qw(Exporter);
our @EXPORT = qw(subst_init
                 subst_get
                 subst_keyof
                 subst
                 rsubst
                 $opt_color
                 );

sub subst_init();
sub subst_get($);
sub subst_keyof($);
sub subst($);
sub rsubst($);

sub add_subst($$$);
sub qrkeys($$);
sub load_substs($$);

our $opt_color = 0;

our %subst_map;
our %subst_qrkeys;
our %rindex_map;
our %rindex_qrkeys;

=head1 SYNOPSIS

    use cmt::subst;
    mysub(arguments...)

=head1 DESCRIPTION

B<cmt::subst> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-cmt::subst-RESOLVES.

=head1 FUNCTIONS

=cut
sub subst_init() {
    load_substdir('', "/etc/coolcmd/subst");
    load_substdir('', "~/.coolcmd/subst");

    qrkeys(\%subst_map,  \%subst_qrkeys);
    qrkeys(\%rindex_map, \%rindex_qrkeys);
}

sub subst_get($) {
    my $key = shift;
    $subst_map{$key}
}

sub subst_keyof($) {
    my $val = shift;
    $rindex_map{$val}
}

sub subst($) {
    my $text = shift;
    for my $key (keys %subst_map) {
        my $val = $subst_map{$key};
        my $qrkey = $subst_qrkeys{$key};
        $text =~ s/$qrkey/$val/g;
    }
    return $text;
}

sub rsubst($) {
    my $text = shift;
    for my $val (keys %rindex_map) {
        my $keys = $rindex_map{$val};

        my $key_text = $keys->[0];
        if (scalar(@{$keys}) > 1) {
            # $key_text = '{ '.join(', ', @{$keys}).' }';
            # $key_text = '{'.$keys->[0].', ...}';
        }

        my $val_re = $rindex_qrkeys{$val};

        if ($opt_color) {
            $text =~ s/$val_re/[01;36m$key_text[0m/g;
        } else {
            $text =~ s/$val_re/$key_text/g;
        }
    }
    return $text;
}

=head1 DIAGNOSTICS

(No Information)

=cut
# (HELPER FUNCTIONS)
sub add_subst($$$) {
    my ($fqn, $key, $val) = @_;
    my $fkey = $fqn.'['.$key.']';
    $subst_map{$fkey} = $val;
    $subst_map{$fqn}  = $val unless defined $subst_map{$fqn};

    if (substr($key, 0, 1) ne '.') {
        my $rkeys = $rindex_map{$val};
        if (! defined $rkeys) {
            $rkeys = $rindex_map{$val} = [];
        }
        push(@{$rkeys}, $fkey);
    }
}

sub qrkeys($$) {
    my $map = shift;
    my $map_regex = shift;
    for my $k (keys %{$map}) {
        my $regex = $k;
        $regex =~ s/([\\\[\]\/\"\'.?*+\(\)^\$])/\\$1/g;
        if ($k =~ /\]$/) {
            $map_regex->{$k} = qr/\b$regex/;
        } else {
            $map_regex->{$k} = qr/\b$regex\b(?!\[)/;
        }
    }
}

sub load_substdir($$) {
    my $prefix = shift;
    my $dir = shift;

    for my $f (<$dir/*>) {
        my ($dirname, $basename) = $f =~ m{^(.*?)([^/]+)$};
        my $fqn = ($prefix eq '') ? $basename : "$prefix:$basename";

        if (-d $f) {
            load_substdir($fqn, $f);
        } elsif (-f $f) {
            open(IN, "<$f") or die "Can't open $f: ";
            while (<IN>) {
                s/^\s+//;
                next if $_ eq '';
                next if /^#/;
                my ($key, $val) = /^(.*?)\s*=\s*(.*)$/;
                add_subst($fqn, $key, $val);
            }
            close IN;
        }
    }
}

=head1 HISTORY

=over

=item 0.x

The initial version.

=back

=head1 SEE ALSO

The L<cmt/"Perl_simple_module_template">

=head1 AUTHOR

lenik <lenik (at) bodz.net>

=cut
1
