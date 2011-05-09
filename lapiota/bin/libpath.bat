@echo off

    setlocal
    set _lib=libpath

    if "%~1"=="" goto help
    set _cmd=%~1
    shift
:_prep
    if "%_cmd:~0,1%"=="-" goto _del
    if "%_cmd:~0,1%"=="~" goto _del
    if "%_cmd:~0,1%"=="/" goto _del
    goto _begin
:_del
    set _cmd=%_cmd:~1%
    goto _prep

:_begin
    goto %_cmd% 2>nul
    echo don't know how to %_cmd%.
    goto end

:leave
    call export _ret
    %leave%

:end
    exit /b

:v
:version
:h
:help
    echo [%_lib%] BAT Library Of Path Functions
    echo author: lenik,  version: 0.1,  last updated: ?
    echo.
    echo THE LIBRARY IS DISTRIBUTED UNDER LGPL LICENSE.
    if "%_cmd%"=="v" goto end
    if "%_cmd%"=="version" goto end

    echo.
    echo Syntax: call %~n0 -command (or /command) arguments...
    echo.
    echo Command List:
    echo    -p, --prefix PATH N get the dirname N'times of PATH, def N=1
    echo    -v, --version       show version information
    echo    -h, --help          show this help page
    goto end

:p
:prefix
    set _ret=%~1
    if "%~2"=="" (
        set _num=1
    ) else (
        set _num=%~2
    )

:_chopsl
    if "%_ret%"=="" goto leave
    if "%_ret:~-1%"=="/" goto _chopsl_1
    if "%_ret:~-1%"=="\" goto _chopsl_1
    goto _chopsl_x
:_chopsl_1
    set _ret=%_ret:~0,-1%
    goto _chopsl
:_chopsl_x

    if %_num% leq 0 goto leave
    set /a _num = _num - 1

:_chopns
    if "%_ret%"=="" goto leave
    if "%_ret:~-1%"=="/" goto _chopns_x
    if "%_ret:~-1%"=="\" goto _chopns_x
    set _ret=%_ret:~0,-1%
    goto _chopns
:_chopns_x
    goto _chopsl
