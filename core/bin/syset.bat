@rem = '$Id: syset.bat,v 1.8 2004-12-03 04:27:29 dansei Exp $';
@rem = ' (Not strict mode)

        @echo off

        if not "%OS%"=="Windows_NT" goto err_notsupp

        rem The BAT-section
        rem ------------------------------------------------------------------

	if "%1"=="" goto help
	goto %1

    :err_label
	echo Syntax error: %1
	echo.
	goto help

    :err_not_supp
	echo You must run this program under Windows NT/2000 or above.
	goto end

    :help
	echo [SYSET] Config system settings   * dir-t public *
	echo Written by Snima Denik               Version 1
	echo.
	echo Syntax:  syset sub-function [arguments]
	echo sub-functions:
	echo     help    show this help page
	echo     env     initialize environment variables for dir-t
	echo.
	echo This program is distributed under GPL license.
	echo If you have problems with this program, you can
	echo   mail to: dansei@163.com
	goto end

    :env
	set t_dir=%~dp0
	set t_dir=%t_dir:~,-3%
	echo Found installed dir-t at %t_dir%

        :env_perlcall
            set PATH=%t_dir%;%t_dir%\0;%t_dir%\1;%t_dir%\2\bin;%t_dir%\3;%t_dir%\4;%t_dir%\5;%t_dir%\6;%t_dir%\7;%t_dir%\8;%t_dir%\9;%PATH%
            set PERLLIB=%t_dir%\0;%t_dir%\2\lib

            call perl %~dpnx0 env %t_dir%

        :env_shell
            reg add hkcr\*\shell\Binary\Command /f /ve /d      "%t_dir%\3\ue.exe ""%%1""" >nul
            reg add hkcr\*\shell\Notepad\Command /f /ve /d     "Notepad ""%%1""" >nul
            reg add hkcr\*\shell\Write\Command /f /ve /d       "Write ""%%1""" >nul
            reg add hkcr\*\shell\Register\Command /f /ve /d    "regsvr32 ""%%1""" >nul
            reg add hkcr\*\shell\Unregister\Command /f /ve /d  "regsvr32 /u ""%%1""" >nul
            reg add "hkcr\*\shell\My Sign\Command" /f /ve /d   "%t_dir%\1\dsign.bat ""%%1""" >nul

            reg add hkcr\Directory\shell\Console\Command /f /ve /d                      "cmd ""%%1""" >nul
            reg add hkcr\Directory\shell\Serialize\Command /f /ve /d                    "%t_dir%\1\renum.exe -D -w 2 ""%%1\*""" >nul
            reg add "hkcr\Directory\shell\Gather binaries\Command" /f /ve /d            "%t_dir%\0\mvup.bat -c ""%%1\..\..\bin\"" -t ""%%1\..\..\bin\"" *.exe *.dll *.ocx" >nul
            reg add "hkcr\Directory\shell\Synchronize IMmediately\Command" /f /ve /d    "%t_dir%\2\bin\perl.exe %t_dir%\0\sim.pl" >nul

            reg add "hklm\SOFTWARE\Microsoft\Command Processor" /f /v CompletionChar /t REG_DWORD /d 14 >nul
            reg add "hklm\SOFTWARE\Microsoft\Command Processor" /f /v PathCompletionChar /t REG_DWORD /d 14 >nul
            reg add "hklm\SOFTWARE\Microsoft\Command Processor" /f /v EnableExtensions /t REG_DWORD /d 1 >nul
            reg add "hklm\SOFTWARE\Microsoft\Command Processor" /f /v DelayedExpansion /t REG_DWORD /d 1 >nul

            reg add "hkcu\SOFTWARE\Microsoft\Command Processor" /f /v CompletionChar /t REG_DWORD /d 14 >nul
            reg add "hkcu\SOFTWARE\Microsoft\Command Processor" /f /v PathCompletionChar /t REG_DWORD /d 14 >nul
            reg add "hkcu\SOFTWARE\Microsoft\Command Processor" /f /v EnableExtensions /t REG_DWORD /d 1 >nul
            reg add "hkcu\SOFTWARE\Microsoft\Command Processor" /f /v DelayedExpansion /t REG_DWORD /d 1 >nul


        :env_ext
		echo Binding file types...
		assoc .stx=txtfile >nul

		assoc .pl=PerlScript >nul
		assoc .p=PerlScript >nul
		ftype .pl=%t_dir%\2\bin\perl.exe "%%0" %%* >nul
		ftype .p=%t_dir%\2\bin\perl.exe "%%0" %%* >nul

		assoc .py=PythonScript >nul
		ftype .py=%t_dir%\2\bin\Python.exe "%%0" %%* >nul

            assoc .php=PHPScript >nul
            ftype .php=%t_dir%\2\bin\php.exe "%%0" %%* >nul

		assoc .sim=SIMScript >nul
		ftype .sim=z:\t\2\bin\perl.exe z:\t\0\sim.pl "%%0" %%* >nul

            echo Initialize completed.
	goto end

        rem ';


