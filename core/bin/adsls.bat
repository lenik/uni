@echo off

    setlocal
    set _strict=1
    goto init

:start

    REM ADS (Alternate Data Streams) List
    if "%~1"=="" (
        call :listdir .
        exit /b
    )

    :loop
    if exist "%~1\*" (
        call :listdir "%~1"
    ) else (
        adsnm %1
    )
    shift
    if not "%~1"=="" goto loop
    exit /b

:listdir
    for %%f in ("%~1\*") do (
        if "%p_unnamed%"=="0" (
            adsnm "%%f" >nul
            if errorlevel 1 exit /b 0
        )
        if "%format%"=="short" (
            echo %%f
        ) else if "%format%"=="long" (
            echo %%~af 1 none none %%~tf %%f
        )
        adsnm "%%f" | grep "^  :"
    )
    exit /b

:init
    set     _verbose=0
    set         _ret=
    set        _rest=
    set      __DIR__=%~dp0
    set     __FILE__=%~dpnx0
    set       format=short
    set    p_unnamed=1

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
    ) else if "%~1"=="-l" (
        set format=long
    ) else if "%~1"=="--long" (
        set format=long
    ) else if "%~1"=="-n" (
        set p_unnamed=0
    ) else if "%~1"=="--named-only" (
        set p_unnamed=0
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
    set _id=$Id: .bat 845 2008-09-29 11:45:47Z lenik $
    for /f "tokens=3-6" %%i in ("%_id%") do (
        set   _version=%%i
        set      _date=%%j
        set      _time=%%k
        set    _author=%%l
    )
    echo [adsls] File List With Streams
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %__FILE__% [OPTION] FILES
    echo.
    echo Options:
    echo    -l, --long          list in long format
    echo    -n, --named-only    list files with named-streams only
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    exit /b 0
