package cmt::ipc;

use strict;
use Exporter;
use vars qw/@ISA @EXPORT/;

use IO::Handle;
use IO::Select;

sub DBG {
    my $msg = shift;
    print $msg;
}

sub J {
    my $delim = shift;
    my $buf;
    for (@_) {
        $buf .= $delim if (defined $buf);
        if (defined $_) {
            $buf .= $_;
        } else {
            $buf .= '-';
        }
    }
    return $buf;
}

sub io_loop;
sub io_proc_stat;
sub _close;

# config:
#    -timeout=<n, default 10000>
#    -proc=<\&io_proc, default io_proc_stat>
#    -title=<s, title of default io_proc>
# io_proc(output, error, [wait_for_input]): input
sub io_loop {
    my ($in_write, $out_read, $err_read, %config) = @_;
    DBG "in,out,err=(".J(',', $in_write, $out_read, $err_read).")\n";
    my $title = $config{-title} || 'No-Title';
    my $timeout = $config{-timeout} || 10.000;
    my %proc_vars = ( title=>$title, in=>0, out=>0, err=>0 );
    my $proc = $config{-proc} || sub { return io_proc_stat(@_, \%proc_vars) };
    my $select = new IO::Select;
    my $errmsg;
    my @sendbuf;
    my $in_end;
    $select->add($in_write) if $in_write;
    $select->add($out_read) if $out_read;
    $select->add($err_read) if $err_read;
    while (1) {
        my @oie = $select->select($select, $select, $select, $timeout);
        if (! @oie) {
            $errmsg = "select error";
            DBG $errmsg;
            last;
        }
        my ($outs, $ins, $errs) = @oie;
        my @ins  = @$ins;
        my @outs = @$outs;
        my @errs = @$errs;
        DBG "selected(i$#ins, o$#outs, e$#errs)\n";
        my ($ih, $oh, $eh) = ($ins[0], $outs[0], $errs[0]);
        DBG "    list(".J(', ', J(':',@ins), J(':',@outs), J(':',@errs)).")\n";
        DBG "   first(".J(', ', $ih, $oh, $eh).")\n";
        my $wait_in = defined $ih;
        my $out  = <$oh> if defined $oh;
        my $err  = <$eh> if defined $eh;
        DBG '    proc('.J(', ', $out, $err, $wait_in).")\n";
        my $in_send = &$proc($out, $err, $wait_in);
        if (! $in_end) {
            if (defined $in_send) {
                push @sendbuf, $in_send if length($in_send);
            } else {
                $in_end = 1;
            }
        }
        if (defined $ih) {
            if (@sendbuf) {
                my $send = join('', @sendbuf);
                @sendbuf = ();
                DBG "sending: $send";
                print $ih $send;
            }
            if ($in_end) {
                $select->remove($ih);
                _close $ih;
            }
        }
    }
}

sub io_proc_stat {
    my ($output, $error, $wait, $vars) = @_;
    if ($output) {
        # print "out-$output";
        $vars->{out}++;
        $vars->{last_out} = $output;
    }
    if ($error) {
        # print "err-$error";
        $vars->{err}++;
    }
    if ($wait) {
        $vars->{in}++;
        return "$vars->{title} - in:$vars->{in}, out:$vars->{out}, err:$vars->{err}\n";
    }
}

sub _close {
    my $h = shift;
    DBG "shutdown $h, 2";
    eval 'shutdown $h, 2';
    #close $h;
}

@ISA = qw(Exporter);
@EXPORT = qw(
             io_proc_stat
             io_loop
             );

1;
