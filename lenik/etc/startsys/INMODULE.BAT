@Echo Off

        Rem If "%SCmdLoaded%"=="True" Log-Off !-- %0

Rem InModule -- Inline Module

        Goto Parameters
        :CParameters

	If "%BD%"=="" Set BD=C:

        Call %BD%\h\l\InlineF Echo M inline :- %IMOP1%

        Goto %IMOP1%

:Set
:SetSimplePath
        Set Home=%BD%\
        Set Comspec=%BD%\h\l\Command.com
        If "%Win32%"=="True" Set ExecBin=%BD%\t\w\32\
        If "%WinNT%"=="True" Set ExecBin=%BD%\t\w\NT\
        Set LinuxBin=%BD%\t\l\bin
        Set Out_Path_A=P:\source\out
        Set Out_Path_B=T:\out
        Set Hlp=%BD%\r\r\h

        Set Xworks=%BD%\h\Xworks
        Set Temp=%Xworks%\Temp
        Set MCS=%BD%\h\l\MCS-51\Ice16

        Set Path=%BD%\h\L;%BD%\H;%BD%\

                Rem Local Developping Out-Dir
                        Set Path=%Path%;T:\;C:\h\C\TC30\Bin;%Out_Path_A%;%Out_Path_B%;%Temp%

                Rem Tools
                        If "%WinNT%"=="True" Set Path=%Path%;%BD%\t\w\NT
                        If "%Win32%"=="True" Set Path=%Path%;%BD%\t\w\32

                        If "%WinNT%"=="True" Set Path=%Path%;%windir%;%windir%\System32;%windir%\System32\DllCache;%windir%\Temp
                        If "%Win32%"=="True" Set Path=%Path%;%winbootdir%;%winbootdir%\System

                        Set Path=%Path%;%BD%\T;%BD%\t\Soft;%BD%\t\Obsolute

                        If "%WinNT%"=="True" Goto WinTools
                        If "%Win32%"=="True" Goto WinTools
                        Goto CWinTools
                        :WinTools
                                If Not "%winbootdir%"=="%BD%\t\W" Set Path=%Path%;%BD%\t\W;%BD%\t\w\Temp;%BD%\t\w\System
                                Set Path=%Path%;%LinuxBin%
                                Set RunMode=Windows
                        :CWinTools

        If "%IMOP1%"=="SetSimplePath" Goto End

Rem Initialize...

    Call %BD%\h\l\InlineF Echo M inline :- %IMOP1% [ENVIROMENT]
        Rem Date 1-1-2000

        Rem only reset enviroments (include command.com)
        If "%IMOP2%"=="r" Set Reset=True
        If "%IMOP2%"=="R" Set Reset=True

        If "%Win32%"=="True" Set LoadHigh=LH
        If "%WinNT%"=="True" Set LoadHigh=

        Rem --- Call %BD%\h\l\InModule SetSimplePath
                Rem Language
                        Set Path=%Path%;%BD%\_PF\VS98\Common\MSDev98\Bin
                        Set Path=%Path%;%BD%\_PF\mn\FrameworkSDK\Bin;%BD%\winnt\Microsoft.NET\Framework\v1.0.2204
                        Set Path=%Path%;%BD%\h\Language\java\bin
                        Set Path=%Path%;%BD%\h\l\Assemb~1\Tasm\Bin;%BD%\h\l\Assemb~1\Masm5
                        Set Path=%Path%;%BD%\h\Else\TP;%BD%\h\Else\TP\Utils
                        Set Path=%Path%;%BD%\h\Perl\Active~1\bin
                        Set Path=%Path%;%BD%\h\Perl\Perl5\bin
                        Set Path=%Path%;%Hlp%;%Temp%
                Rem Other Tools
                        Set Path=%Path%;%BD%\UCDos;%BD%\UCDict
                        Set Path=%Path%;%MCS%

        Set VCRoot=E:\Progra~1\Micros~3\VC98
        Set Include=%VCRoot%\Include;%VCRoot%\MFC\Include;c:\s\t\i
        Set Lib=%VCRoot%\Lib;%VCRoot%\MFC\Lib

	Set WNMake=%BD%\_PF\VS98\Common\MSDev98\Bin\wnmake.exe
	Set VscomnTools=%BD%\_PF\Mn\Common7\Tools
        Set MSDevDir=%BD%\_PF\VS98
