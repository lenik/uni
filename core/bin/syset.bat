@rem = '$Id: syset.bat,v 1.15 2005-07-08 02:12:26 dansei Exp $';
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
	set dir_t=%~dp0
	set dir_t=%dir_t:~,-3%

	if not exist %dir_t%\.dirt (
	    echo Cannot found dir-t, please install it first
	    goto end
	    )
	echo Found installed dir-t at %dir_t%

        if exist "%dir_t%\2\.cirkonstancoj" (
            set dir_cir=%dir_t%\2
            echo Found built-in cirkonstancoj
            goto env_perlcall
        )

        for %%i in (c d e f g h i j k l m n o p q r s t u v w x y z) do (
            if exist %%i:\.cirkonstancoj\.cirkonstancoj (
                set dir_cir=%%i:\.cirkonstancoj
                echo Found cirkonstancoj installed at %dir_cir%
                goto env_perlcall
            )
        )

        echo Cannot found cirkonstancoj, please install it first.
        goto end

        :env_perlcall
            set PATH=%dir_t%;%dir_t%\0;%dir_t%\1;%dir_t%\2;%dir_t%\3;%dir_t%\4;%dir_t%\5;%dir_t%\6;%dir_t%\7;%dir_t%\8;%dir_t%\9;%PATH%
            set PATH=%dir_cir%\perl\perl5\bin;%PATH%
            set PERLLIB=%dir_t%\0\lib;%dir_cir%\perl\perl5\lib;%dir_cir%\perl\perl5\site\lib
            call perl %~dpnx0 env %dir_t% %dir_cir%

        :env_shell
            reg add hkcr\*\shell\Binary\Command /f /ve /d      "%dir_t%\3\ue.exe ""%%1""" >nul
            reg add hkcr\*\shell\Notepad\Command /f /ve /d     "%dir_t%\3\metapad.exe ""%%1""" >nul
            reg add hkcr\*\shell\Write\Command /f /ve /d       "Write ""%%1""" >nul
            reg add hkcr\*\shell\Register\Command /f /ve /d    "regsvr32 ""%%1""" >nul
            reg add hkcr\*\shell\Unregister\Command /f /ve /d  "regsvr32 /u ""%%1""" >nul
            reg add "hkcr\*\shell\My Sign\Command" /f /ve /d   "%dir_t%\1\dsign.bat ""%%1""" >nul

            reg add hkcr\Directory\shell\Console\Command /f /ve /d              "cmd ""%%1""" >nul
            reg add hkcr\Directory\shell\Serialize\Command /f /ve /d            "%dir_t%\1\renum.exe -D -w 2 ""%%1\*""" >nul
            reg add "hkcr\Directory\shell\Gather binaries\Command" /f /ve /d    "%dir_t%\0\mvup.bat -c ""%%1\..\..\bin\"" -t ""%%1\..\..\bin\"" *.exe *.dll *.ocx" >nul
            reg add "hkcr\Directory\shell\Build TGZ archive\Command" /f /ve /d  "%dir_t%\0\tgz.bat ""%%1""" >nul
            reg add "hkcr\Directory\shell\SIMplifiers expand\Command" /f /ve /d "%dir_cir%\perl\perl5\bin\perl.exe %dir_t%\0\sim.pl" >nul
            reg add "hkcr\Directory\shell\Add nfs mapping\Command" /f /ve /d    "%dir_cir%\perl\perl5\bin\perl.exe %dir_t%\0\nfs.pl -i -m" >nul

            reg add "hklm\SOFTWARE\Microsoft\Command Processor" /f /v CompletionChar /t REG_DWORD /d 14 >nul
            reg add "hklm\SOFTWARE\Microsoft\Command Processor" /f /v PathCompletionChar /t REG_DWORD /d 14 >nul
            reg add "hklm\SOFTWARE\Microsoft\Command Processor" /f /v EnableExtensions /t REG_DWORD /d 1 >nul
            reg add "hklm\SOFTWARE\Microsoft\Command Processor" /f /v DelayedExpansion /t REG_DWORD /d 1 >nul

            reg add "hkcu\SOFTWARE\Microsoft\Command Processor" /f /v CompletionChar /t REG_DWORD /d 14 >nul
            reg add "hkcu\SOFTWARE\Microsoft\Command Processor" /f /v PathCompletionChar /t REG_DWORD /d 14 >nul
            reg add "hkcu\SOFTWARE\Microsoft\Command Processor" /f /v EnableExtensions /t REG_DWORD /d 1 >nul
            reg add "hkcu\SOFTWARE\Microsoft\Command Processor" /f /v DelayedExpansion /t REG_DWORD /d 1 >nul

            reg add "hkcu\SOFTWARE\metapad" /f /v m_ShowToolbar        /t REG_DWORD /d 0 >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v m_ShowStatus         /t REG_DWORD /d 0 >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v m_Hyperlinks         /t REG_DWORD /d 0 >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v bAutoIndent          /t REG_DWORD /d 1 >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v bInsertSpaces        /t REG_DWORD /d 1 >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v nTabStops            /t REG_DWORD /d 4 >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v bNoCaptionDir        /t REG_DWORD /d 1 >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v bSaveWindowPlacement /t REG_DWORD /d 1 >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v bSuppressUndoBufferPrompt /t REG_DWORD /d 1 >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v nSelectionMarginWidth /t REG_DWORD /d 5 >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v nTransparentPct      /t REG_DWORD /d 50 >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v szLangPlugin         /d "%dir_t%\3\metapad.dll" >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v szFavDir             /d "%userprofile%\Local Settings" >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v w_Left               /t REG_DWORD /d 200 >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v w_Top                /t REG_DWORD /d 120 >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v w_Width              /t REG_DWORD /d 320 >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v w_Height             /t REG_DWORD /d 240 >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v BackColour           /t REG_BINARY /d 2e335000 >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v BackColour2          /t REG_BINARY /d 263a4400 >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v PrimaryFont          /t REG_BINARY /d f3ffffff000000000000000000000000900100000000008603020131d0c2cbcecce500004e65770000204d5420426f6c640000000000000000000000 >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v SecondaryFont        /t REG_BINARY /d f0ffffff000000000000000000000000bc0200000000000003020131436f7572696572204e6577000000000000000000000000000000000000000000 >nul

        :env_ext
		echo Binding file types...
		assoc .stx=txtfile >nul

		assoc   .p=PerlScript >nul
		assoc  .pl=PerlScript >nul
		assoc .plc=PerlScript Compiled>nul
		ftype   .p=%dir_cir%\perl\perl5\bin\perl.exe "%%0" %%* >nul
		ftype  .pl=%dir_cir%\perl\perl5\bin\perl.exe "%%0" %%* >nul
		ftype .plc=%dir_cir%\perl\perl5\bin\perl.exe "%%0" %%* >nul

		assoc  .py=PythonScript >nul
		assoc .pyc=PythonScript Compiled>nul
		ftype  .py=%dir_cir%\python\python23\Python.exe "%%0" %%* >nul
		ftype .pyc=%dir_cir%\python\python23\Python.exe "%%0" %%* >nul

            assoc .php=PHPScript >nul
            ftype .php=%dir_cir%\php\php5\bin\php.exe "%%0" %%* >nul

		assoc .sim=SIMScript >nul
		ftype .sim=%dir_cir%\perl\perl5\bin\perl.exe %dir_t%\0\sim.pl "%%0" %%* >nul

            echo Initialize completed.
	goto end

        rem ';


