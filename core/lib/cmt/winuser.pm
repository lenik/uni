
package cmt::winuser;

use strict;
use Exporter;
#use os-portable
use vars qw/@ISA @EXPORT/;


sub escape {
    my $str = shift;
    $str =~ s/\\/\\\\/g;
    $str =~ s/\"/\\\"/g;
    $str =~ s/\'/\\\'/g;
    $str =~ s/\n/\\n/g;
    return $str;
}


my %msgbox_tokens = (
    'ok'        => 0b0000000000000001,
    'cancel'    => 0b0000000000000010,
    'yes'       => 0b0000000000000100,
    'no'        => 0b0000000000001000,
    'abort'     => 0b0000000000010000,
    'retry'     => 0b0000000000100000,
    'ignore'    => 0b0000000001000000,
    '?'         => 0b0000000100000000,
    '!'         => 0b0000001000000000,
    'i'         => 0b0000010000000000,
    'x'         => 0b0000100000000000,
    'modal'     => 0b1000000000000000,
    );

my @msgbox_ids = qw(
    null    ok      cancel  abort   retry   ignore  yes     no
    close   help
    );

sub msgbox {
    my ($msg, $title, @options) = @_;
    $title ||= 'Diret Commons';
    @options = ('ok') if !@options;

    my $com = 0;
    for (@options) {
        die "Invalid option: $_" if (! $msgbox_tokens{$_});
        $com |= $msgbox_tokens{$_};
    }

    my $button = 0;

    my $com1 = $com & 0b1111111;
    if ($com1 == 0b0000001) { $button = 0; }
    if ($com1 == 0b0000011) { $button = 1; $button |= 0x100 if ($options[0] eq 'cancel'); }
    if ($com1 == 0b1110000) { $button = 2; $button |= 0x100 if ($options[0] eq 'retry');
                                           $button |= 0x200 if ($options[0] eq 'ignore'); }
    if ($com1 == 0b0001110) { $button = 3; $button |= 0x100 if ($options[0] eq 'no');
                                           $button |= 0x200 if ($options[0] eq 'cancel'); }
    if ($com1 == 0b0001100) { $button = 4; $button |= 0x100 if ($options[0] eq 'no'); }
    if ($com1 == 0b0010010) { $button = 5; $button |= 0x100 if ($options[0] eq 'cancel'); }

    $button |= 0x10 if $com & $msgbox_tokens{'x'};
    $button |= 0x20 if $com & $msgbox_tokens{'?'};
    $button |= 0x30 if $com & $msgbox_tokens{'!'};
    $button |= 0x40 if $com & $msgbox_tokens{'i'};

    $button |= 0x1000 if $com & $msgbox_tokens{'modal'};

    $msg = escape $msg;
    $title = escape $title;

    my $output = `lc /nologo user32::MessageBoxA(0, '$msg', '$title', $button)`;

    my $id = $? >> 8;

    return $msgbox_ids[$id];
}


@ISA = qw(Exporter);
@EXPORT = qw(
    msgbox
	);

1;
