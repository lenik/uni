@echo off

    setlocal
    set _strict=1
    set format=
    goto init

:start
    set i=0
    set pre=
    set post=
    set state=pre
    if "%~1"=="" (
        echo file pattern isn't specified.
        goto help
    )
    set files=%1
    shift

    :insert
        if "%~1"=="" goto run
        if %i% equ %n% set state=post
        if "%~1"=="--" (
            set files=%files%%pre%%post%
            set pre=
            set post=
            set state=pre
            set i=0
            shift
            goto insert
        )
        if "%state%"=="pre" (
            set pre=%pre% %1
        ) else (
            set post=%post% %1
        )
        shift
        set /a i = i + 1
        goto insert

    :run
        if not "%pre%"=="" set pre=%pre:~1%
        for %%i in (%files%) do (
            if %_verbose% geq 1 echo [s/%n%] %%~dpnxi
            if "%format%"=="dpnx" (
                %pre% %%~dpnxi%post%
            ) else (
                %pre% %%i%post%
            )
        )

    exit /b 0

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
    ) else if "%~1"=="-f" (
        set format=dpnx
    ) else if "%~1"=="--fullpath" (
        set format=dpnx
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
    set _n=%~1
    set n=
    if "%_n:~0,1%"=="/" set n=%_n:~1%
    if "%_n:~0,1%"=="-" set n=%_n:~1%
    if "%n%"=="" (
        rem n=default(1)
        set n=1
    ) else (
        set /a n = n
        shift
    )

:init_ok
    if %_verbose% geq 1 (
        echo _startdir=%_startdir%
        echo  _program=%_program%
        echo     _rest=%_rest%
        echo         n=%n%
    )
    goto start

:version
    set _id=$Id$
    for /f "tokens=3-6" %%i in ("%_id%") do (
        set   _version=%%i
        set      _date=%%j
        set      _time=%%k
        set    _author=%%l
    )
    echo [s/n] Substitute at argument#n
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [OPTION] /n FILE [FILE... --] cmd arg_1 arg_2 arg_3 ...
    echo.
    echo Options:
    echo    -f, --fullpath      substitute with full path
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    exit /b 0