# The Perl-section
# ---------------------------------------------------------------------------
use Win32::Registry;
use cmt::vcs;
use cmt::path;

our ($opt_cmd, @opt_args) = @ARGV;
$| = 1;

sub env {
    my ($dir_t, $dir_cir) = @_;
    my $regenv;
    my ($type, $value);

    my $prefix = path_normalize($dir_t)
         . "|" . path_normalize($dir_cir);
    $prefix =~ s/\\/[\/\\\\]/g;

    print "Updating environment";

        $::HKEY_LOCAL_MACHINE->Open("SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment",
            $regenv)  or die "Can't open environment key";

        $regenv->SetValueEx('DIR_T_HOME', 0, &REG_EXPAND_SZ, "$dir_t");
            print ".";

        my @path = ();
            if ($regenv->QueryValueEx('PATH', $type, $value)) {
                @path = grep { $_ !~ m/^$prefix/i } split(';', $value)
            }
            unshift @path, (
                path_normalize "$dir_t",
                path_normalize "$dir_t\\0",
                path_normalize "$dir_t\\1",
                path_normalize "$dir_t\\3"
                );
            push @path, (
                path_normalize "$dir_cir\\Perl\\Perl5\\bin",
                path_normalize "$dir_cir\\Python\\Python23",
                path_normalize "$dir_cir\\PHP\\PHP5",
                path_normalize "$dir_cir\\MinGW\\bin",
                path_normalize "$dir_cir\\Cygwin\\bin",
                path_normalize "$dir_t\\2",
                path_normalize "$dir_t\\4",
                path_normalize "$dir_t\\5",
                path_normalize "$dir_t\\6",
                path_normalize "$dir_t\\7",
                path_normalize "$dir_t\\8",
                path_normalize "$dir_t\\9"
                );
            $regenv->SetValueEx('PATH', 0, &REG_EXPAND_SZ, join(';', @path));
            print ".";

        my @pathext = qw/.exe .com .bat .cmd .vbs .js .sh/;
            if ($regenv->QueryValueEx('PATHEXT', $type, $value)) {
                @pathext = grep { $_ !~ m/^\.(p|pl|plc|py|pyc|php)$/i } split(';', $value);
            }
            push @pathext, qw(
                .p
                .pl
                .plc
                .py
                .pyc
                .php
                );
            $regenv->SetValueEx('PATHEXT', 0, &REG_EXPAND_SZ, join(';', @pathext));
            print ".";

        my @perllib = ();
            if ($regenv->QueryValueEx('PERLLIB', $type, $value)) {
                @perllib = grep { $_ !~ m/^$prefix/i } split(';', $value);
            }
            push @perllib, (
                path_normalize "$dir_t/0/lib",
                path_normalize "$dir_cir/perl/perl5/lib",
                path_normalize "$dir_cir/perl/perl5/site/lib",
                path_normalize "$dir_cir/perl/blib",
                );
            $regenv->SetValueEx('PERLLIB', 0, &REG_EXPAND_SZ, join(';', @perllib));
            print ".";

        my @pythonpath = ();
            if ($regenv->QueryValueEx('PYTHONPATH', $type, $value)) {
                @pythonpath = grep { $_ !~ m/^$prefix/i } split(';', $value);
            }
            push @pythonpath, (
                path_normalize "$dir_t/0/lib",
                path_normalize "$dir_cir/python/python23/lib",
                path_normalize "$dir_cir/python/python23/lib/site-packages",
                );
            $regenv->SetValueEx('PYTHONPATH', 0, &REG_EXPAND_SZ, join(';', @pythonpath));
            print ".";

        $regenv->Close();
        print "\n";


    my $windir = $ENV{'SystemRoot'};

        # Force PHP.ini exists
        if (! -f "$windir/php.ini") {
            if (-f "$dir_cir/php/php5/php.ini") {
                print "PHP not installed, install one\n";
                open(FH, "$dir_cir/php/php5/php.ini")
                    or die "Install failed: source";
                my @data = <FH>;
                open(FH, ">$windir/php.ini")
                    or die "Install failed: target";
                print FH join('', @data);
                close FH;
            } else {
                print "PHP not installed, and cirkonstancoj doesn't contain one. \n";
            }
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
                s/=.*$/= $dir_cir\\php\\php5\\ext/;
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