Rem     Set Perl5Lib=%BD%\h\Perl\ActivePerl\lib;%BD%\h\Perl\ActivePerl\site\lib;%BD%\h\Perl\Perl5\lib;%BD%\h\Perl\Perl5\site
        Set Perl5Lib=%BD%/H/Perl/ActivePerl/lib;%BD%/H/Perl/ActivePerl/site/lib;%BD%/H/Perl/Perl5/lib;%BD%/H/Perl/Perl5/site
        Set ClassPath=%BD%\_PF\Borland\VBroken\lib\vbcpp.jar

        Set Blaster=A220 I5 D1 H1 REC3 P330 T6

        If "%RunMode%"=="" Set RunMode=Dos

        Prompt %%

    Call %BD%\h\l\InlineF Echo M inline :- %IMOP1% [ADD-INS]

        If "%Win32%"=="True" Echo loading smartdrv 16384 a+ b+ /q >nul
        If "%Win32%"=="True" Smartdrv 16384 A+ B+ >nul

        %LoadHigh% %ExecBin%DosKey /BufSize:1024 >Nul
        %ExecBin%DosKey =Go.com $*
        %ExecBin%DosKey ?=InModLog Macros $* -- show macros
        %ExecBin%DosKey Config=InModLog Config $*
        %ExecBin%DosKey ReSet=InModLog Set $*
        %ExecBin%DosKey _ReSet=%BD%\h\l\InModLog Set $*
        %ExecBin%DosKey CSet=InModLog ClearSetNormal $*
        %ExecBin%DosKey SSP=InModLog SetSimplePath $* !-- set simple path
        %ExecBin%DosKey CMap=InModLog CMirror $*
        %ExecBin%DosKey ReMap=InModLog Mirror $*
        %ExecBin%DosKey Show=InModLog Show $* !-- show disks
        %ExecBin%DosKey Hide=InModLog Hide $* !-- hide disks
        %ExecBin%DosKey Clean=InModLog CleanDiskC $* !-- clean *.??? files under disk C
        %ExecBin%DosKey InShell=InModLog InShell $*
        %ExecBin%DosKey StartWin=InModLog InShell InModule External StartWindows $*
        %ExecBin%DosKey _Log=InModLog DumpLogs $* !-- dump logs if scmd running
        %ExecBin%DosKey __Log=Type C:\SCLog $*

        %ExecBin%DosKey True=InModLog TurnTrue

    Call %BD%\h\l\InlineF Echo M inline :- %IMOP1% [FINISH]

        Goto End

:ClearSetNormal
        If Not "%BD%"=="" Call InModule SetSimplePath

        For %%i in (BD NetDisk) Do Set %%i=
        For %%i in (Reset LoadHigh) Do Set %%i=
        For %%i in (Win32 WinNT) Do Set %%i=
        For %%i in (Iam RunMode MirrorDisks Mirrored) Do Set %%i=
        For %%i in (Home LinuxBin Out_Path_A Out_Path_B Hlp Edit) Do Set %%i=
        For %%i in (Xworks Temp MCS) Do Set %%i=
        For %%i in (Include Lib WNMake ClassPath) Do Set %%i=
        For %%i in (VscomnTools MSDevDir Perl5Lib) Do Set %%i=
        For %%i in (Blaster) Do Set %%i=

        Goto End

:Macros
        %ExecBin%DosKey /MACROS
        Goto End

:Mirror
Rem Set mirror disks
    If "%Mirrored%"=="True" If Not "%IMOP2%"=="-f" Goto End
    If Exist X:\Nul If Not "%IMOP2%"=="-f" Goto End

    Set MirrorDisks=A B H J M P Q R S T U V W X
    Rem Language
        %ExecBin%Map T: %BD%\h\C\TC20
        %ExecBin%Map P: %BD%\h\C\TC30
               CD P:\Bin
        %ExecBin%Map Q: %BD%\h\Basic\Quick5
        %ExecBin%Map J: %BD%\h\Language\Java
    Rem H\L
        %ExecBin%Map M: %MCS%
        %ExecBin%Map U: %BD%\h\l\Trans
               CD U:\Source
               If "%Config%"=="SoftDos" CD U:\SoftIce\Dos
               If "%Config%"=="SoftWin" CD U:\SoftIce\Win95
    Rem Develop Enviroment
        %ExecBin%Map R: %BD%\R
               CD R:\Frags
        %ExecBin%Map H: %BD%\R\R
               CD H:\Lists
        %ExecBin%Map S: %BD%\S
        %ExecBin%Map V: %BD%\S\G
        If "%WinNT%"=="True" %ExecBin%Map W: %NetDisk%\Webs\Develop

    Rem Others
        %ExecBin%Map X: %Xworks%

        If "%SimuFloppy%"=="True" If "%Win32%"=="True" %ExecBin%Map A: %Xworks%\FloppyA
        %ExecBin%Map B: %Xworks%\FloppyB

        Set Mirrored=True
        Goto End

