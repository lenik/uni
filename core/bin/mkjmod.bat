@echo off

    setlocal
    set _strict=1
    goto init

:start

    REM _____________________________________________

    call :findup .project
    if errorlevel 1 (
        echo Not within an eclipse project.
        exit /b 1
    )

    for /d %%d in ("%_dir%") do set _prjdir=%%~dpnxd
    echo project: [%_prjdir%]

:loop
    if "%~1"=="" exit /b 0

    set _modrel=mod\%~1
    if exist "%_modrel%" (
        if not "%_force%"=="1" (
            echo module %~1 already existed.
            exit /b 1
        )
    ) else (
        md "%_modrel%"
    )
    for /d %%d in ("%_modrel%") do set _moddir=%%~dpnxd
    set _moddir=!_moddir:%_prjdir%=!
    set _moddir=%_moddir:~1%
    set _moddir=%_moddir:\=/%
    echo module: [%_moddir%]

    pushd "%_moddir%" >nul
        if not exist src\* md src
        if not exist bin\* md bin
        if not exist test\* md test
        if not exist test.bin\* md test.bin
    popd >nul

    set         _entry=\lclasspathentry kind=\qsrc\q path=\q%_moddir%/src\q  output=\q%_moddir%/bin\q/\g
    set _entry=%_entry%\lclasspathentry kind=\qsrc\q path=\q%_moddir%/test\q output=\q%_moddir%/test.bin\q/\g
    call xmledit -f "%_prjdir%\.classpath" -S "%_entry%" -c /classpath -O pretty -T "concat(@kind, '-', @path)" -t //classpathentry

    shift
    goto loop

:findup
    set _dir=
    set _i=0
  :findup_loop
    set /a _i = _i + 1
    if %_i% geq 32 exit /b 1
    if not exist "%_dir%%~1" (
        set _dir=%_dir%..\
        goto findup_loop
    )
    if "%_dir%"=="" (
        set _dir=.
    ) else (
        set _dir=%_dir:~0,-1%
    )
    exit /b 0

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set _startdir=%~dp0
    set  _program=%~dpnx0
    set    _force=

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
    ) else if "%~1"=="-f" (
        set _force=1
    ) else if "%~1"=="--force" (
        set _force=1
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
    echo [mkjmod] create new modules for eclipse project
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [OPTION] ...
    echo.
    echo Options:
    echo    -f, --force         overwrite existing modules
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    exit /b 0
