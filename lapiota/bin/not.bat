@echo off

if "%1"=="" goto help

set _fn=%~1
set _fbase=%~nx1
set _fname=%~n1
set _fext=%~x1

if "%_fext%"==".-NoT-" goto enable
if exist "%_fn%" goto disable
if exist "%_fn%.-NoT-" (
    set _fn=%_fn%.-NoT-
    set _fname=%_fname%%_fext%
    set _fext=.-NoT-
    goto enable
    )
goto notexist

:notexist
    echo File doesn't exist: %_fn%
    goto end

:disable
    ren "%_fn%" "%_fbase%.-NoT-"

    echo %_fbase% Disabled.
    goto end

:enable
    rem assert "%_fext%"==".-NoT-"

    if not exist "%_fn%" (
        echo Disabled file %_fn% not existed.
        goto end
        )

    ren "%_fn%" "%_fname%"

    echo %_fname% Enabled.
    goto end

:help
    echo [NOT] Enable/Disable File By Tag File Name
    echo Syntax:
    echo     NOT filename
    goto end

:end
    set _fn=
    set _fbase=
    set _fname=
    set _fext=
