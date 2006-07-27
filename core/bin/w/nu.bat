@echo off

    set _REGISTRY=%USERPROFILE%\Registry

    if not exist "%_REGISTRY%\*" (
        echo Initializing Registry...
        md "%_REGISTRY%"
    )

    if not "%1"=="" goto host

    set _I=0
    for %%i in ("%_REGISTRY%\*.auto") do (
        set /A _I=_I+1
        echo Auto connecting [!_I!] %%~dpni
        call :host %%i
    )

    if %_I%==0 echo No host to be auto connected.
    set _I=

    goto end

:host
    set _HOST=%1
    set _USER=administrator
    if not "%3"=="" set _USER=%3
    set _PASSWORD=%2

    if "%_USER%"=="" goto host_restore
    if "%_PASSWORD%"=="" goto host_restore

    echo Reset login data
    echo %_USER%         > "%_REGISTRY%\%_HOST%.ini"
    echo %_PASSWORD%    >> "%_REGISTRY%\%_HOST%.ini"
    goto host_set

:host_restore
    set _LOGINFILE=%_REGISTRY%\%_HOST%.ini
    if not exist "%_LOGINFILE%" (
        echo Login data for host %_HOST% isn't existed.
        echo Please make the initial connection by run command:
        echo %0 HostName Password UserName
        goto host_end
    )

    echo Restore login data from host settings...
    set _LINE=0
    for /f "delims=| " %%i in ('type "%_LOGINFILE%"') do (
        set /A _LINE=_LINE+1
        if !_LINE!==1 set _USER=%%i
        if !_LINE!==2 set _PASSWORD=%%i
    )
    set _LINE=

:host_set
    echo Connecting %_HOST%...

    rem echo net use \\%_HOST% /user:%_USER% "%_PASSWORD%"
    net use \\%_HOST% /user:%_USER% "%_PASSWORD%" >nul 2>nul

    if errorlevel 2 (
        echo Failed to connect to %_HOST%.
    ) else (
        echo Succeeded to connect to %_HOST%.
    )
    goto host_end

:end
    set _REGISTRY=

:host_end
    set _HOST=
    set _USER=
    set _PASSWORD=
    set _LOGINFILE=
