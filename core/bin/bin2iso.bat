@echo off

    setlocal
    set _strict=1
    goto init

:start

    REM _____________________________________________
    if %_verbose% geq 1 echo [bin2iso] %_bin%

    if %_verbose% geq 2 echo bchunk "%_bin%" "%_cue%" "%_out%"
	bchunk "%_bin%" "%_cue%" "%_out%"

    if "%_delete%"=="1" (
        if %_verbose% geq 1 echo delete %_bin%
        if %_verbose% geq 1 echo delete %_cue%
        del "%_bin%" >nul
        del "%_cue%" >nul
    ) else if not "%_move%"=="" (
        if %_verbose% geq 1 echo move %_bin%
        if %_verbose% geq 1 echo move %_cue%
        move "%_bin%" "%_move%"
        move "%_cue%" "%_move%"
    )
    exit /b 0

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set _startdir=%~dp0
    set  _program=%~dpnx0
    set      _out=
    set     _move=
    set   _delete=

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
    ) else if "%~1"=="-d" (
        set _delete=1
    ) else if "%~1"=="--delete" (
        set _delete=1
    ) else if "%~1"=="-o" (
        set _out=%~1
        shift
    ) else if "%~1"=="--out" (
        set _out=%~1
        shift
    ) else if "%~1"=="-m" (
        set _move=%~1
        shift
    ) else if "%~1"=="--moveto" (
        set _move=%~1
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

    set _bin=%~1
    set _dir=%~dp1
    set _base=%~nx1
    set _cue=%~dpn1.cue

    if "%~x1"==".cue" set _bin=%~dpn1.bin
    if "%~x1"=="" set _bin=%~1.bin

    if not exist "%_bin%" (
        echo .bin file: %_bin% isn't existed.
        exit /b 1
    )
    if not exist "%_cue%" (
        echo .cue file: %_cue% isn't existed.
        exit /b 1
    )

    if not "%~2"=="" (
        if "%~x2"==".cue" (
            set _cue=%~2
        ) else (
            set _out=%~2
        )
    )
    if not "%~3"=="" (
        set _out=%~3
    )

    if "%_out%"=="" (
        set _out=%~dpn1.iso
    )
    if exist "%_out%\*" (
        set _out=%_out%\%_base%.iso
    )

    shift
    shift
    shift

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
    echo [bin2iso] Convert .bin/.cue to .iso, based on bchunk program
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [OPTION] BINFILE [CUEFILE] [ISOFILE]
    echo.
    echo Options:
    echo    -o, --output=ISOFILE
    echo    -m, --moveto=DIR    move the converted .bin/.cue files to DIR
    echo    -d, --delete        delete the converted .bin/.cue files
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    exit /b 0
