#
# DNA forming
#

package cmt::gene;

use strict;
use Exporter;
use vars qw/@ISA @EXPORT/;


my @def_pad = ( 1, 0, 0, 0, 1, 2, 3, 4, 5, 5, 4, 3, 2, 1, 0, 0, 0, 1, );
my @def_len = ( 0, 2, 3, 4, 4, 4, 3, 2, 0, 0, 2, 3, 4, 4, 4, 3, 2, 0, );
my $def_num = scalar(@def_pad);
my $def_pos = -1;
my @def_set = qw/A T C G/;
my @def_setc= qw/T A G C/;

sub def_encoder {
	$def_pos = ($def_pos + 1) % $def_num;
	return ($def_pad[$def_pos], $def_len[$def_pos]);
};


sub Encode {
	my ($data, $encoder) = @_;
	    $encoder ||= \&def_encoder;
	my $datalen = length($data);
	my $contoff = 0;

	my @ret;
	my $print = wantarray == undef;

	for (my $i = 0; $i < $datalen; $i++) {
		my $vc = substr($data, $i, 1);
		my $v = ord $vc;
		for (my $j = 0; $j < 4; $j++) {
			my ($pad, $blen) = &$encoder;
			my $bit2 = (($v >> 6) + $contoff++) & 3;
			$v <<= 2;
			my ($c, $cc) = ($def_set[$bit2], $def_setc[$bit2]);
			my $line = ' 'x$pad . $c . '-'x$blen . $cc . "\n";

			if ($print) {
			    print $line;
			} else {
			    push @ret, $line;
			}
		}
	}
	return @ret;
}


sub DNA {
    my @bit2s;
       @bit2s = split("\n", shift);
    my @lines;
    my $line = '';
    my $bit2i = 0;
    my $v = 0;
    my $contoff_rev = 0;
    my $copymode = 0;

    while (@bit2s) {
        my $bit2 = shift @bit2s;

        if ($copymode) {
            print "$bit2\n";
            $copymode = 0;
            next;
        }

        $bit2 =~ s/^\s+//;
        next if not $bit2;
        next if $bit2 =~ m/^[\<\#]/;

        my $b2 = index('ATCG', substr($bit2, 0, 1));
            $b2 = ($b2 + $contoff_rev) & 3;
            if (--$contoff_rev < 0) {
                $contoff_rev = 3;
            }
        $v <<= 2;
        $v |= $b2;
        if ($bit2i >= 3) {
            # one byte composed
            $bit2i = 0;
            if ($v == 13) {
            } elsif ($v == 10) {
                if ($line =~ m/^\#\@DNAC::(\w+)\s+(.*)$/) {
                    my ($_cmd, $_args) = ($1, $2);
                    if ($_cmd eq 'COPY') {
                        $copymode = 1;
                    }
                }
                push @lines, $line;
                $line = '';
            } else {
                $line .= chr $v;
            }
            $v = 0;
        } else {
            $bit2i++;
        }
    }
    if ($line) {
        push @lines, $line;
        $line = '';
    }

    if (defined(wantarray)) {
        if (wantarray) {
            return @lines;
        } else {
            return join("\n", @lines);
        }
    } else {
        # void-context, global last-eval
        my $program = join("\n", @lines);
        if (!defined(eval($program))) {
            print "Gene Error: $@\n";
        }
    }
}



@ISA = qw/Exporter/;
@EXPORT = qw/Encode DNA/;


__END__


Example

use gene;

DNA << DNA;
	AT
	C G
	...
DNA
