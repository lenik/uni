@echo off

rem export/clear
    if "%~1"=="-" (
        set _evl=
        shift
    )
    if "%~1"=="" exit /b

:add_var
    rem add vars to exported variables list
    if "%~1"=="" goto make
    set _evl=%_evl%%~1
    shift
    goto add_var

:make
    set _e1=
    for %%v in (%_evl%) do (
        if not "!_e1!"=="" set _e1=!_e1!^&
        set _e1=!_e1!set %%v=!%%v!
    )
    set leave=for %%s in ("%_e1%") do (endlocal^& %%~s)

:end
