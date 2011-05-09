@echo off

    rem CMD/Shell Gate
    rem Require following variables have been defined:
    rem     PID
    rem     SHELL
    rem     TMP

    rem This program isn't reentrantable.

    if "%~1"=="" (
        echo syntax:
        echo    csg program arguments...
        exit /b 1
    )

    setlocal

    if not exist "%SHELL%.*"    call lapiota-init boot
    if "%PID%"==""              call lapiota-init boot
    if not exist "%TMP%\"       set TMP=%windir%\TEMP

    rem /tmp/ must be mounted from %TMP%
    rem Because it's too expansive to create a process, so don't use it
    rem     for %%f in ('cygpath -u %_CSGQUEUE%') do set CSGQUEUE=%%f
    set _CSGQUEUE=%TMP%\cmdqueue.csg\%PID%\%~n1
    set  CSGQUEUE=/tmp/cmdqueue.csg/%PID%/%~n1

    if not exist "%_CSGQUEUE%\." mkdir "%_CSGQUEUE%"

    rem Because it's too expansive to create a process, so don't use it
    rem     for /f %%i in ('cygpath -u "%~1"') do set _program=%%i
    set _program=%~1
    if "%~x1"==".bat" (
        rem cmd/sh/cmd
    ) else (
        set _program=%_program:\=/%
    )
    shift

    set _args=
:next_arg
    if "%~1"=="" (
        set _=%1.
        set _=!_:"=?!
        if !_!==. goto end_arg
    )
    set _args=%_args% %~1
    shift
    goto next_arg
:end_arg

    %SHELL% -c "%_program%"%_args%

    call export _CSGQUEUE
    %leave%

    for %%f in (%_CSGQUEUE%\*.bat) do (
        call %%f
        del %%f
    )
    call :rmdir_up "%_CSGQUEUE%\*"
    set _CSGQUEUE=
    exit /b 0

:rmdir_up
    setlocal
    set _dir=%~dp1
    if "%_dir:~-1%"=="\" set _dir=%_dir:~0,-1%
    rmdir "%_dir%" 2>nul
    if exist "%_dir%\" exit /b 0
    call :rmdir_up "%_dir%"
