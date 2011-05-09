@echo off

    rem dcall   verbose=
    rem dCall   verbose=0
    rem DCALL   verbose=1

    setlocal
    set prog=%~0
    set prog=%prog:~0,5%
    if "%prog%"=="dCall" set DCALL_VERBOSE=0
    if "%prog%"=="DCALL" set DCALL_VERBOSE=1

    if not "%DCALL_VERBOSE%"=="0"       echo [%TIME%] %1 begin 1>&2
    if "%DCALL_VERBOSE%"=="1"           echo [%TIME%] cmdline: %* 1>&2

    set /a _t=360000*(1%time:~0,2%-100) + 6000*(1%time:~3,2%-100) + 100*(1%time:~6,2%-100) + (1%time:~9,2%-100)
                                        rem printf '[%TIME%] ' 1>&2
                                        call %*
    set /a _t=360000*(1%time:~0,2%-100) + 6000*(1%time:~3,2%-100) + 100*(1%time:~6,2%-100) + (1%time:~9,2%-100) - _t

    if "%DCALL_VERBOSE%"=="1"           echo [%TIME%] %1 end: %_t% ms 1>&2

    err %_t%
    endlocal
