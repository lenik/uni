@echo off

    setlocal
    set _strict=1

    if /i "%~n1"=="cmd" goto chsh
    if "%~1"==":chsh"   goto chsh
    goto init

:copy
    if exist "%~2" (
        diff "%~1" "%~2" >nul
        if errorlevel 2 (
            echo Overwriting %2...
            rem copy /y "%~1" "%~2" >nul
            wfpreplace  "%~2" "%~1" >nul
        )
    ) else (
        copy "%~1" "%~2" >nul
    )
    exit /b

:start
:normal
    set _src=%~1
    set _dir=%~dp1
    set _base=%~nx1
    if exist "%_src%" goto got_target

    set _dir=%~dp$PATH:1
    if exist "!_dir!%~nx1" (
        if not exist "!_dir!%~nx1\*" (
            set _src=!_dir!%~nx1
            goto got_target
        )
    )

    echo %1 isn't existed.
    exit /b 1

:got_target
  :_disable_sfc
  :_copy
    if "%~2"=="" (
    rem call :copy "%_src%" "%windir%\ServicePackFiles\i386\%_base%"
        call :copy "%_src%" "%windir%\System32\dllcache\%_base%"
        call :copy "%_src%" "%windir%\System32\%_base%"
        REM call :copy "%_src%" "%windir%\%_base%"
    ) else (
        call :copy "%_src%" "%~2"
    )
  :_enable_sfc

  :_finalize
    if not "%_final%"=="" (
        echo finalizing...
        %_final%
    )
    echo done.
    exit /b 0

:chsh
:change_shell
    if "%~1"==":chsh" (
        waitpid -q %2
        shift
        shift
        REM set _final=start
        goto start
    )
    set _sh=%LAPIOTA%\local\bin\cmd.exe
    diff "%_sh%" "%ComSpec%" >nul
    if not errorlevel 2 (
        echo already updated.
        exit /b
    )
    ppid
    start %_sh% /c %0 :chsh %errorlevel% %*
    exit

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set   __DIR__=%~dp0
    set  __FILE__=%~dpnx0

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
    echo [kopy] System/Kernel files replacer
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %__FILE__% [OPTION] SOURCE
    echo        replace the system file of same name with SOURCE.
    echo    %__FILE__% [OPTION] SOURCE DEST
    echo        replace the system file DEST with SOURCE
    echo.
    echo Options:
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    exit /b 0
