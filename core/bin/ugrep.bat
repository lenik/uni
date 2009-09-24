@echo off

    setlocal
    set _strict=1
    goto init

:start

    REM _____________________________________________
    grep %_flags% "%_upat%" %_rest%

    exit /b 0

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set   __DIR__=%~dp0
    set  __FILE__=%~dpnx0
    set    _flags=-Pabo

:prep1
    if "%~1"==""            goto prep2
    if "%~1"=="--version"   goto version
    if "%~1"=="--help"      goto help
    if "%~1"=="-?" (
        set _verbose=1
        shift
    )
    if "%~1"=="--verbose" (
        set _verbose=1
        shift
    )
    if "%~1"=="--" (
        shift
    )

:prep2
    if "%~1"=="" goto init_ok
    set A=%~1
    if "%A:~0,1%"=="-" (
        set _flags=%_flags% %~1
    ) else (
        set _pat=%~1
        set _t=%~1
        shift
        goto prep2b
    )
    shift
    goto prep2

:prep2b
    if "%_t%"=="" goto prep2c
    set _upat=%_upat%%_t:~0,1%\x00
    set _t=%_t:~1%
    goto prep2b

:prep2c
    if not %_verbose% geq 1 goto prep3
    for /f %%i in ('call hex -d -- "%_pat%"') do set _hex=%%i
    echo HEX DUMP:
    echo    %_hex%
  :loop2c
    if "%_hex%"=="" goto end2c
    set _uhex=%_uhex%%_hex:~0,2%00
    set _hex=%_hex:~2%
    goto loop2c
  :end2c
    echo UNICODE-HEX DUMP:
    echo    %_uhex%

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
    echo [ugrep] unicode-grep
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    echo.
    grep --version
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %__FILE__% [u-OPTION] [grep-OPTION] [--] PATTERN ...
    echo.
    echo u-Options:
    echo    -?, --verbose       repeat to get more info
    echo        --version       show version info
    echo        --help          show this help page
    echo.
    grep --help
    exit /b 0
