@echo off

    setlocal
    set _strict=1
    goto init

:start

    REM _____________________________________________
    if not "%~1"=="" (
        cd /d "%~1" 2>nul
        if errorlevel 1 (
            echo failed to chdir: %~1
            exit /b 1
        )
    )

    set _shot=
    for /d %%i in (*) do (
        set _attr=%%~ai
        if "!_attr:~-1!"=="l" set _shot=!_shot! "%%i"
    )

    for /d %%i in (%_shot%) do (
	rd "%%~i" 2>nul
        if exist "%%~i\*" (
            if %_verbose% geq 1 echo [dir] %%~dpnxi
        ) else (
            set _i=%%~i
            set _xi=%%~dpnxi
:mdloop
            md "!_i!" 2>nul
		if %_verbose% geq 0 echo [fix] !_xi!
            if not exist "!_i!\*" goto mdloop
        )
    )

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
    if "%~1"=="" goto help
    rem set _arg1=%~1
    rem shift

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
    echo [remd] Re-mkdir to fix ntfs junctions
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
