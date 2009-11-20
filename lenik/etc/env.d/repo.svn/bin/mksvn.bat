@echo off

    setlocal
    set _strict=1
    goto init

:start

    cd /d "%~dp0.."

    set name=%~1
    set prefix=%name:~0,1%

    echo create repository %prefix%\%name%

    rem set svnopts=--config-dir %~dp0..\etc

    echo %svnadmin% create "%prefix%\%name%"
    %svnadmin% create "%prefix%\%name%"

    echo configure
    copy /y "%~dp0..\etc\svnserve.conf" "%prefix%\%name%\conf" >nul

    echo done

    exit /b 0

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set _startdir=%~dp0
    set  _program=%~dpnx0

    set      svnd=
    set       svn=svn
    set  svnadmin=svnadmin
    set _svn1=%SystemDrive%\Program Files\CollabNet Subversion Server
    for %%i in (9 8 7 6 5 4 3 2 1) do (
        if exist "!_svn%%i!\svn.exe" set svnd=!_svn%%i!
    )
    if not "%svnd%"=="" (
        echo Warning: no svn executable, use default in PATH.
        set svn="%svnd%\svn.exe"
        set svnadmin="%svnd%\svnadmin.exe"
    )

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
    set _id=$Id: .bat 784 2008-01-15 10:53:24Z lenik $
    for /f "tokens=3-6" %%i in ("%_id%") do (
        set   _version=%%i
        set      _date=%%j
        set      _time=%%k
        set    _author=%%l
    )
    echo [mksvn] create svn repository and configure passwd
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [OPTION] REPO-NAME
    echo.
    echo Options:
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    exit /b 0
