
package Net::Adapter;

use 5.008;
use strict;
use warnings;
use cmt::util;
use Exporter;
use Win32::Registry;

our @ISA = qw(Exporter);

our %EXPORT_TAGS = ( 'all' => [ qw(

) ] );

our @EXPORT_OK = ( @{ $EXPORT_TAGS{'all'} } );

our @EXPORT = qw(
	enum_adapters
	netcon_connect
	netcon_disconnect
	netcon_reset
	chmac
);

our $VERSION = '0.02';

require XSLoader;
XSLoader::load('Net::Adapter', $VERSION);

sub info;
sub info2;

our $opt_verbtitle      = __PACKAGE__;
our $opt_verbtime       = 0;
our $opt_verbose        = 1;

sub check_hr {
    my $hr = shift;
    if ($hr != 0) {
        info2 "failed: $hr";
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
    info2 "Old MAC-Override: $mac0" if defined $mac0;

    if (defined $mac) {
        if (!defined $mac0 or $mac ne $mac0) {
            info2 "set $inst/\@NetworkAddress";
            $inst->SetValueEx("NetworkAddress", undef, REG_SZ, $mac)
                or die "can't set value of NetworkAddress: $!";
            $reset = 1;
        }
    } else {
        info2 "delete $inst/\@NetworkAddress";
        if ($inst->DeleteValue("NetworkAddress")) {
            $reset = 1 if defined $mac0;
        }
    }

    $inst->Close;

    if ($reset) {
        info2 "netcon_reset $id";
        return netcon_reset($id);
    }
    return 1;
}

# utilities

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

1;
__END__
# Below is stub documentation for your module. You'd better edit it!

=head1 NAME

Net::Adapter - Perl extension for blah blah blah

=head1 SYNOPSIS

  use Net::Adapter;
  blah blah blah

=head1 DESCRIPTION

Stub documentation for Net::Adapter, created by h2xs. It looks like the
author of the extension was negligent enough to leave the stub
unedited.

Blah blah blah.

=head2 EXPORT

None by default.



=head1 SEE ALSO

Mention other useful documentation such as the documentation of
related modules or operating system documentation (such as man pages
in UNIX), or any relevant external documentation such as RFCs or
standards.

If you have a mailing list set up for your module, mention it here.

If you have a web site set up for your module, mention it here.

=head1 AUTHOR

A. U. Thor, E<lt>a.u.thor@a.galaxy.far.far.awayE<gt>

=head1 COPYRIGHT AND LICENSE

Copyright (C) 2007 by A. U. Thor

This library is free software; you can redistribute it and/or modify
it under the same terms as Perl itself, either Perl version 5.8.8 or,
at your option, any later version of Perl 5 you may have available.


=cut
