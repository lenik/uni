@Echo Off

        Rem If "%SCmdLoaded%"=="True" Log-Off !-- %0

Rem InlineF -- Inline Function

        Goto Parameters
        :CParameters

        Goto %IFOP1%

:Echo
Rem Echo if not var %Silence% Set
        If Not "%Silence%"=="True" Echo %IFOP2% %IFOP3% %IFOPx%
        Goto End

:Copy
rem - By Windows Setup -         Copy %IFOP2% %IFOP3% %IFOPx% >Nul
        Goto End

:Del
	If "%Silence%"=="True" Goto __SkipDelInfo
        If "%Win32%"=="True" Echo For %%i in (%IFOP2% %IFOP3% %IFOPx%) Do Del %%i
        If "%WinNT%"=="True" Echo Del /q %IFOP2% %IFOP3% %IFOPx%
	:__SkipDelInfo
        For %%i in (%IFOP2% %IFOP3% %IFOPx%) Do If Exist %%i Del %%i >Nul
        Goto End

:Parameters
Rem Initialize full-parameters support
        Set IFOP0=%0
        For %%o in (0 1 2 3 x) Do Set IFOP%%o=
        If "%1"=="!--" Goto CParameters
        Set IFOP1=%1
        If "%2"=="!--" Goto CParameters
        Set IFOP2=%2
        If "%3"=="!--" Goto CParameters
        Set IFOP3=%3
        If "%4"=="!--" Goto CParameters
        Set IFOPx=%4

        :NextParameters
                Shift
                If "%4"=="" Goto CParameters
                If "%4"=="!--" Goto CParameters
                Set IFOPx=%IFOPx% %4
        Goto NextParameters

:End
        For %%o in (0 1 2 3 x) Do Set IFOP%%o=

        Rem If "%SCmdLoaded%"=="True" Log-On !-- %0
