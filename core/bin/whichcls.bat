@echo off

    setlocal
    set _strict=1
    goto init

:start

    set CLASSPATH=%_cp%
    call WhichClass %_names%
    exit /b 0

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set _startdir=%~dp0
    set  _program=%~dpnx0
    set    _recur=
    set       _cp=.
    set    _names=

:prep1
    if "%~1"==""            goto prep2
    if "%~1"=="--version"   goto version
    if "%~1"=="-h"          goto help
    if "%~1"=="--help"      goto help
    if "%~1"=="--" (
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
    ) else if "%~1"=="-r" (
        set _recur=1
    ) else if "%~1"=="--recursive" (
        set _recur=1
    ) else if "%_arg:~0,1%"=="-" (
        if "%_strict%"=="1" (
            echo Invalid option: %1
            exit /b 1
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
    if "%~1"=="--" goto prepcp
    if "%~1"=="*" goto prepcp
    if exist "%~1\*" goto prepcp
    if "%~x1"==".jar" goto prepcp
    set _names=%_names% %~1
    shift
    goto prep2

:prepcp
    if "%~1"=="" goto prep3
    if "%_recur%"=="1" (
        for /r %%i in ("%~1") do (
            if %_verbose% geq 1 echo classpath - %%~dpnxi
            set _cp=!_cp!;%%~dpnxi
        )
    ) else (
        for %%i in ("%~1") do (
            if %_verbose% geq 1 echo classpath - %%~dpnxi
            set _cp=!_cp!;%%~dpnxi
        )
    )
    shift
    goto prepcp

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
    if %_verbose% geq 2 (set _ | tabify -b -d==)
    goto start

:version
    set _id=$Id: .bat 784 2008-01-15 10:53:24Z lenik $
    for /f "tokens=3-6" %%i in ("%_id%") do (
        set   _version=%%i
        set      _date=%%j
        set      _time=%%k
        set    _author=%%l
    )
    echo [whichclass] Find path of given class names
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [OPTION] CLASSES [--] CLASSPATHS
    echo.
    echo Options:
    echo    -r, --recursive     recursive into subdirectories when necessary
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    exit /b 0
