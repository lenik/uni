@echo off

    setlocal
    set _strict=1
    goto init

:start

:next_file
    if "%~1"=="" (
        set _=%1.
        set _=!_:"=?!
        if !_!==. goto end_files
    )
    for %%f in ("%~1") do (
        if "%_del%"=="1" (
            if %_verbose% geq 1 echo delete path %%f
            reg delete "%_key%\%%~nxf" /f >nul
        ) else (
            if %_verbose% geq 1 echo add path %%f
            reg add "%_key%\%%~nxf" /f /ve /d "%%~fsf" >nul
            reg add "%_key%\%%~nxf" /f /v PATH /t REG_SZ /d "%%~dpf" >nul
        )
    )
    shift
    goto next_file
:end_files
    exit /b

:init
    set  _verbose=1
    set      _ret=
    set     _rest=
    set _startdir=%~dp0
    set  _program=%~dpnx0
    set      _all=1

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
    ) else if "%~1"=="-d" (
        set _del=1
    ) else if "%~1"=="--delete" (
        set _del=1
    ) else if "%~1"=="-u" (
        set _all=0
    ) else if "%~1"=="--user" (
        set _all=0
    ) else if "%~1"=="-a" (
        set _all=1
    ) else if "%~1"=="--all" (
        set _all=1
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

    if "%_all%"=="1" (
        set _key=HKLM\SOFTWARE\Microsoft\Windows\CurrentVersion\App Paths
    ) else (
        set _key=HKCU\SOFTWARE\Microsoft\Windows\CurrentVersion\App Paths
    )

:init_ok
    if %_verbose% geq 2 (
        echo _startdir=%_startdir%
        echo  _program=%_program%
        echo     _rest=%_rest%
        echo      _del=%_del%
        echo      _all=%_all%
        echo      _key=%_key%
    )
    goto start

:version
    set _id=$Id$
    for /f "tokens=3-6" %%i in ("%_id%") do (
        set   _version=%%i
        set      _date=%%j
        set      _time=%%k
        set    _author=%%l
    )
    echo [TITLE] Add/remove path shortcuts in the registry
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [OPTION] FILE-LIST
    echo.
    echo Options:
    echo    -d, --delete        delete the path
    echo    -u, --user          affect only current user
    echo    -a, --all           affect all users (default)
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    exit /b 0
