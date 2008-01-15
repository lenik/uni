@echo off

    setlocal
    set _strict=1
    goto init

:start
    set _=perl -Mcmt::perlsys
    set _=%_% -e "my @p = which_package '%_name%' or exit 1; "
    set _=%_% -e "print join(chr(10), @p), chr(10); "
    %_%
    if not errorlevel 1 goto cleanup

    set _=perl -Mcmt::perlsys
    set _=%_% -e "my $st = which_sub '%_name%' or exit 1; "
    set _=%_% -e "print $st, chr(10); "
    for /f %%i in ('%_%') do (
        set _package=%%i
    )
    if not "%_package%"=="" (
        echo %_package%
        if "%_all%"=="1" (
            call %_program% "%_package%"
        )
        goto cleanup
    )

    echo don't know anything about %_name%.
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
    ) else if "%~1"=="-a" (
        set _all=1
    ) else if "%~1"=="--all" (
        set _all=1
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
    set _name=%~1
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
    echo [whichpm] find path to a perl module which contains the exported sub
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    goto end

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [OPTION] full-qualified-package-name
    echo    %_program% [OPTION] exported-sub-name
    echo.
    echo Options:
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    goto end

:cleanup

:end
