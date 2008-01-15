@echo off

    setlocal
    set _strict=1
    goto init

:start
    if "%_hide%"=="1" call wm -hide

    call cput "%_progbase%"
         sleep %_sleep%
    call cput "%_progbase%"
    echo [%date% %time%] cput=%errorlevel% >>"%_logfile%"
    if errorlevel 1 exit /b

    echo [%date% %time%] restart %1 >>"%_logfile%"
    taskkill /f /im %_progbase%

    %_runas% /command:"cmdw /c start %_progbase% %_rest%"

    if "%_hide%"=="1" call wm -show

    exit /b 0

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set _startdir=%~dp0
    set  _program=%~dpnx0
    set    _sleep=60
    set     _hide=0
    set     _user=%USERNAME%
    set   _passwd=
    if exist "%HOME%\.passwd" set /p _passwd=<"%HOME%\.passwd"
    set   _logdir=%HOME%\logs\%~n0
    if not exist "%_logdir%\*" md "%_logdir%" >nul 2>nul
    set  _logfile=
    set  _runpath=
    set  _progdir=
    set _progbase=

:prep1
    if "%~1"==""            goto prep2
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
    ) else if "%~1"=="-v" (
        set /a _verbose = _verbose + 1
    ) else if "%~1"=="-m" (
        set _hide=1
    ) else if "%~1"=="-s" (
        set _sleep=%~2
        shift
    ) else if "%~1"=="-u" (
        set _user=%~2
        shift
    ) else if "%~1"=="-p" (
        set _passwd=%~2
        shift
    ) else if "%~1"=="-l" (
        set _logfile=%~2
        shift
    ) else if "%~1"=="-c" (
        set _runpath=%~2
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
    set _progdir=%~dp1
    set _progbase=%~nx1
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
    if "%_logfile%"=="" set _logfile=%_logdir%\%_progbase%.log
    if "%_runpath%"=="" (
        if "%_progdir%"=="" (
            set _runpath=%CD%
        ) else (
            set _runpath=%_progdir%
        )
    )
    set _runas=lsrunas /user:%_user% /password:%_passwd% /domain:
    if not "%_runpath%"=="" set _runas=%_runas% /runpath:"%_runpath%"

    if %_verbose% geq 2 (set _ | tabify -b -d==)
    goto start

:version
    set _id=$Id: .bat 759 2007-11-29 14:45:59Z Lenik $
    for /f "tokens=3-6" %%i in ("%_id%") do (
        set   _version=%%i
        set      _date=%%j
        set      _time=%%k
        set    _author=%%l
    )
    echo [killzomb] Kill zombie processes
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [OPTION] PROGRAM [ARGUMENTS]
    echo.
    echo Options:
    echo    -s, --sleep=SEC     time to check if a process is dead
    echo    -m, --hide          hide the console when checking
    echo    -u, --user=USER     run as USER
    echo    -p, --passwd=PASSWD the password of the USER
    echo    -l, --logfile=FILE  the default log file is $HOME/logs/PROGRAM.log
    echo    -c, --changedir=DIR running under this directory
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    exit /b 0
