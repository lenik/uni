@echo off

    rem Generated by batInvoker
    rem Template: .batInvoker.gsp

:check_os
    if "%OS%"=="" goto check_cmd
    if "%OS%"=="Windows_NT" goto check_cmd
    echo The operating system isn't supported: %OS%
    exit /b 1

:check_cmd
    verify other 2>nul
    setlocal enableextensions
    if not errorlevel 1 goto check_more
    echo The cmd extensions isn't supported.
    echo Maybe your windows version is too old.
    exit /b 1

:check_more
    set _fullParse=0
    goto init

:start
    if %_verbose% geq 1 set CLASSPATH

    if "%_start%"=="" (
        if %_verbose% geq 1 set _start=startc
    )

    if "%_start%"=="" (
        set _start=startc
        if "%_shell%"=="cmdw" set _start=startw
        if "%_shell%"=="CMDW" set _start=startw
    )
    goto %_start%

:startc
    if "%PERL%"=="" set PERL=perl
    if %_verbose% geq 1 (
        echo "%PERL%" %_perlopts% "%_nam%" %_rest%
    )
    "%PERL%" %_perlopts% "%_nam%" %_rest%
    exit /b

:startw
    if "%PERLW%"=="" set PERLW=perlw
    if %_verbose% geq 1 (
        "%PERLW%" %_perlopts% "%_nam%" %_rest%
    )
    "%PERLW%" %_perlopts% "%_nam%" %_rest%
    exit /b

:init
    set   __DIR__=%~dp0
    set  __FILE__=%~dpnx0
    set  _verbose=0
    set      _ret=
    set     _rest=

    set      _nam=%~n0
    set      _ext=
    set _perlopts=-w -Mcmt::mess

:find_target
    for %%x in ("" .pl .p .pc .pld) do (
        set _ext=%%~x
        if exist "%_nam%%%~x" (
            if not exist "%_nam%%%~x\*" (
                set _dir=
                goto find_shell
            )
        )
        if exist "..\%_nam%%%~x" (
            if not exist "..\%_nam%%%~x\*" (
                set _dir=../
                goto find_shell
            )
        )
        for %%i in ("%_nam%%%~x") do (
            set _dir=%%~dp$PATH:i
            if exist "!_dir!%%~i" (
                if not exist "!_dir!%%~i\*" (
                    goto find_shell
                )
            )
        )
    )
    echo Can't find the target program %_nam%.
    exit /b 1

:find_shell
    if "%_shell%"=="" (
        rem CMDCMDLINE doesn't work, this is a Windows' BUG.
        for %%a in (%COMSPEC%) do (
            for %%c in (%%a) do (
                set _shell=%%~nc
                goto init2
            )
        )
    )
    set _shell=cmd

:init2
    set _nam=%_dir%%_nam%%_ext%

    if "%_fullParse%"=="0" (
        set _rest=%*
        goto start
    )

:prep1
    if "%~1"==""            goto prep2
    if "%~1"=="--pb-ver"    goto version
    if "%~1"=="--pb-help"   goto help
    if "%~1"=="-Pq" (
        set /a _verbose = _verbose - 1
    ) else if "%~1"=="--pb-quiet" (
        set /a _verbose = _verbose - 1
    ) else if "%~1"=="-Pv" (
        set /a _verbose = _verbose + 1
    ) else if "%~1"=="--pb-verbose" (
        set /a _verbose = _verbose + 1
    ) else if "%~1"=="-Pw" (
        set _start=startw
    ) else if "%~1"=="--pb-win" (
        set _start=startw
    ) else (
        goto prep2
    )
    shift
    goto prep1

:prep2

:prep3
    if "%~1"=="" (
        set _=%1.
        set _=!_:"=?!
        if !_!==. goto init_ok
    )
    set _rest=%_rest%%1
    shift
    goto prep3

:init_ok
    if %_verbose% geq 1 (set _ | tabify -b -d==)
    goto start

:version
    set _id=$Id$
    for /f "tokens=3-6" %%i in ("%_id%") do (
        set   _version=%%i
        set      _date=%%j
        set      _time=%%k
        set    _author=%%l
    )
    echo [merge2] batInvoker for perl
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [PB-OPTION] ARGUMENTS...
    echo.
    echo Options:
    echo    -Pw,--pb-win        start with perlw.exe
    echo    -Pq,--pb-quiet      repeat to get less info
    echo    -Pv,--pb-verbose    repeat to get more info
    echo        --pb-ver        show version info
    echo    -Ph,--pb-help       show this help page
    exit /b 0
