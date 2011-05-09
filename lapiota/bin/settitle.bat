@echo off

    setlocal

    title %~1
    shift

    set _args=
:next_arg
    if "%~1"=="" (
        set _=%1.
        set _=!_:"=?!
        if !_!==. goto end_arg
    )
    set _args=%_args%%1
    shift
    goto next_arg
:end_arg

    %_args%
    exit /b
