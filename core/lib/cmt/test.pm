
package cmt::test;

use strict;
use Exporter;
use vars qw/@ISA @EXPORT/;


sub is_same {
    my ($a, $b) = @_;
    my ($r, $rb) = (ref $a, ref $b);

    # scalar-reference & scalar
    if (not $r and not $rb) {
        return $a eq $b;
    }
    if ($r eq 'SCALAR' and not $rb) {
        return is_same($$a, $b);
    }
    if ($rb eq 'SCALAR' and not $r) {
        return is_same($a, $$b);
    }
    if ($r ne $rb) {
        return 0;
    }
    if ('SCALAR' eq $r) {
        return $$a eq $$b;
    }

    # compare depends on ref-type
    if ('REF' eq ref $r) {
        return is_same($$a, $$b);
    }
    if ('ARRAY' eq $r) {
        my ($la, $lb) = (scalar @$a, scalar @$b);
        return 0 if $la != $lb;
        for (my $i = 0; $i < $la; $i++) {
            return 0 if not is_same(\$a->[$i], \$b->[$i]);
        }
        return 1;
    }
    if ('HASH' eq $r) {
        my %ha = %$a;
        my %hb = %$b;
        my @ka = keys %ha;
        my $na = scalar @ka;
        my $nb = scalar keys %hb;
        return 0 if $na != $nb;
        for (my $i = 0; $i < $na; $i++) {
            my $key = $ka[$i];
            my $va = \$ha{$key};
            my $vb = \$hb{$key};
            return 0 if not is_same($va, $vb);
        }
        return 1;
    }
    if ('Regexp' eq $r) {
        return $a eq $b;
    }
    die "Not supported reference type: $r";
}


sub ref_detail {
    my $val = shift;
    my $r = ref $val;
    return $val if (not $r);
    return $$val if ($r eq 'SCALAR');
    if ($r eq 'ARRAY') {
        return '['.join(',', @$val).']';
    }
    if ($r eq 'HASH') {
        my $buf;
        my @k = keys %$val;
        for (@k) {
            $buf .= ',' if $buf;
            $buf .= "$_=>".$val->{$_};
        }
        return "{$buf}";
    }
    if ($r eq 'Regexp') {
        return $val;
    }
    die "Not supported reference type: $r";
}


sub test_batch {
    my ($batch) = @_;
    my $batch_total = scalar(@$batch);
    my $batch_index = 1;
    my $batch_ok = 0;
    my $c_total = 0;
    my $c_ok = 0;

    foreach (@$batch) {
        my $type = $_->{'type'};
        my $name = $_->{'name'};
        my $entry = $_->{'entry'};
        my $cases = $_->{'cases'};
        my $total = scalar(@$cases)/2;
        my $ok = 0;

        print "UNIT$batch_index>> $name ($total cases)\n";

        # ?? Not used ??
        my ($args) = $entry =~ s/(\(.*\))//;
        if ($args) {
            $args =~ s/\((.*)\)/$1/;        # remove '(' and ')'
            $args =~ s/^\s*(.*?)\s*$/$1/;   # trim space
            $args = length($args);          # get args count
        } else {
            $args = 0;
        }

        my $case_index = 0;
        for (; $case_index < $total; $case_index++) {
            my $input = $cases->[$case_index*2];
            my $expect = $cases->[$case_index*2+1];
            my $numhead = "    $batch_index.".(1+$case_index); #."/$total";

            print $numhead.(' 'x(10-length($numhead)));

            # ?? Why the @$input can't be evaluated correctly without '&' ??
            my $eval_body = '&'.$entry.'(@$input)'."\n";

            my $expect_ref = ref $expect;
            my $out;
            if (not $expect_ref or 'SCALAR' eq $expect_ref) {
                $out = eval($eval_body);
            }
            elsif ('Regexp' eq $expect_ref) {
                $out = eval($eval_body);
            }
            elsif ('ARRAY' eq $expect_ref) {
                my @out_array = eval($eval_body);
                $out = \@out_array;
            }
            elsif ('HASH' eq $expect_ref) {
                my %out_hash = eval($eval_body);
                $out = \%out_hash;
            }

            if ($@) {
                print "ERR: $@\n";
            } else {
                if (is_same($out, $expect)) {
                    $ok++;
                    print "OK\n";
                } else {
                    print "<".ref_detail($out).">\n"
                         ."          ERR: expected to be: <".ref_detail($expect).">\n";
                }
            }
        }

        if ($ok == $total) {
            print "\n";
            $batch_ok++;
        } else {
            print "!!! Unit failure, ".($total-$ok)." cases failed. \n\n";
        }
        $c_total += $total;
        $c_ok += $ok;
        $batch_index++;
    }

    my ($rate_batch, $rate_case)
        = (1000*$batch_ok/$batch_total, 1000*$c_ok/$c_total);
    $rate_batch = int($rate_batch)/10;
    $rate_case  = int($rate_case )/10;

    print <<"EOM";
    Test Result
===============================================================
    Total units:       $batch_total
    Passed units:      $batch_ok
    Total cases:       $c_total
    Passed cases:      $c_ok
    Unit passed rate:  $rate_batch%
    Case passed rate:  $rate_case%
EOM
    if (wantarray) {
        return $batch_ok == $batch_total;
    } else {
        return ($batch_total, $batch_ok, $c_total, $c_ok);
    }
}


@ISA = qw(Exporter);
@EXPORT = qw(
    is_same
    ref_detail
    test_batch
    );

1;
