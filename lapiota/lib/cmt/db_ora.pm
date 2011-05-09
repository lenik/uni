package cmt::db_ora;

use strict;
use Exporter;
use vars qw/@ISA @EXPORT/;
use Win32::TieRegistry(Delimiter=>'/');
use DBI;

my $REG_ORACLE = "LMachine/SOFTWARE/ORACLE/";

my $ODBC_DRIVER =
    'Microsoft ODBC for Oracle';
    #'Oracle in OraHome92';

sub ora_home {
    return $Registry->{$REG_ORACLE}->{'/ORACLE_HOME'} || 'C:\oracle\ora92';
}

sub ora_tnsnames {
    my $orahome = ora_home;
    my $path = "$orahome/network/ADMIN/tnsnames.ora";
    open(FILE, "<$path")
        or die("Can't open $path for read");
    my @lines = <FILE>;
    close FILE;
    my $script = join('', @lines);
    my @names;
    while ($script =~ m/(\w+) \s* = \s* \(DESCRIPTION\b/gx) {
        push @names, $1;
    }
    return @names;
}

sub ora_connect {
    my ($sid, $uid, $pwd) = @_;
    my $path =
        "dbi:ODBC:DRIVER={$ODBC_DRIVER};SERVER=$sid";
        # "dbi:ODBC:DRIVER={$ODBC_DRIVER};DBQ=$sid";
    return DBI->connect($path, $uid || 'SYSTEM', $pwd || 'SYSTEM', {
        RaiseError => 1,
        AutoCommit => 1});
}

sub ora_disconnect {
    my $h = shift;
    $h->disconnect;
}

sub ora_tab {
    my $h = shift;
    my $filter = shift || 'TABLE';
    $filter = " where TABTYPE='$filter'" if $filter;
    my $st = $h->prepare('select TNAME from TAB' . $filter);
    $st->execute;
    my @tab;
    while (my @row = $st->fetchrow_array) {
        push @tab, $row[0];
    }
    return @tab;
}

@ISA = qw(Exporter);
@EXPORT = qw(
        ora_home
	ora_tnsnames
	ora_connect
	ora_disconnect
	ora_tab
	);

1;
