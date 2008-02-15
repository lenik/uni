package Net::Adapter;

=head1 NAME

Net::Adapter - DeScRiPtIoN

=cut
use 5.008;
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
use Win32::Registry;

our @ISA    = qw(Exporter);
our @EXPORT = qw(enum_adapters
                 netcon_connect
                 netcon_disconnect
                 netcon_reset
                 chmac
                 );

# INITIALIZORS

our $VERSION = '0.02';

require XSLoader;
XSLoader::load('Net::Adapter', $VERSION);

=head1 SYNOPSIS

    use Net::Adapter;
    mysub(arguments...)

=head1 DESCRIPTION

B<Net::Adapter> is a WHAT used for WHAT. It HOW-WORKS.

BACKGROUND-PROBLEM.

HOW-Net::Adapter-RESOLVES.

=head1 FUNCTIONS

=cut
=head2 mysub(arguments)

=cut
sub check_hr {
    my $hr = shift;
    if ($hr != 0) {
        _log2 "failed: $hr";
    }
    return $hr == 0;
}

sub netcon_reset {
    my $id = shift;
    my $hr;
    check_hr(netcon_disconnect($id)) or return;
    check_hr(netcon_connect($id)) or return;
    1
}

my $CC_Net;
sub get_registry {
    my $id = lc shift;

    unless (defined $CC_Net) {
        $::HKEY_LOCAL_MACHINE->Open(
            "SYSTEM\\CurrentControlSet\\Control\\Class\\{4D36E972-E325-11CE-BFC1-08002bE10318}",
            $CC_Net) or die "Can't open registry of [Control-Class Net]";
    }

    my @seq;
    $CC_Net->GetKeys(\@seq) or die "can't enum network-adapters: $!";
    for (@seq) {
        my $inst;
        next unless $CC_Net->Open($_, $inst);
        my $inst_id;
        $inst->QueryValueEx("NetCfgInstanceId", REG_SZ, $inst_id)
            or next;
        if (lc($inst_id) eq $id) {
            return $inst;
        }
        $inst->Close;
    }
    return undef;
}

sub chmac {
    my ($id, $mac) = @_;

    my $reset = 0;

    my $inst = get_registry($id)
        or die "can't find the config-entry in the registry: $!";

    # @DriverDesc        = REG_SZ Hamachi Network Interface
    # @NetCfgInstanceId  = REG_SZ $id   {05CB1FA2-26F4-4936-BE55-FC1860AB4C6F}
    # @NetworkAddress    = REG_SZ $mac  7A7905454D71 (delete if $mac is null)

    $mac =~ s/:-\s//g if defined $mac;

    my $mac0;
    $inst->QueryValueEx("NetworkAddress", REG_SZ, $mac0);
    _log2 "Old MAC-Override: $mac0" if defined $mac0;

    if (defined $mac) {
        if (!defined $mac0 or $mac ne $mac0) {
            _log2 "set $inst/\@NetworkAddress";
            $inst->SetValueEx("NetworkAddress", undef, REG_SZ, $mac)
                or die "can't set value of NetworkAddress: $!";
            $reset = 1;
        }
    } else {
        _log2 "delete $inst/\@NetworkAddress";
        if ($inst->DeleteValue("NetworkAddress")) {
            $reset = 1 if defined $mac0;
        }
    }

    $inst->Close;

    if ($reset) {
        _log2 "netcon_reset $id";
        return netcon_reset($id);
    }
    return 1;
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