@echo off

    setlocal enabledelayedexpansion
    set _strict=1
    goto init

:start

:nextroot
    if "%ABCPATH%"=="" goto fail
    for /f "delims=; tokens=1*" %%i in ("%ABCPATH%") do (
        set _xdir=%%i
        set ABCPATH=%%j
    )

    rem recursive add virtual-groups
    for /d %%g in ("%_xdir%\*.d" "%_xdir%\[*]") do (
        if "!ABCPATH!"=="" (
            set ABCPATH=%%g
        ) else (
            set ABCPATH=%%g;!ABCPATH!
        )
    )

        set _plen=0
    :st_dirs
        rem echo find with prefix: %_xdir%
        if exist "%_xdir%\%_name%*" (
            for /d %%i in ("%_xdir%\%_name%*") do (
                set _home=%%i
                if not "%_last%"=="1" goto leave
            )
            if not "!_home!"=="" goto leave
        )
      :st_extend
        set /a _plen = _plen + 1
        set _prefix=!_name:~0,%_plen%!
        if "%_prefix%"=="%_name%" goto nextroot
        if not exist "%_xdir%\%_prefix%\*" goto st_extend
        set _xdir=%_xdir%\%_prefix%
        goto st_dirs

:leave
    set _=%_home%
    if "%_slash%"=="1" set _=%_:\=/%
    if not "%_chdir%"=="" set _=%_%::%_chdir%
    if "%_print%"=="1" printf %%s "%_%"
    call export - _
    %leave%

    for /f "delims=| tokens=1,2" %%i in ("%_:::=|%") do (
        set _home=%%i
        set _chdir=%%j
    )
    set _=
    if not "%_chdir%"=="" cd /d "%_home%/%_chdir%"
:end
    exit /b 0

:fail
    echo failed to find %_name%
    exit /b 1

:init
    set   __DIR__=%~dp0
    set  __FILE__=%~nx0
    set  _verbose=0
    set      _ret=
    set     _home=

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
    ) else if "%~1"=="-l" (
        set _last=1
    ) else if "%~1"=="--last" (
        set _last=1
    ) else if "%~1"=="-p" (
        set _print=1
    ) else if "%~1"=="--print" (
        set _print=1
    ) else if "%~1"=="-w" (
        set _slash=0
    ) else if "%~1"=="--win32" (
        set _slash=0
    ) else if "%~1"=="-u" (
        set _slash=1
    ) else if "%~1"=="--unix" (
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
    if "%_:~-1%"=="/" set _=%_%.
    for /f "delims=/\ tokens=1*" %%i in ("%_%") do (
        set _name=%%i
        set _chdir=%%j
    )
    shift
    if "%~1"=="" goto findlams

:prep3
    if "%~1"=="" goto init_ok
    if "%ABCPATH%"=="" (
        set ABCPATH=%~1
    ) else (
        set ABCPATH=%ABCPATH%;%~1
    )
    goto prep3

:findlams
    rem LAPIOTA[\bin\]
    if "%LAM_ROOT%"=="" (
        set ABCPATH=!__DIR__:~,-5!
        goto init_ok
    )
    for /d %%d in ("%LAM_ROOT%\*") do (
        if "!ABCPATH!"=="" (
            set ABCPATH=%%d
        ) else (
            set ABCPATH=!ABCPATH!;%%d
        )
    )

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
    echo    %__FILE__% [OPTION] abc-package  SEARCHPATH
    echo    %__FILE__% [OPTION] abc-package/ SEARCHPATH
    echo.
    echo Options:
    echo    -l, --last          get last/most-recent version
    echo    -p, --print         print home-directory to STDOUT
    echo    -u, --unix          return unix/ path
    echo    -w, --win32         return win32\ path
    echo    -q                  repeat to get less info
    echo    -v                  repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    exit /b 0
