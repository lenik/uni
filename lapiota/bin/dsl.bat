@echo off

    rem dirtix shell login

    setlocal

    if "%~1"=="" (
        echo dsl ^<remote-host^> [^<remote-port=1023^>]
        goto end
    )

    set host=%~1
    set port=1023
    if not "%~2"=="" set port=%~2

    send "%host%" "%port%"

:end