# The Perl-section
# ---------------------------------------------------------------------------
use Win32::Registry;
use cmt;

our ($opt_cmd, @opt_args) = @ARGV;
$| = 1;

sub env {
    my ($t_dir) = @_;
    my $regenv;
    my ($type, $value);

    my $prefix = $t_dir;
    $prefix =~ s/\\/\\\\/g;

    print "Updating environment";

        $::HKEY_LOCAL_MACHINE->Open("SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment",
            $regenv)  or die "Can't open environment key";

        $regenv->SetValueEx('DIR_T_HOME', 0, &REG_EXPAND_SZ, "$t_dir");
            print ".";

        $regenv->QueryValueEx('PATH', $type, $value);
            my @path = grep { $_ !~ m/^$prefix/i } split(';', $value);
            unshift @path, (
                "$t_dir", "$t_dir\\0", "$t_dir\\1", "$t_dir\\3"
                );
            push @path, (
                "$t_dir\\2\\bin", "$t_dir\\4", "$t_dir\\5", "$t_dir\\6",
                "$t_dir\\7", "$t_dir\\8", "$t_dir\\9"
                );
            $regenv->SetValueEx('PATH', 0, &REG_EXPAND_SZ, join(';', @path));
            print ".";

        $regenv->QueryValueEx('PATHEXT', $type, $value);
            my @pathext = grep { $_ !~ m/^\.(p|pl|py|php)$/i } split(';', $value);
            push @pathext, qw/ .p .pl .py .php /;
            $regenv->SetValueEx('PATHEXT', 0, &REG_EXPAND_SZ, join(';', @pathext));
            print ".";

        $regenv->QueryValueEx('PERLLIB', $type, $value);
            my @perllib = grep { $_ !~ m/^$prefix/i } split(';', $value);
            push @perllib, (
                "$t_dir/0", "$t_dir/2/lib"
                );
            $regenv->SetValueEx('PERLLIB', 0, &REG_EXPAND_SZ, join(';', @perllib));
            print ".";

        $regenv->QueryValueEx('PYTHONPATH', $type, $value);
            my @pythonpath = grep { $_ !~ m/^$prefix/i } split(';', $value);
            push @pythonpath, (
                "$t_dir/0", "$t_dir/2/lib"
                );
            $regenv->SetValueEx('PYTHONPATH', 0, &REG_EXPAND_SZ, join(';', @pythonpath));
            print ".";

        $regenv->Close();
        print "\n";


    my $windir = $ENV{'SystemRoot'};

        # Force PHP.ini exists
        if (! -f "$windir/php.ini") {
            print "PHP not installed, install one\n";
            open(FH, "$t_dir/2/etc/php.ini")
                or die "Install failed: source";
            my @data = <FH>;
            open(FH, ">$windir/php.ini")
                or die "Install failed: target";
            print FH join('', @data);
            close FH;
        } else {
            print "Found PHP installed\n";
        }

    if (-f "$windir/php.ini") {
        my $usrdir = $ENV{'USERPROFILE'};
        my $updir = "$usrdir\\Local Settings\\php_upload";
        my $sessdir = "$usrdir\\Local Settings\\php_session";

        if (! -d $updir) {
            mkdir($updir)
                && print "Created the php upload directory\n";
            print ">>> The php upload directory ($updir) should be accessable by Internet Users\n";
        }
        if (! -d $sessdir) {
            mkdir($sessdir)
                && print "Created the php session directory\n";
            print ">>> The php session directory ($sessdir) should be accessable by Internet Users\n";
        }

        print "Configure PHP settings";

        my @lines;
        open(FH, "<$windir/php.ini")
            or die "Can't open php.ini";
        while (<FH>) {
            chop;

            if (m/^extension_dir\b/) {
                s/=.*$/= $t_dir\\2\\bin\\ext/;
                print ".";
            } elsif (m/^upload_tmp_dir\b/) {
                s/=.*$/= $updir/;
                print ".";
            } elsif (m/^session\.save_path\b/) {
                s/=.*$/= $sessdir/;
                print ".";
            }
            push @lines, $_;
        }
        close FH;

        open(FH, ">$windir/php.ini")
            or die "Can't save php.ini, maybe you haven't write permission";
        print FH join("\n", @lines);
        close FH;
        print "\n";
    }

}

&env(@opt_args) if ($opt_cmd eq 'env');


__END__
:end
