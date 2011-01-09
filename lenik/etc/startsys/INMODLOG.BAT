@Echo Off

        If "%SCmdLoaded%"=="True" Log-Off !-- %0 %1 %2 ...

Rem InModLog -- Inline Module under logs

        Goto Parameters
        :CParameters

:Main
        Call InModule %IMOP1% %IMOP2% %IMOP3% %IMOPx%
        Goto End

:Parameters
Rem Initialize full-parameters support
        Set IMOP0=%0
        Set IMOP1=%1
        Set IMOP2=%2
        Set IMOP3=%3

        Set IMOPx=%4
        :NextParameters
                Shift
                If "%4"=="" Goto CParameters
                If "%4"=="!--" Goto CParameters
                Set IMOPx=%IMOPx% %4
        Goto NextParameters

:End
        For %%o in (0 1 2 3 x) Do Set IMOP%%o=

        If "%SCmdLoaded%"=="True" Log-On !-- %0 %1 %2 ...
