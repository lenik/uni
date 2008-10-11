@echo off

    setlocal
    set _strict=1
    goto init

:start
    if not "%_mpoint%"=="" goto f_mount

:f_info
    set _eval=partcp -f "%_pgdfile%" -x0x60 -yx/00
    for /f "usebackq delims=|" %%i in (`%_eval%`) do set _mpoint=%%i

    call export - _mpoint
    %leave%
    exit /b 0

:f_mount
    REM pgdinfo - currentmpoint
    REM for %d in (%currentmpoint%) do set attr=%~ad
    REM 'd-------l'
    REM if "%attr:~-1%"=="l" echo already mounted.

    REM touch -r "%_pgdfile%" "%_pgdfile%" 2>nul
    canwrite "%_pgdfile%"

    if errorlevel 1 (
        echo %_pgdfile% is already mounted
        if %_verbose% geq 1 (
            call :f_info
            echo   on !_mpoint!
        )
        exit /b 1
    )

    if not exist "%_mpoint%" (
        echo Mount point isn't existed: %_mpoint%
        set /p _mkdir=Create? ^(Y/N^)
        if not "!_mkdir!"=="y" (
            echo User canceled.
            exit /b 1
        )
        mkdir "%_mpoint%"
    ) else (
        rd "%_mpoint%" 2>nul
        if exist "%_mpoint%\." (
            echo Failed to reset the mount point.
            echo Try a force clean?
            echo **DANGEROUS** THIS WILL REMOVE ALL FILES UNDER %_mpoint%.
            set /p _force=^(y/n^)
            if not "!_force!"=="y" (
                echo User canceled.
                exit /b 1
            )
            rd /s /q "%_mpoint%"
        )
        md "%_mpoint%" 2>nul
    )

    if %_verbose% geq 1 echo DANGEROUS OPERATION: write the mount point to pgdfile
    call partcp -a "%_mpoint%" -o "%_pgdfile%" -z0x60

    if %_verbose% geq 1 echo DANGEROUS OPERATION: recalc the header crc.
    call partcp -f "%_pgdfile%" -Dfill-range=12,16 -Pcrc.pgp -l0x160 -o "%_pgdfile%" -z12

    start /wait "Mounting..." "%_pgdfile%"
    exit /b

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set _startdir=%~dp0
    set  _program=%~dpnx0
    set  _pgdfile=
    set   _mpoint=

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
    set _pgdfile=%~1
    shift
    if not "%~1"=="" (
        set _mpoint=%~dpnx1
        shift
    )

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
    set _id=$Id: pgdinfo.bat 857 2008-10-10 10:26:04Z lenik $
    for /f "tokens=3-6" %%i in ("%_id%") do (
        set   _version=%%i
        set      _date=%%j
        set      _time=%%k
        set    _author=%%l
    )
    echo [mount.pgd] Mount PGP Disk
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [OPTION] PGDFILE [PATH]
    echo.
    echo Options:
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    exit /b 0
