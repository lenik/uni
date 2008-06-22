@echo off

    setlocal
    set _strict=1
    goto init

:start

    REM _____________________________________________

    cmd /c %_rest%

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
    if "%~2"=="" goto help
    set TARGET=%~1
    set TARGET_DIR=%~dp1
    set TARGET_BASE=%~nx1
    set TARGET_NAME=%~n1
    set TARGET_EXT=%~x1
    set TARGET_DATE=%~t1
    call :time "%~t1"
    set TARGET_TIME=%_%
    shift

    set PREQ_ALL=
    set _n=0
    set _nexpire=0
:prep2b
    if "%~1"=="" goto prep3
    if "%~1"=="-" (
        shift
        goto prep3
    )
    set PREQ_ALL=%PREQ_ALL% %1
    set /a _n = _n + 1
    set PREQ%_n%=%~1
    set PREQ%_n%_DIR=%~dp1
    set PREQ%_n%_BASE=%~nx1
    set PREQ%_n%_NAME=%~n1
    set PREQ%_n%_EXT=%~x1
    set PREQ%_n%_DATE=%~t1
    call :time "%~t1"
    set PREQ%_n%_TIME=%_%
    if "%TARGET_TIME%" lss "%_%" set /a _nexpire = _nexpire + 1
    shift
    goto prep2b

:prep3
    set PREQ_COUNT=%_n%
    set PREQ_EXPIRE_COUNT=%_nexpire%

    if %_verbose% geq 1 (
        set TARGET
        set PREQ
    )

    if %PREQ_EXPIRE_COUNT% equ 0 exit /b 0
    if "%~1"=="" exit /b %PREQ_EXPIRE_COUNT%

:prep3b
    if "%~1"=="" (
        set _=%1.
        set _=!_:"=?!
        if !_!==. goto init_ok
    )
    set _rest=%_rest%%1
    shift
    goto prep3b

:time
    set _=-1
    if "%~1"=="" exit /b
    set _date=%~1%
    set /a _=(%_date:~0,4% - 1980)
    set /a _=%_% * 372 + (1%_date:~5,2% - 101) * 31 + 1%_date:~8,2% - 101
    set /a _=%_% * 1440+ (1%_date:~11,2%- 100) * 60 + 1%_date:~14,2%- 100
    exit /b

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
    echo [makefile] a tiny make program
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [OPTION] [--] TARGET PREQ... [- CMDLINE]
    echo.
    echo Options:
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    echo.
    echo Variables:
    echo    TARGET TARGET_{DIR, BASE, NAME, EXT, DATE, TIME}
    echo    PREQn  PREQn_{DIR, BASE, NAME, EXT, DATE, TIME}
    echo    PREQ_{ALL, COUNT, EXPIRE_COUNT}
    echo.
    echo If CMDLINE isn't specified, the exit value is set to the expiration
    echo count to PREQ LIST.
    exit /b 0
