@echo off

    setlocal
    set _strict=1
    goto init

:start

    if not exist "%_file%" (
        rem find the file in path
        call :findfile "%_file%"
        if not exist "!_!" (
            rem find the file.pl in path
                call :findfile "%_file%.pl"
                if not exist "!_!" (
                    echo can't find file %_file%.
                    goto end
                )
            )
        )
        set _file=!_!
    )

    if %_verbose% geq 1 echo Exec: perl "%_file%" %_rest%
    perl -Mcmt::moduse "%_file%" %_rest%

    goto cleanup

:findfile
    if %_verbose% geq 1 echo find %1 in PATH...
    set _=%~dpnx$PATH:1
    goto end

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set _startdir=%~dp0
    set  _program=%~dpnx0

:prep1
    if "%~1"==""            goto init_ok
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
    if "%~1"=="" (
        echo no perl-file specified.
        goto end
    )
    set _file=%~1
    shift

:prep3
    if "%~1"=="" goto init_ok
    set _rest=%_rest%%~1
    shift
    goto prep3

:init_ok
    if %_verbose% geq 1 (
        echo _startdir=%_startdir%
        echo  _program=%_program%
        echo     _file=%_file%
        echo     _rest=%_rest%
    )
    goto start

:version
    set _id=$Id: moduse.bat,v 1.1 2007-07-21 01:14:03 lenik Exp $
    for /f "tokens=3-6" %%i in ("%_id%") do (
        set   _version=%%i
        set      _date=%%j
        set      _time=%%k
        set    _author=%%l
    )
    echo [moduse] Get loading info about used libraries ^(for Perl only^)
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    goto end

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% ^<options^> perl-file [arguments...]
    echo.
    echo Options:
    echo    --quiet             (q)
    echo    --verbose           (v, repeat to get more info)
    echo    --version
    echo    --help              (h)
    goto end

:cleanup

:end
