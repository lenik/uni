@rem = '$Id: syset.bat,v 1.22 2006-09-15 10:45:41 dansei Exp $';
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

    :env
        call .dirt.0
        if not "%errorlevel%"=="70" goto init
        call .dirt.2
        if not "%errorlevel%"=="72" goto init
        goto end

    :init
	set dirt_home=%~dp0
	set dirt_home=%dirt_home:~,-3%

	if not exist %dirt_home%\.dirt (
	    echo Cannot found dir-t, please install it first
	    goto end
	    )
	echo Found installed dir-t at %dirt_home%

        if exist "%dirt_home%\2\.cirkonstancoj" (
            set cirk_home=%dirt_home%\2
            echo Found cirkonstancoj installed at t:2
            goto env_end
        )

        for %%i in (c d e f g h i j k l m n o p q r s t u v w x y z) do (
            if exist %%i:\.cirkonstancoj\.cirkonstancoj (
                set cirk_home=%%i:\.cirkonstancoj
                echo Found cirkonstancoj installed at !cirk_home!
                goto env_end
            )
        )

        echo Cannot found cirkonstancoj, please install it first.
        goto end

        :env_end
            set PATH=%dirt_home%;%dirt_home%\0;%dirt_home%\1;%cirk_home%;%dirt_home%\3;%dirt_home%\4;%dirt_home%\5;%dirt_home%\6;%PATH%;%dirt_home%\7;%dirt_home%\8;%dirt_home%\9
            set PATH=%cirk_home%\perl\perl5\bin;%PATH%
            set PATH=%PATH%;%cirk_home%\python\python24
            set PATH=%PATH%;%cirk_home%\cygwin\bin
            if "%1"=="env" goto end

        :init_perlcall
            set PERLLIB=%dirt_home%\0\lib;%cirk_home%\perl\perl5\lib;%cirk_home%\perl\perl5\site\lib
            call perl %~dpnx0 init %dirt_home% %cirk_home%

        :init_shell
            reg add hkcr\*\shell\Binary\Command /f /ve /d      "%dirt_home%\3\ue.exe ""%%1""" >nul
            reg add hkcr\*\shell\Notepad\Command /f /ve /d     "%windir%\system32\Notepad.exe ""%%1""" >nul
            reg add hkcr\*\shell\Metapad\Command /f /ve /d     "%dirt_home%\3\metapad.exe ""%%1""" >nul
            reg add hkcr\*\shell\Write\Command /f /ve /d       "Write ""%%1""" >nul
            reg add hkcr\*\shell\Register\Command /f /ve /d    "regsvr32 ""%%1""" >nul
            reg add hkcr\*\shell\Unregister\Command /f /ve /d  "regsvr32 /u ""%%1""" >nul
            reg add "hkcr\*\shell\My Sign\Command" /f /ve /d   "%dirt_home%\1\dsign.bat ""%%1""" >nul
            reg add hkcr\.patch\shell\Merge\Command /f /ve /d   "%cirk_home%\cygwin\bin\patch.exe ""%%1""" >nul

            reg add hkcr\Directory\shell\Console\Command /f /ve /d              "cmd ""%%1""" >nul
            reg add hkcr\Directory\shell\Serialize\Command /f /ve /d            "%dirt_home%\1\renum.exe -D -w 2 ""%%1\*""" >nul
            reg add "hkcr\Directory\shell\Gather binaries\Command" /f /ve /d    "%dirt_home%\0\mvup.bat -c ""%%1\..\..\bin\"" -t ""%%1\..\..\bin\"" *.exe *.dll *.ocx" >nul
            reg add "hkcr\Directory\shell\Build TGZ archive\Command" /f /ve /d  "%dirt_home%\0\tgz.bat ""%%1""" >nul
            reg add "hkcr\Directory\shell\SIMplifiers expand\Command" /f /ve /d "%cirk_home%\perl\perl5\bin\perl.exe %dirt_home%\0\sim.pl" >nul
            reg add "hkcr\Directory\shell\Add nfs mapping\Command" /f /ve /d    "%cirk_home%\perl\perl5\bin\perl.exe %dirt_home%\0\nfs.pl -i -m" >nul

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
            reg add "hkcu\SOFTWARE\metapad" /f /v szLangPlugin         /d "%dirt_home%\3\metapad.dll" >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v szFavDir             /d "%userprofile%\Local Settings" >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v w_Left               /t REG_DWORD /d 200 >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v w_Top                /t REG_DWORD /d 120 >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v w_Width              /t REG_DWORD /d 320 >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v w_Height             /t REG_DWORD /d 240 >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v BackColour           /t REG_BINARY /d 2e335000 >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v BackColour2          /t REG_BINARY /d 263a4400 >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v PrimaryFont          /t REG_BINARY /d f3ffffff000000000000000000000000900100000000008603020131d0c2cbcecce500004e65770000204d5420426f6c640000000000000000000000 >nul
            reg add "hkcu\SOFTWARE\metapad" /f /v SecondaryFont        /t REG_BINARY /d f0ffffff000000000000000000000000bc0200000000000003020131436f7572696572204e6577000000000000000000000000000000000000000000 >nul

        :init_ext
            echo Binding file types...
            assoc .stx=txtfile >nul

            assoc   .p=xPLC >nul
            assoc  .pl=xPLC >nul
            assoc .plc=xPLC Compiled>nul
            ftype   .p=%cirk_home%\perl\perl5\bin\perl.exe "%%0" %%* >nul
            ftype  .pl=%cirk_home%\perl\perl5\bin\perl.exe "%%0" %%* >nul
            ftype .plc=%cirk_home%\perl\perl5\bin\perl.exe "%%0" %%* >nul
            ftype xPLC=%cirk_home%\perl\perl5\bin\perl.exe "%%0" %%* >nul

            assoc  .py=xPYC >nul
            assoc .pyc=xPYC Compiled>nul
            ftype  .py=%cirk_home%\python\python24\Python.exe "%%0" %%* >nul
            ftype .pyc=%cirk_home%\python\python24\Python.exe "%%0" %%* >nul
            ftype xPYC=%cirk_home%\python\python24\Python.exe "%%0" %%* >nul

            assoc .php=xPHP >nul
            ftype .php=%cirk_home%\php\php5\bin\php.exe "%%0" %%* >nul
            ftype xPHP=%cirk_home%\php\php5\bin\php.exe "%%0" %%* >nul

            assoc  .rb=xRB >nul
            ftype  .rb=%cirk_home%\ruby\ruby\bin\ruby.exe "%%1" %%* >nul
            ftype  xRB=%cirk_home%\ruby\ruby\bin\ruby.exe "%%1" %%* >nul

            assoc  .ss=xSS >nul
            ftype  .ss=%cirk_home%\lisp\plt\mzscheme.exe "%%1" %%* >nul
            ftype  xSS=%cirk_home%\lisp\plt\mzscheme.exe "%%1" %%* >nul

            assoc .sim=xSIM >nul
            ftype .sim=%cirk_home%\perl\perl5\bin\perl.exe %dirt_home%\0\sim.pl "%%0" %%* >nul
            ftype xSIM=%cirk_home%\perl\perl5\bin\perl.exe %dirt_home%\0\sim.pl "%%0" %%* >nul

            assoc   .6=x6 >nul
            ftype   .6=%cirk_home%\1\simxml.exe "%%1" %%* >nul
            ftype   x6=%cirk_home%\1\simxml.exe "%%1" %%* >nul

            assoc  .m4=xM4 >nul
            ftype  .m4=%cirk_home%\cygwin\bin\m4.exe "%%1" %%* >nul
            ftype  xM4=%cirk_home%\cygwin\bin\m4.exe "%%1" %%* >nul

            echo Initialize completed.
	goto end

    :help
	echo [SYSET] Config system settings   * dir-t public *
	echo Written by Snima Denik               Version 1
	echo.
	echo Syntax:  syset sub-function [arguments]
	echo sub-functions:
	echo     help    show this help page
	echo     init    initialize environment variables for dir-t
	echo.
	echo This program is distributed under GPL license.
	echo If you have problems with this program, you can
	echo   mail to: dansei@163.com
	goto end

        rem ';


