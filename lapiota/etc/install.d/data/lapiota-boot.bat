@echo off

    setlocal
    set _strict=1
    goto init

:start
    title Booting...

    if "%LAPIOTA%"=="" (
        echo Lapiota isn't installed correctly.
        pause >nul
        exit /b 1
    )

:located
    if exist "%LAPIOTA%\.LAPIOTA" (
        echo Already mounted.
        goto mounted
    )

    rd %LAPIOTA% 2>nul
    if exist "%LAPIOTA%\." (
        echo Failed to reset the mount point.
        echo Try a force clean?
        echo **DANGEROUS** THIS WILL REMOVE ALL FILES UNDER %LAPIOTA%.
        set /p _force=^(y/n^)
        if not "!_force!"=="y" exit /b 1
        rd /s /q "%LAPIOTA%" 2>nul
    )
    md %LAPIOTA% 2>nul

    if "%LAM_ROOT%"=="" set LAM_ROOT=%LAPIOTA:~0,-5%
    for %%b in (%LAM_ROOT%\boot\*.bat) do (
        call %%b
    )

    if not exist "%LAPIOTA%\.LAPIOTA" (
        echo Lapiota isn't mounted.
        pause >nul
        exit /b 2
    )

:mounted
    REM the .bat version of startup scripts are treated especially.
    cd /d %LAPIOTA%\etc\startup.d
    for %%f in (*.bat) do (
        REM echo boot %%~nf
        call "%%f"
    )
    if exist %HOME%\etc\startup.d\* (
        cd /d %HOME%\etc\startup.d
        for %%f in (*.bat) do (
            REM echo user boot %%~nf
            call "%%f"
        )
    )

    bash -c $LAM_KALA/etc/startup

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
    if "%~1"=="" goto prep3
    set LAPIOTA=%~1
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
    set _id=$Id$
    for /f "tokens=3-6" %%i in ("%_id%") do (
        set   _version=%%i
        set      _date=%%j
        set      _time=%%k
        set    _author=%%l
    )
    echo [lapiota-boot] Lapiota Booter Program
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [OPTION] ...
    echo.
    echo Options:
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    exit /b 0
