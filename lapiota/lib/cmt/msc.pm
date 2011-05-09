# MsScriptControl

package cmt::msc;

use strict;
use vars        qw/@ISA @EXPORT/;
use Exporter;
use Win32::OLE;

my $SC_VBS;
my $SC_JS;
sub VBS {
    unless (defined $SC_VBS) {
        $SC_VBS = Win32::OLE->new('MSScriptControl.ScriptControl');
        $SC_VBS->{Language} = 'vbscript';
    }
    return $SC_VBS;
}

sub JS {
    unless (defined $SC_JS) {
        $SC_JS = Win32::OLE->new('MSScriptControl.ScriptControl');
        $SC_JS->{Language} = 'javascript';
    }
    return $SC_JS;
}

sub vbs_eval    { return VBS->Eval(shift); }
sub vbs_exec    { return VBS->ExecuteStatement(shift); }
sub vbs_reset   { VBS->Reset; }
sub js_eval     { return JS->Eval(shift); }
sub js_exec     { return JS->ExecuteStatement(shift); }
sub js_reset    { JS->Reset; }

sub END {
    if (defined $SC_VBS) {
        # Clean up vbscript-control
        $SC_VBS = undef;
    }
    if (defined $SC_JS) {
        # Clean up javascript-control
        $SC_JS = undef;
    }
    # Win32::OLE->uninitialize;
}

@ISA = qw(Exporter);
@EXPORT = qw(
	vbs_eval
	vbs_exec
	vbs_reset
	js_eval
	js_exec
	js_reset
);

1;
