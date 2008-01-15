@echo off

    setlocal
    set _strict=1
    goto init

:start

    if "%USERNAME%"=="" set USERNAME=root
    set HOME=/home/%USERNAME%

    if exist b:\bin (
        cd /d b:\bin
    ) else if exist %CIRK_HOME%\Cygwin\bin (
        subst B: %CIRK_HOME%\Cygwin
        cd /d b:\bin
    )

    if "%SHELL%"=="" (
        if exist b:\bin\bash.exe (
            set SHELL=b:\bin\bash.exe
        ) else (
            set SHELL=bash
        )
    )

    %SHELL% %_rest%

    goto cleanup

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set _startdir=%~dp0
    set  _program=%~dpnx0

:prep1
    if "%~1"==""            goto init_ok
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
    if "%~1"=="" goto init_ok
    set _rest=%_rest%%~1
    shift
    goto prep2

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
    echo [SH] Shell for windows
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    goto end

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% ^<options^> ...
    echo.
    echo Options:
    echo    --quiet             (q)
    echo    --verbose           (v, repeat to get more info)
    echo    --version
    echo    --help              (h)
    goto end

:cleanup

:end
