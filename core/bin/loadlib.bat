@echo off

    set _export=%TEMP%\loadlib_%RANDOM%.bat

    setlocal
    set _strict=1
    goto init

:start

    REM _____________________________________________

:loop
    if "%~1"=="" goto saveExit
    if not exist "%~1\%_libini%" goto next

    set _libid=%~1
    set _libid=%_libid:\=_%
    set _libid=%_libid::=_%
    set _libid=%_libid:/=_%
    if not "!_lib_%_libid%!"=="" goto next

    call :loadini lib "%~1\%_libini%"
    echo set _lib_%_libid%=1 >>%_export%

:next
    shift
    goto loop

:saveExit
    rem call export
    rem %leave%
    endlocal
    if exist "%_export%" (
        call "%_export%"
        del /f "%_export%" >nul 2>nul
    )
    set _export=
    exit /b

:loadini
    set _prefix=%~1
    shift

    set _dir=%~dp1
    for /f "delims== tokens=1,2 usebackq" %%i in ("%~1") do (
        set _k=%%i
        set _v=%%j
        set _k=!_k: =!
        set _v=!_v: =!
        echo set %_prefix%_!_k!=%_dir%!_v!>>"%_export%"
    )
    exit /b

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set _startdir=%~dp0
    set  _program=%~dpnx0
    set   _libini=libraries.ini

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
    ) else if "%~1"=="-i" (
        set _include=%~2
        shift
    ) else if "%~1"=="--include" (
        set _include=%~2
        shift
    ) else if "%~1"=="-e" (
        set _exclude=%~2
        shift
    ) else if "%~1"=="--exclude" (
        set _exclude=%~2
        shift
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
    echo [loadlib] Load variables defined in libraries.ini under spec libdir.
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [OPTION] DIR...
    echo.
    echo Options:
    echo    -i, --include=WILDCHAR
    echo    -e, --exclude=WILDCHAR
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    exit /b 0
