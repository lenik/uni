use Win32::Console;
my $console = new Win32::Console(STD_OUTPUT_HANDLE);
my ($left, $top, $right, $bottom) = $console->Window;

$| = 1;
my $rctmp = $ENV{_rc_tmp};
# print "rctmp: $rctmp\n";
open(F, ">$rctmp/envs.bat")
    or die "open $rctmp/envs.bat: $!";

print F "set COLUMNS=".($right - $left + 1)."\n";
print F "set LINES=".($bottom - $top + 1)."\n";
close F;
