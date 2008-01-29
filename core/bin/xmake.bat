@echo off

    setlocal
    set _strict=1
    goto init

:start

    make %_makeopts% %_rest%

    if not "%_exec%"=="1" goto end

:exec
    if not "%_chdir%"=="" pushd "%_chdir%" >nul
    for %%f in (*.exe) do (
        clear
        %%f
        goto exec_end
    )
:exec_end
    if not "%_chdir%"=="" popd >nul

:end
    if "%_pause%"=="1" pause
    exit /b 0

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set _startdir=%~dp0
    set  _program=%~dpnx0
    set    _chdir=
    set     _exec=
    set    _pause=
    set _makeopts=

:: BUGFIX (512-ALIGNMENT) :::::::::::::::::::::::::::::::::::::::::::::::::::::
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
    ) else if "%~1"=="-p" (
        set _pause=1
    ) else if "%~1"=="--pause" (
        set _pause=1
    ) else if "%~1"=="-C" (
        set _chdir=%~2
        shift
    ) else if "%~1"=="-x" (
        set _exec=1
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
    if not "%_chdir%"=="" (
        set _makeopts=%_makeopts% -C "%_chdir%"
    )
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
    echo [xmake] launch make program for windows
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    echo.
    make --version
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [OPTION] ...
    echo.
    echo Options:
    echo    -C=DIR              chdir to DIR before make
    echo    -x                  execute the program after made
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    echo.
    make --help
    exit /b 0
