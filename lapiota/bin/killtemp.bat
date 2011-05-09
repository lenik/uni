@echo off

    setlocal

    if "%~1"=="-p" (
        set pause=1
        shift
    )

    if "%~1"=="-" (
        set prefix=1
        shift
    )

    if not "%~1"=="" goto kill_list

:kill_all
    rd /s /q %TEMP%
    goto end

:kill_list
    if "%~1"=="" goto end

    set _n=%TEMP%\%~1
    if "%prefix%"=="1" set _n=%_n%*

    if exist "%TEMP%\%~1\*" (
        echo kill directory: "%TEMP%\%~1"
        rd /s /q "%TEMP%\%~1"
    ) else if exist "%_n%" (
        echo kill file^(s^): "%_n%"
        del /f /q "%_n%"
    ) else (
        echo file^(s^) not exist: %~1
    )

    shift
    goto kill_list

:end
    if "%pause%"=="1" pause
