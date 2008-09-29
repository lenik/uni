@echo off

    setlocal
    set _strict=1
    goto init

:start

:nextarg
    if "%~1"=="" goto cleanup

    for /r %%f in (*.sum *.sig) do (
        set _ext=%%~xf
        call :chk_!_ext:~1! "%%f"
    )
    shift
    goto nextarg

:cleanup
    del "%_capout%" 2>nul
    if exist "%_errlog%" (
        echo Check error log:
        echo %_errlog%
        rem type "%_errlog%"
    )
    exit /b

:chk_sum
    pushd "%~dp1"
    set _vpre=%~dp1
    set _vpre=%_vpre:\=/%
    echo Check: %~1
    "%~n1sum" -c "%~nx1" >%_capout%
    if errorlevel 1 (
        echo Error:
        type "%_capout%"
        echo.

        echo # error: %~1 >>%_errlog%
        grep -v ": OK" "%_capout%" | cut -d: -f1 | sed "s|^|%_vpre%|" >>%_errlog%
        echo.>>%_errlog%
    )
    popd
    exit /b

:chk_sig
    echo sig unsupport: %1
    exit /b

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set _startdir=%~dp0
    set  _program=%~dpnx0

    set   _capout=%TMP%\.sum-%RANDOM%
    set   _errlog=%TMP%\.sumlog-%RANDOM%
    if exist "%_errlog%" del "%_errlog%"

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
    echo [ddchk] Directory Digest Check
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [OPTION] DIRS
    echo.
    echo Options:
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    exit /b 0
