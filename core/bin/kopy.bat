@echo off

    setlocal
    set _strict=1

    if "%~n1"=="cmd"    goto chsh
    if "%~n1"=="CMD"    goto chsh
    if "%~1"==":chsh"   goto chsh
    goto init

:start
:normal
    if not exist "%~1" (
        echo %1 isn't existed.
        goto end
    )
    set _base=%~nx1

  :_disable_sfc

  :_copy
    if "%~2"=="" (
    rem call :copy "%~1" "%windir%\ServicePackFiles\i386\%_base%"
        call :copy "%~1" "%windir%\System32\dllcache\%_base%"
        call :copy "%~1" "%windir%\System32\%_base%"
        call :copy "%~1" "%windir%\%_base%"
    ) else (
        call :copy "%~1" "%~2"
    )

  :_enable_sfc

  :_finalize
    if not "%_final%"=="" (
        echo finalizing...
        %_final%
    )
    echo done.
    goto cleanup

:chsh
:change_shell
    if "%~1"==":chsh" (
        waitpid -q %2
        shift
        shift
        REM set _final=start
        goto start
    )
    set _sh=%DIRT_HOME%\3\cmd.exe
    ppid
    start %_sh% /c %0 :chsh %errorlevel% %*
    exit

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
    goto end

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
    if "%~1"=="" goto help

:init_ok
    if %_verbose% geq 1 (
        echo _startdir=%_startdir%
        echo  _program=%_program%
        echo     _rest=%_rest%
        echo     _arg1=%_arg1%
    )
    goto start

:version
    set _id=$Id: kopy.bat,v 1.4 2007-08-16 10:21:44 lenik Exp $
    for /f "tokens=3-6" %%i in ("%_id%") do (
        set   _version=%%i
        set      _date=%%j
        set      _time=%%k
        set    _author=%%l
    )
    echo [TITLE] System/Kernel files replacer
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    goto end

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [OPTION] SOURCE
    echo        replace the system file of same name with SOURCE.
    echo    %_program% [OPTION] SOURCE DEST
    echo        replace the system file DEST with SOURCE
    echo.
    echo Options:
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    goto end

:cleanup

:end
