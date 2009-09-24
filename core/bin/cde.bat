@echo off

    setlocal
    set _strict=1
    goto init

:start
    if "%_parents%"=="1" goto chdir_pp

    call :find "*%~1"
    if errorlevel 2 (
        echo ambiguous names:
        for /d %%d in ("*%~1") do (
            echo     %%d
        )
        exit /b 2
    )
    if errorlevel 1 (
        echo can't find %~1
        exit /b 1
    )
    goto chdir

:find
    set _dir=
    for /d %%d in ("%~1") do (
        if not "!_dir!"=="" exit /b 2
        set _dir=%%d
    )
    if "%_dir%"=="" exit /b 1
    exit /b 0

:chdir_pp
    set _chain=.
:nextp
    set _pre=%~1
    :choploop
        if "%_pre%"=="" goto chdir_ppfail
        call :find "%_chain%\*%_pre%"
        if errorlevel 1 (
            set _pre=%_pre:~0,-1%
            goto choploop
        )
    set _chain=%_dir%
    if "%_pre%"=="%~1" (
        set _dir=%_chain%
        goto chdir
    ) else (
        goto nextp
    )
:chdir_ppfail
    echo can't find %~1, stopped at %_chain%
    exit /b 0

:chdir
    if "%_dir%"=="" (
        echo can't find %1
        exit /b 1
    )
    call export - _dir
    %leave%
    cd "%_dir%"
    exit /b 0

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set   __DIR__=%~dp0
    set  __FILE__=%~dpnx0
    set  _parents=1

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
    ) else if "%~1"=="-P" (
        set _parents=
    ) else if "%~1"=="--no-postfix-parents" (
        set _parents=
    ) else if "%~1"=="-p" (
        set _parents=1
    ) else if "%~1"=="--postfix-parents" (
        set _parents=1
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
    echo [cde] "ends-with" version of chdir
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %__FILE__% [OPTION] DIRNAME
    echo.
    echo Options:
    echo    -p, --postfix-parents
    echo    -P, --no-postfix-parents
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    exit /b 0
