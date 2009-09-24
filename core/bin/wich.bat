@echo off

    setlocal
    set _strict=1
    goto init

:start
    set _count=0
    set _base=%~1
    set _ch=%_chdir%
    set _setdir=
    if "%_base:~-1%"=="/" (
        set _base=%_base:~0,-1%
        set _ch=1
    )
    for %%e in ("" %PATHEXT:;= %) do (
        set _nam=%_base%%%~e
        call :which%_verbose% "!_nam!" %_ch%
        if errorlevel 1 (
            if %_verbose% lss 0 goto ext_break
        )
    )
    :ext_break
    shift
    if "%_ch%"=="1" (
        if not "%_setdir%"=="" set _exitdir=%_setdir%
    )
    if not "%~1"=="" (
        echo.
        goto start
    )

    if %_count% equ 0 echo no %_base% found.
    if "%_exitdir%"=="" exit /b %_count%
    call export - _exitdir
    %leave%
    cd /d "%_exitdir%"
    exit /b

:which0
:which1
    set _target=%~dp$PATH:1
    if "%_target%"=="" exit /b 0
    set _setdir=%_target%
    set _target=%_target%%~1
    set _detail=%_target%
    goto found%_verbose%

:::::::
:which2
    set _path="%PATH:;=" "%"
    set _c=0
    for %%p in (%_path%) do (
        set _target=%%~p\%~1
        if exist "!_target!" (
            set _setdir=%%~p
            call :found%_verbose%
            set /a _c = _c + 1
        )
    )
    exit /b %_c%

:found2
    for %%f in ("%_target%") do (
        set _fshort=%%~sf
        set _fattr=%%~af
        set _ftime=%%~tf
        set _fsize=%%~zf
    )
    rem set _detail=%_target%:%_fsize%:%_ftime%:%_fattr%
    set _fsize=          %_fsize%
    set _fsize=%_fsize:~-10%
    set _detail=%_fattr% %_fsize% %_ftime% %_target%

:found1
    echo %_detail%

:found0
    if not "%_copyto%"=="" (
        copy /y "%_target%" "%_copyto%"
    )
    set /a _count = _count + 1
    exit /b 1

:init
    set  _verbose=1
    set      _ret=
    set     _rest=
    set   __DIR__=%~dp0
    set  __FILE__=%~dpnx0
    set    _chdir=0
    set   _copyto=
    set  _exitdir=

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
    ) else if "%~1"=="-c" (
        set _chdir=1
    ) else if "%~1"=="--chdir" (
        set _chdir=1
    ) else if "%~1"=="-p" (
        set _copyto=%~2
        shift
    ) else if "%~1"=="--copy-to" (
        set _copyto=%~2
        shift
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
    if %_verbose% geq 3 (set _ | tabify -b -d==)

    if %_verbose% geq 2 set _verbose=2
    goto start

:version
    set _id=$Id$
    for /f "tokens=3-6" %%i in ("%_id%") do (
        set   _version=%%i
        set      _date=%%j
        set      _time=%%k
        set    _author=%%l
    )
    echo [wich] Enhanced unix which program
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %__FILE__% [OPTION] ...
    echo.
    echo Options:
    echo    -c, --chdir         chdir into the found path
    echo    -p, --copy-to DEST  copy found path to dest
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    exit /b 0
