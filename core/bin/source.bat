@echo off

    setlocal
    goto init

:start
    if not "%_file%"=="" goto exec

    ppid
    set _file=$SRC_%errorlevel%_%random%.bat
    set _istmp=1

    if %_verbose% leq 0 (
        echo @echo off >"%_file%"
    ) else (
        echo @echo on >"%_file%"
    )
    call fddump -a "%_file%"

:exec
    if %_verbose% geq 1 (
        echo executing %_file%
    )

    call "%_file%" %_rest%
   @echo off

    goto cleanup

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
    if "%~1"=="" goto prep3
    if not "%~1"=="-" set _file=%~1
    shift

:prep3
    if "%~1"=="" goto init_ok
    set _rest=%_rest%%~1
    shift
    goto prep3

:init_ok
    if %_verbose% geq 1 (set _ | tabify -b -d==)
    goto start

:version
    set _id=$Id$
    for /f "tokens=3-6" %%i in ("%_id%") do (
        set   _version=%%i
        set      _date=%%j
        set      _time=%%k
        set    _author=%%l
    )
    echo [TITLE] bat/source executer
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    goto end

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
    goto end

:cleanup
    if "%_istmp%"=="1" (
        if %_verbose% leq 0 del %_file%
    )

:end
    set  _verbose=
    set      _ret=
    set     _rest=
    set _startdir=
    set  _program=
    set     _file=
    set    _istmp=