# The Perl-section
# ---------------------------------------------------------------------------
use Win32::Registry;
use cmt::vcs;
use cmt::path;

our ($opt_cmd, @opt_args) = @ARGV;
$| = 1;

sub env_open {
    my $reg;
    $::HKEY_LOCAL_MACHINE->Open(
        "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment",
        $reg) or die "Can't open environment key";
    return $reg;
}

sub env_set {
    my ($name, $value) = @_;
    my $reg;
    $reg = env_open();
    $reg->SetValueEx($name, 0, &REG_EXPAND_SZ, $value);
    $reg->Close();
}

sub env_add {
    my $reg;
    my ($type, $value);
    my ($name, $removes, $append, $insert) = @_;
    my @list = ();
    $reg = env_open();
    if ($reg->QueryValueEx($name, $type, $value)) {
        @list = split(';', $value);
        if ($removes) {
            @list = grep { $_ !~ m/$removes/ } @list;
        }
    }
    push @list, @$append if $append;
    unshift @list, @$insert if $insert;
    $reg->SetValueEx($name, 0, &REG_EXPAND_SZ, join(';', @list));
    $reg->Close();
}

sub init {
    my ($dirt_home, $cirk_home) = @_;
    my $disk_cygwin = 'B:';
    my $regenv;
    my ($type, $value);

    # prefix == x:/t | y:/.cir
    my $prefix = path_normalize($dirt_home)
         . "|" . path_normalize($cirk_home);
    $prefix =~ s/\\/[\/\\\\]/g;         # \ -> [/\], because prefix is regexp.

    print "Updating environment";
        env_set('DIRT_HOME', "$dirt_home");
        env_set('CIRK_HOME', "$cirk_home");
        print ".";

        env_add('PATH', qr/^$prefix/i, [
                path_normalize "$cirk_home",
                path_normalize "$cirk_home\\Perl\\Perl5\\bin",
                path_normalize "$cirk_home\\PHP\\PHP5",
                path_normalize "$cirk_home\\Python\\Python24",
                path_normalize "$cirk_home\\Ruby\\ruby\\bin",
                path_normalize "$cirk_home\\Java\\bin",
                path_normalize "$cirk_home\\MinGW\\bin",
                path_normalize "$disk_cygwin\\bin",
                path_normalize "$cirk_home\\Cygwin\\bin",
                path_normalize "$dirt_home\\4",
                path_normalize "$dirt_home\\5",
                path_normalize "$dirt_home\\6",
                path_normalize "$dirt_home\\7",
                path_normalize "$dirt_home\\8",
                path_normalize "$dirt_home\\9",
                ], [
                path_normalize "$dirt_home",
                path_normalize "$dirt_home\\0",
                path_normalize "$dirt_home\\1",
                path_normalize "$dirt_home\\3",
                ]);
        print ".";

        env_add('PATHEXT', qr/^\.(6|m4|p|pl|plc|py|pyc|php|rb|ss)$/i,
                [qw/.6 .p .pl .plc .py .pyc .php .rb .ss/]);
        print ".";

        env_add('INCLUDE', qr/^$prefix/i, [
                path_normalize "$cirk_home/sdk/include",
                ]);
        print ".";

        env_add('LIB', qr/^$prefix/i, [
                path_normalize "$cirk_home/sdk/lib",
                ]);
        print ".";

        env_add('JAVA_LIB', qr/^$prefix/i, [
                path_normalize "$cirk_home/Java/lib",
                ]);
        print ".";

        env_add('PERLLIB', qr/^$prefix/i, [
                path_normalize "$dirt_home/0/lib",
                path_normalize "$cirk_home/perl/perl5/lib",
                path_normalize "$cirk_home/perl/perl5/site/lib",
                path_normalize "$cirk_home/perl/blib",
                ]);
        print ".";

        env_add('PYTHONPATH', qr/^$prefix/i, [
                path_normalize "$dirt_home/0/lib",
                path_normalize "$cirk_home/python/python24/lib",
                path_normalize "$cirk_home/python/python24/lib/site-packages",
                path_normalize "$cirk_home/python/blib",
                ]);
        print ".";

        env_add('SIMXMLPATH', qr/^$prefix/i, [
                path_normalize "$dirt_home/0/lib/6",
                ]);
        print ".";

        print "\n";


    my $windir = $ENV{'SystemRoot'};

        # Force PHP.ini exists
        if (! -f "$windir/php.ini") {
            if (-f "$cirk_home/php/php5/php.ini-dist") {
                print "PHP not installed, install one\n";
                open(FH, "$cirk_home/php/php5/php.ini-dist")
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
                s/=.*$/= $cirk_home\\php\\php5\\ext/;
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

if ($opt_cmd eq 'init') {
    init(@opt_args);
}


__END__
:end
