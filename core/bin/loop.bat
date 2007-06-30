@echo off

    setlocal

:prep
    if "%1"=="[" goto get_delay
    if "%1"=="--" (
        shift
        goto get_cl
    ) else if "%1"=="-c" (
        set _onchange=1
    ) else if "%1"=="-d" (
        set _runcopy=1
    ) else if "%1"=="-k" (
        set _fork=1
    ) else if "%1"=="-cdk" (
        set _onchange=1
        set _runcopy=1
        set _fork=1
    ) else if "%1"=="-v" (
        set _verbose=1
    ) else if "%1"=="-h" (
        goto help
    ) else (
        goto get_cl
    )
    shift
    goto prep

:get_delay
    if "%1"=="]" (
        shift
        goto prep
    )

    set /a _num=%1
    if "%_num%"=="%1" if "%2"=="]" (
        set _delay=%_num%
    )

    rem more options...
    shift
    goto get_delay

:get_cl
    if "%~1"=="" goto help
    set _srcprog=%~1
    set _runprog=%~1
    if "%_runcopy%"=="1" set _runprog=%~dpn1.copied%~x1
    if "%_verbose%"=="1" (
        echo [loop] src-program: %_srcprog%
        echo [loop] run-program: %_runprog%
    )
    set _cl=%_runprog%
    shift

:get_cl_args
    if "%~1"=="" goto begin
    set _cl=%_cl% %1
    shift
    goto get_cl_args

:help
    echo [loop] Running command repeatly
    echo Written by Lenik,  $Revision: 1.6 $
    echo Syntax:
    echo     loop [options] command arguments...
    echo Options:
    echo     ['[' delay ']']:
    echo            interval delay ^(seconds^)
    echo     -c:    repeat on program-file changes ^(Modified-Time^)
    echo     -d:    dup program before running
    echo     -k:    fork mode
    echo     -cdk:  set -c, -d, -k
    echo     -h:    display this help page
    echo     -v:    display verbose information
    goto end

:begin
    if "%_onchange%"=="1" (
        call filetime "%_srcprog%"
        set _last=!errorlevel!
        if "%_verbose%"=="1" echo [loop] Initial version of %_srcprog%: !_last!
    )

    if "%_verbose%"=="1" title %_cl%

:loop

    if "%_verbose%"=="1" echo [loop] execute: %_cl%
    copy /y "%_srcprog%" "%_runprog%" >nul
    call %_cl%

    set _keygot=0

    if not "%_onchange%"=="1" goto skip_waitch

:waitch
    set _cur=%~t1
    REM if %_cur%
:skip_waitch

    if not "%_delay%"=="" (
        readkey -t %_delay%
        if errorlevel 1 set _keygot=1
        if "!errorlevel!"=="113" goto quit
    )

    goto loop

:quit
    echo Quitting...

:cleanup
    if "%_runcopy%"=="1" (
        if "%_verbose%"=="1" echo [loop] remove copy: %_runprog%
        del "%_runprog%" >nul
    )

:end
