@echo off

    setlocal
    goto init

:start

    set _=dd if=/dev/zero "of=%_file%" bs=%_blen% count=%_bnum%
    if %_verbose% geq 1 echo %_%
    if %_verbose% lss 0 ( %_% >nul 2>nul ) else ( %_% )

    goto cleanup

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set _startdir=%~dp0
    set  _program=%~dpnx0

:prep1
    if "%~1"==""            goto prep2
    if "%~1"=="--version"   goto version
    if "%~1"=="-h"          goto help
    if "%~1"=="--help"      goto help
    if "%~1"=="-" (
        shift
        goto prep2
    )
    set _arg=%~1

    if "%~1"=="-q" (
        set /a _verbose = _verbose - 1
    ) else if "%~1"=="--quiet" (
        set /a _verbose = _verbose - 1
    ) else if "%~1"=="-v" (
        set /a _verbose = _verbose + 1
    ) else if "%~1"=="--verbose" (
        set /a _verbose = _verbose + 1
    ) else if "%_arg:~0,1%"=="-" (
        if "%_strict%"=="1" (
            echo Invalid option: %1
            goto end
        ) else (
            set _%_arg:~1%=%~2
            if %_verbose% geq 1 echo _%_arg:~1%=%~2
            shift
        )
    ) else (
        goto prep2
    )
    shift
    goto prep1

:prep2
    if "%~1"=="" goto help
    set _file=%~1
    set _bnum=0
    set _blen=1024
    set _char=0
    if not "%~2"=="" set _bnum=%~2
    if not "%~3"=="" set _blen=%~3
    if not "%~4"=="" set _char=%~4

:init_ok
    if %_verbose% geq 1 (
        echo _startdir=%_startdir%
        echo  _program=%_program%
        echo     _rest=%_rest%
        echo     _file=%_file%
        echo     _bnum=%_bnum%
        echo     _blen=%_blen%
        echo     _char=%_char%
    )
    goto start

:version
    set _id=$Id: zero.bat,v 1.3 2007-08-25 08:50:12 lenik Exp $
    for /f "tokens=3-6" %%i in ("%_id%") do (
        set   _version=%%i
        set      _date=%%j
        set      _time=%%k
        set    _author=%%l
    )
    echo [zero] create a file contains only \x00-bytes
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    goto end

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [OPTION] filename [bnum=0 [blen=1024 [fillchar=0]]]
    echo.
    echo Options:
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    goto end

:cleanup

:end