:CMirror
Rem Clean mirror disks
        If "%Mirrored%"=="True" 	Goto CMirror2
        If "%IMOP2%"=="-f" 		Goto CMirror2
        If "%IMOP2%"=="-a"		Goto CMirror2
        Goto End
        :CMirror2
        If "%IMOP2%"=="-f" Set IMOP2=%IMOP3%
	If "%IMOP2%"=="-a" Set MirrorDisks=A B C D E F G H I J K L M N O P Q R S T U V W X Y Z
	Echo MirrorDisks = %MirrorDisks%
        For %%i in (%MirrorDisks%) Do %ExecBin%Map %%i: /d >Nul
        Set Mirrored=
        Goto End

:Loading
Rem Loading system & TSR programs

        If "%WinNT%"=="True" Goto SkipSCmd
                %LoadHigh% %BD%\t\SCmd
                _Status ' Disable Security Command Extend

                Set SCmdLoaded=True
                Set SCmdStatus=Off
        :SkipSCmd
        Call LM

        Goto End

:Welcome
Rem Show welcome screen
        If Not Exist %BD%\h\Nul Goto End
        Call %BD%\Distrib\HChat\II\Frame\ShowWel
        Goto End

:Show
        For %%i in (WinNT Progra~1 T H R S Webs) Do %ExecBin%A -H -R %BD%\%%i
        Goto End

:Hide
        For %%i in (WinNT Progra~1 T H R S Webs) Do %ExecBin%A +H +R %BD%\%%i
        Goto End

:CleanDiskC
	Set CleanDisks=C D E F G H I J K L M N O P Q R S T U V W X Y Z
        For %%i in (%CleanDisks%) Do If Exist %%i:\'*.* Call InlineF Del %%i:\'*.*
        Set CleanDisks=

        If Not "%IMOP2%"=="" Echo Call InlineF Del %IMOP2% %IMOP3% %IMOPx% /Y
        Goto End

:Config
	Set OldEdit=%Edit%
	Set Edit=%BD%\t\w\32\Edit
	If "%WinNT%"=="True" Set Edit=%BD%\t\w\Uedit32
		Set ConfigFiles=%BD%\h\l\InModule.bat %BD%\h\l\InLineF.bat %BD%\Autoexec.bat %BD%\Config.sys %BD%\Msdos.sys
                For %%i in (%ConfigFiles%) do %ExecBin%A -H -R %%i
                        %Edit% %ConfigFiles% %IMOP2% %IMOP3% %IMOPx%
	        For %%i in (%ConfigFiles%) do %ExecBin%A +H +R %%i
	        Set ConfigFiles=
	Set Edit=%OldEdit%
	Set OldEdit=
        Call InModule Set
        Goto End

:TurnTrue
        Rem SCQuit or Disable at first
        Call InlineF Echo (Turn off scmd manually)

        Set Iam=True
        Call InModule Mirror
        Call InModule Set
        Goto End

:SetCdrom
Rem Setup cdrom
        %LoadHigh% %BD%\t\Mscdex /D:Mscd001 /M:20
        Echo.
        Echo install type:
        Echo    1. minimum cd-rom
        Echo    2. typical cd-rom
        %ExecBin%Choice /C12 /N>nul
        If ErrorLevel 2 Set Break=True
        Goto End

:InShell
        Command /K Call InModule StartShell %IMOP2% %IMOP3% %IMOPx%
        Goto End

        :StartShell
        Rem Run program in new shell
                Call %IMOP3% %IMOPx%
                Exit
        Goto End

:External
Rem Run external module -- only an interface to external module
        Call %BD%\h\l\External %IMOP2% %IMOP3% %IMOPx%
        Goto End

:ClearEnv
Rem Clear tempolary enviroment vars
        Set Break=
        Set Reset=
        Set SCmdLoaded=

        Goto End

:DumpLogs
Rem dump logs if scmd running
        If "%IMOP2%"=="-" Type C:\ScLog | More
        If Not "%IMOP2%"=="-" Type C:\ScLog
        Goto End

:Parameters
Rem Initialize full-parameters support
        Set IMOP0=%0
	For %%o in (0 1 2 3 x) Do Set IMOP%%o=
        If "%1"=="!--" Goto CParameters
        Set IMOP1=%1
        If "%2"=="!--" Goto CParameters
        Set IMOP2=%2
        If "%3"=="!--" Goto CParameters
        Set IMOP3=%3
        If "%4"=="!--" Goto CParameters
        Set IMOPx=%4

        :NextParameters
                Shift
                If "%4"=="" Goto CParameters
                If "%4"=="!--" Goto CParameters
                Set IMOPx=%IMOPx% %4
        Goto NextParameters

:End
        For %%o in (0 1 2 3 x) Do Set IMOP%%o=

        Rem If "%SCmdLoaded%"=="True" Log-On !-- %0
