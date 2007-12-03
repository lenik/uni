@echo off

    rem Generated by plbat 756

:check_os
    if "%OS%"=="" goto check_cmd
    if "%OS%"=="Windows_NT" goto check_cmd
    echo The operating system isn't supported: %OS%
    exit /b 1

:check_cmd
    verify other 2>nul
    setlocal enableextensions
    if not errorlevel 1 goto boot
    echo The cmd extensions isn't supported.
    echo Maybe your windows version is too old.
    exit /b 1

:check_more

:boot
    setlocal
    set _nam=%~n0
    set _ext=
    set PERLFLAGS=-w -Mcmt::mess

:find_target
    for %%x in ("" .pl .p .pc .pld) do (
        set _ext=%%~x
        if exist "%_nam%%%~x" (
            set _dir=
            goto find_shell
        )
        if exist "../%_nam%%%~x" (
            set _dir=../
            goto find_shell
        )
        for %%i in ("%_nam%%%~x") do (
            set _dir=%%~dp$PATH:i
            if exist "!_dir!%%~i" goto find_shell
        )
    )
    echo Can't find the target program %_nam%.
    exit /b 1

:find_shell
    if "%_shell%"=="" (
        for %%a in (%CMDCMDLINE%) do (
            for %%c in (%%a) do (
                set _shell=%%~nc
                goto start_mode
            )
        )
    )

:start_mode
    set _args=%*
    set _look=3
:next_arg
    if %_look% leq 3 goto bycmd
    if "%~1"=="" goto bycmd
    if "%~1"=="-v" goto start
    set _1=%~1
    if "%_1:~0,6%"=="--verb" goto start
    shift
    set /a _look = _look - 1
    goto next_arg

:bycmd
    if "%_shell%"=="cmdw" goto startw
    if "%_shell%"=="CMDW" goto startw
    goto start

:start
    if "%PERL%"=="" set PERL=perl
    "%PERL%" %PERLFLAGS% "%_dir%%_nam%%_ext%" %_args%
    goto end

:startw
    if "%PERLW%"=="" set PERLW=perlw
    "%PERLW%" %PERLFLAGS% "%_dir%%_nam%%_ext%" %_args%
    goto end

:end
