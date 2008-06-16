@echo off

    setlocal
    set _strict=1
    goto init

:start

    if %_verbose% geq 1 echo xcopy %_xcopyopts% %_rest%
    xcopy %_xcopyopts% %_src% %_dst% %_rest% %_out%

    exit /b 0

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set _startdir=%~dp0
    set  _program=%~dpnx0
    set      _out=
    set     _jfix=

    rem /c  error continue
    rem /d  update only
    rem /h  include hidden files
    rem /r  overwrite readonly files
    rem /y  don't prompt
    rem /e  empty directories
    set _xcopyopts=/d /h /r

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
        set _xcopyopts=%_xcopyopts% /c /y
        set _out=^>nul
    ) else if "%~1"=="--force" (
        set _xcopyopts=%_xcopyopts% /c /y
        set _out=^>nul
    ) else if "%~1"=="-r" (
        set _xcopyopts=%_xcopyopts% /e
    ) else if "%~1"=="--recursive" (
        set _xcopyopts=%_xcopyopts% /e
    ) else if "%~1"=="-J" (
        set _jfix=1
    ) else if "%~1"=="--jfix" (
        set _jfix=1
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
    set _src=%~1
    if "%_jfix%"=="1" set _src=%_src%\*
    shift

    set _dst=%~1
    if "%~1"=="" goto prep3
    if "%_jfix%"=="1" set _dst=%_dst%\*
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
    echo [cpnew] copy new/updated files only
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [OPTION] SOURCE [DEST]
    echo.
    echo Options:
    echo    -f, --force         overwrite existing files without prompt
    echo    -r, --recursive     recurse into sub-directories
    echo    -J, --jfix          a work-around for NTFS-Junction
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    exit /b 0
