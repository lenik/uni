@echo off

    setlocal enabledelayedexpansion
    set _strict=1
    set _root=/abc.d
    goto init

:start
    set _lev=0
    if not "%_root%"=="" set _root=%_root:/=\%
    set _prefix=%LAPIOTA%%_root%
:st_loop
    rem echo find with prefix: %_prefix%
    if exist %_prefix%\%_name%* goto found
:st_next
    set /a _lev = _lev + 1
    set _st=!_name:~0,%_lev%!
    if "%_st%"=="%_name%" (
        echo failed to find %_name%
        exit /b 1
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
    set _=%_home%
    if "%_slash%"=="1" set _=%_:\=/%
    if not "%_chdir%"=="" set _=%_%::%_chdir%
    if "%_print%"=="1" printf %%s %_%
    call export _
    %leave%

    set _home=
    set _chdir=
    for %%i in (%_:::= %) do (
        if "!_home!"=="" (
            set _home=%%i
        ) else (
            set _chdir=%%i
        )
    )
    set _=
    if not "%_chdir%"=="" cd /d "%_home%/%_chdir%"

:add_path
    if "%~1"=="" goto end
    if "%~1"=="." (
        set PATH=%_home%;%PATH%
    ) else (
        rem %_slash% is ignored here.
        set PATH=%_home%\%~1;%PATH%
    )
    shift
    goto add_path

:end
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
    ) else if "%~1"=="-v" (
        set /a _verbose = _verbose + 1
    ) else if "%~1"=="-r" (
        set _root=%~2
        shift
    ) else if "%~1"=="--root" (
        set _root=%~2
        shift
    ) else if "%~1"=="-p" (
        set _print=1
    ) else if "%~1"=="--print" (
        set _print=1
    ) else if "%~1"=="-s" (
        set _slash=1
    ) else if "%~1"=="--slash" (
        set _slash=1
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
    set _=%~1
    if "%_:~0,1%"=="/" set _root=
    if "%_:~-1%"=="/" set _=%_%.
    set _name=
    set _chdir=
    for %%i in (%_:/= %) do (
        if "!_name!"=="" (
            set _name=%%i
        ) else if "!_chdir!"=="" (
            set _chdir=%%i
        ) else (
            set _chdir=%_chdir%/%%i
        )
    )
    shift

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
    echo [findabc] Find out directory/prefix of an installed abc-package
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [OPTION] abc-package  [DIR... add to PATH]
    echo    %_program% [OPTION] abc-package/ [DIR... add to PATH]
    echo.
    echo Options:
    echo    -r, --root DIR      start directory to find, default /abc.d
    echo    -p, --print         print home-directory to STDOUT
    echo    -s, --slash         use slash(/) instead of default back-slash(\)
    echo    -q                  repeat to get less info
    echo    -v                  repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    exit /b 0
