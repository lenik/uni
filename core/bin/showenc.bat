@echo off

    setlocal
    set _strict=1
    goto init

:start

    REM _____________________________________________

    cd %temp%

    set _t=showenc.%random%

    echo %_rest%>%_t%

    rem trim the last 3 bytes
    for %%f in (%_t%) do set _size=%%~zf
    set /a _size = _size - 3
    head -c%_size% %_t% >%_t%.b

    call reencoding -f gb2312 -t %_charset% %_t%.b >nul
    hexdump -C %_t%.b >%_t%
    for /f "delims=|" %%i in (%_t%) do (
        call :disp "%%i"
    )

    del %_t%
    del %_t%.b
    exit /b 0

:disp
    set _l=%~1
    set _l=%_l:~9%
    if "%_l%"=="" exit /b 0

    set _l=%_l:  =%
    if "%_l:~-1%"==" " set _l=%_l:~0,-1%
    if not "%_fill%"=="" (
        set _l=!_l: =%_fill%!
    )
    echo %_l%
    exit /b 0

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set _startdir=%~dp0
    set  _program=%~dpnx0
    set     _fill=

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
    ) else if "%~1"=="-u" (
        set _fill=%%
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
    set _charset=%~1
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
    echo [showenc] show the encoding of a given string
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [OPTION] CHARSET STRING
    echo.
    echo Options:
    echo    -u                  add ^%
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    exit /b 0
