@echo off

    setlocal
    set _strict=1
    call lapiota-init
    goto init

:start
    set _script=%TMP%\%_name%.bat
    set _scriptbak=%TMP%\%_name%_bak.bat
    REM call wm -hide

:waitLoop
    REM warning: windows application may be async launched.
    start /wait winevent -x -w "%_name%"
    if errorlevel 1 (
        echo can't start the service.
        sleep 1
        goto waitLoop
    )

    if %_verbose% geq 1 (
        echo [%DATE% %TIME%] recv %_script%
    )
    type "%_script%" >"%_scriptbak%"
    del "%_script%"
    call "%_scriptbak%" "%_name%"

    goto waitLoop

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
    if "%~1"=="" (
        set _name=fast0
    ) else (
        set _name=%~1
        shift
    )

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
    echo [fastcmd] Fast CMD Server
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %__FILE__% [OPTION] SERVERNAME
    echo.
    echo Options:
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    exit /b 0
