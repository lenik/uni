@echo off

    setlocal
    set _strict=1
    goto init

:start

    set _testdir=_rentest

    if not exist "%_testdir%\." mkdir "%_testdir%"
    for %%i in (%_oldp%) do touch "%_testdir%\%%i"

    pushd "%_testdir%" 2>nul
        ren "%_oldp%" "%_newp%"
        dir /b
    popd 2>nul

    rd /s /q "%_testdir%"

    goto cleanup

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set   __DIR__=%~dp0
    set  __FILE__=%~dpnx0

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
    if "%~2"=="" goto help
    set _oldp=%~1
    set _newp=%~2
    shift
    shift

:prep3
    if "%~1"=="" goto init_ok
    set _rest=%_rest%%1
    shift
    goto prep3

:init_ok
    if %_verbose% geq 2 (set _ | tabify -b -d==)
    goto start

:version
    set _id=$Id$
    for /f "tokens=3-6" %%i in ("%_id%") do (
        set   _version=%%i
        set      _date=%%j
        set      _time=%%k
        set    _author=%%l
    )
    echo [TITLE] REN test utility
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    goto end

:help
    call :version
    echo.
    echo Syntax:
    echo    %__FILE__% [OPTION] oldpattern newpattern
    echo.
    echo Options:
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    goto end

:cleanup

:end
