@echo off

    setlocal
    set _strict=1
    goto init

:start

    set _dname=
    set _dname=%_dname%, cn=Lapiota General Purpose Unsafe Key
    set _dname=%_dname%, ou=Lapiota
    set _dname=%_dname%, o=boDz
    set _dname=%_dname:~2%

    set _opts=-genkey
    set _opts=%_opts% -dname "%_dname%"
    set _opts=%_opts% -alias LGPUK
    set _opts=%_opts% -keypass Lapiota
    set _opts=%_opts% -validity 365

    call keytool2 %_opts%

:file
    echo [resign] %~1
    call jarsigner2 -keypass Lapiota "%~1" LGPUK
    shift
    if not "%~1"=="" goto file

    exit /b 0

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

:prep3

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
    echo [resign] jar re-sign with lapiota default key
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [OPTION] JARS
    echo.
    echo Options:
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    exit /b 0
