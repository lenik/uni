@echo off

    setlocal
    set _strict=1
    goto init

:start

    :loop
        set yyyy=%DATE:~0,4%
        set /a _yyyy=yyyy+0
        if "%yyyy%"=="%_yyyy%" (
            set mm=%DATE:~5,2%
            set dd=%DATE:~8,2%
        ) else (
            set yyyy=%DATE:~4,4%
            set mm=%DATE:~9,2%
            set dd=%DATE:~12,2%
        )
        set hms=%TIME:~0,2%:%TIME:~3,2%:%TIME:~6,2%
        set title=%yyyy%-%mm%-%dd% %hms%
        set line=
        set /p line=%title% -

        if "%line%"=="" exit /b 0
        if "%line%"=="." goto loop

        set db=%HOME%\.qlog\%yyyy%
        if not exist "%db%\*" md "%db%" 2>nul
        set db=%db%\%yyyy%-%mm%.log

        if "%line:~0,1%"=="\" (
            call :mod %line:~1%
        ) else (
            echo %title%>>"%db%"
            echo %line% >>"%db%"
            echo.>>"%db%"
        )

    goto loop

:mod
    setlocal
    set cmd=%~1
    shift
    goto _%cmd%
    echo ERROR COMMAND!
    exit /b

    :_e
        call no "%db%"
        exit /b

    :_l
        set n=3
        if not "%~1"=="" set n=%~1
        sed -n "2~3p" <"%db%" | tail -n%n%
        exit /b

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
    echo [qlog] Quick Logger
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
