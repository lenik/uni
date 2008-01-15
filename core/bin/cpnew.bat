@echo off

    setlocal
    set _strict=1
    set flags=/d /h /r
    goto init

:start

    if "%_rest%"=="" goto help

    rem /d      update only
    rem /h      include hidden files
    rem /r      overwrite readonly files
    rem /y      don't prompt
    rem /e      empty directories

    if %_verbose% geq 1 echo xcopy %flags% %_rest%
    xcopy %flags% %_rest%

    goto cleanup

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set _startdir=%~dp0
    set  _program=%~dpnx0

:prep1
    if "%~1"==""            goto init_ok
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
    ) else if "%~1"=="-f" (
        set flags=%flags% /c /y
    ) else if "%~1"=="--force" (
        set flags=%flags% /c /y
    ) else if "%~1"=="-r" (
        set flags=%flags% /e
    ) else if "%~1"=="--recursive" (
        set flags=%flags% /e
    ) else if "%_arg:~0,1%"=="-" (
        if "%_strict%"=="1" (
            echo Invalid option: %1
            goto end
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
    if "%~1"=="" goto init_ok
    set _rest=%_rest%%~1
    shift
    goto prep2

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
    echo [cpnew] copy new/updated files only
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    goto end

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [OPTION] ...
    echo.
    echo Options:
    echo    -r, --recursive     recurse into sub-directories
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    goto end

:cleanup

:end
