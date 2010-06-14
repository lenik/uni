@echo off

    setlocal
    set _strict=1
    goto init

:start

    if not exist "%_outdir%\." md "%_outdir%"

    set _opts=-volume "%_vol%" -ilong -jlong -directory
    "%_ultraiso%" %_opts% "%_src%" -out "%_out%"

    exit /b 0

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set _startdir=%~dp0
    set  _program=%~dpnx0
    set _ultraiso=C:\Program Files\UltraISO\UltraISO.exe
    set      _vol=Noname
    set      _src=

    for %%i in ("%home%\.isozip.*") do set _outdisk=%%~xi:
    if not "%_outdisk%"=="" set _outdisk=%_outdisk:~1%
    if "%_outdisk%"=="" set _outdisk=C:

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
    ) else if "%~1"=="--vol" (
        set _vol=%~2
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

    set _src=%~dpnx1

    set _vol=%~nx1
    if exist "%~dpnx1\.LABEL" set /p _vol=<"%~dpnx1\.LABEL"

    if "%_outdisk%"=="" set _outdisk=%~d1
    set _outdir=%_outdisk%%~p1
    set _out=%_outdir%%~nx1.iso

    shift

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
    if %_verbose% geq 1 (set _ | tabify -b -d==)
    goto start

:version
    set _id=$Id: .bat 784 2008-01-15 10:53:24Z lenik $
    for /f "tokens=3-6" %%i in ("%_id%") do (
        set   _version=%%i
        set      _date=%%j
        set      _time=%%k
        set    _author=%%l
    )
    echo [isozip] convert directory to iso image
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [OPTION] DIR
    echo.
    echo Options:
    echo    -d, --disk=OUTDISK  default to $HOME/.isozip.DISK
    echo        --vol=VOLNAME   default to DIR/.LABEL
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    exit /b 0
