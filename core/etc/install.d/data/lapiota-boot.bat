@echo off

    setlocal
    set _strict=1
    goto init

:start
    if not "%LAPIOTA%"=="" goto located

    for /d %%i in (t lapiota) do (
        if exist "%%i\." (
            set LAPIOTA=%%~dpnxi
            goto located
        )
    )
    set LAPIOTA=C:\Lapiota

:located
    rd %LAPIOTA% 2>nul

    set pgd=lam.root.pgd
    for %%d in ("%homedrive%" c: d: e: f: g: u: v: w: x: y: z:) do (
        if exist "%%d\." (
            for %%f in (%%d\%pgd% %%d\.radiko\.miaj\image\%pgd%) do (
                if exist %%f (
                    set pgd=%%f
                    goto found
                )
            )
        )
    )
    rem if not found...

  :found
    md %LAPIOTA% 2>nul
    if not exist "%LAPIOTA%\." (
        echo Failed to reset the mount point, try again
        goto found
    )
    %pgd%

:boot
    cd /d %LAPIOTA%\etc\startup.d
    if "%BOOTLEVEL%"=="" set BOOTLEVEL=20
    for %%f in (*) do (
        set base=%%f
        set level=!base:~0,2!
        if "!level!" leq "!BOOTLEVEL!" (
            echo boot %%~nf
            call "%%f"
        )
    )
    pause
    exit /b 0

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
    if "%~1"=="" goto prep3
    set LAPIOTA=%~1
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
    set _id=$Id: .bat 784 2008-01-15 10:53:24Z lenik $
    for /f "tokens=3-6" %%i in ("%_id%") do (
        set   _version=%%i
        set      _date=%%j
        set      _time=%%k
        set    _author=%%l
    )
    echo [lapiota-boot] Lapiota Booter Program
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
