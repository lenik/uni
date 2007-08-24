@echo off

    setlocal
    set _lib=libunknown

    if "%~1"=="" goto help
    set _cmd=%~1
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
    echo [%_lib%] bat_library_based_on_libbat_framework
    echo author: lenik,  version: 0.1,  last updated: ?
    echo.
    echo THE LIBRARY IS DISTRIBUTED UNDER LGPL LICENSE.
    if "%_cmd%"=="v" goto end
    if "%_cmd%"=="version" goto end

    echo.
    echo Syntax: call %~n0 -command (or /command) arguments...
    echo Command List:
    echo        --hello         hello_world_test
    echo    -v, --version       show version information
    echo    -h, --help          show this help page
    goto end

:hello
    echo Hello, World^!
    set _ret=hello from %_lib%
    goto leave
