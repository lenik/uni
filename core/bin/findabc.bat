@echo off

    setlocal
    set _strict=1
    goto init

:start
    set _lev=0
    set _prefix=%LAPIOTA%\abc.d
:st_loop
    rem echo find with prefix: %_prefix%
    if exist %_prefix%\%_name%* goto found
:st_next
    set /a _lev = _lev + 1
    set _st=!_name:~0,%_lev%!
    if "%_st%"=="%_name%" (
        echo failed to find %_name%
        goto end
    )
    set _prefix=%_prefix%\%_st%
    goto st_loop

:found
    for /d %%i in (%_prefix%\%_name%*) do (
        set _home=%%i
        goto leave
    )
    goto st_next

:leave
    call export _home
    %leave%

:add_path
    if "%~1"=="" goto end
    if "%~1"=="." (
        set PATH=%_home%;%PATH%
    ) else (
        set PATH=%_home%\%~1;%PATH%
    )
    shift
    goto add_path

:init
    set  _verbose=0
    set      _ret=
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
    set _name=%~1
    shift

:init_ok
    if %_verbose% geq 1 (
        echo _startdir=%_startdir%
        echo  _program=%_program%
        echo     _name=%_name%
    )
    goto start

:version
    set _id=$Id: findabc.bat,v 1.1 2007-08-24 11:44:56 lenik Exp $
    for /f "tokens=3-6" %%i in ("%_id%") do (
        set   _version=%%i
        set      _date=%%j
        set      _time=%%k
        set    _author=%%l
    )
    echo [findabc] Find out directory/prefix of an installed abc-package
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    goto end

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [OPTION] abc-package
    echo.
    echo Options:
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    goto end

:cleanup

:end
