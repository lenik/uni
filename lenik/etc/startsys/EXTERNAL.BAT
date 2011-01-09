@Echo Off

        Rem If "%SCmdLoaded%"=="True" Log-Off !-- %0

Rem External Module Calling

        Goto Parameters
        :CParameters

        Call InlineF Echo External Module Package :- %EOP1%

        Goto %EOP1%

:StartWindows
Rem Start windows with enviroment var set
        If "%RunMode%"=="Windows" InlineF Echo This program cannot run under windows.
        If "%RunMode%"=="Windows" Goto End

        Set RunMode=Windows
        Call ExecWin %EOP2% %EOP3% %EOPx%
        Set RunMode=Dos

        Goto End

Rem Other language interfaces

:Parameters
Rem Initialize full-parameters support
        Set EOP0=%0
        If "%1"=="!--" Goto CParameters
        Set EOP1=%1
        If "%2"=="!--" Goto CParameters
        Set EOP2=%2
        If "%3"=="!--" Goto CParameters
        Set EOP3=%3
        If "%4"=="!--" Goto CParameters
        Set EOPx=%4

        :NextParameters
                Shift
                If "%4"=="" Goto CParameters
                If "%4"=="!--" Goto CParameters
                Set EOPx=%EOPx% %4
        Goto NextParameters

:End
        For %%o in (0 1 2 3 x) Do Set EOP%%o=

        Rem If "%SCmdLoaded%"=="True" Log-On !-- %0
