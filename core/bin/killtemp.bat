@echo off

    setlocal

    if "%~1"=="-p" (
        set pause=1
        shift
    )

    if not "%~1"=="" goto kill_list

:kill_all
    rd /s /q %TEMP%
    goto end

:kill_list
    if "%~1"=="" goto end

    if exist "%TEMP%\%~1\*" (
        echo kill directory: "%TEMP%\%~1"
        rd /s /q "%TEMP%\%~1"
    ) else if exist "%TEMP%\%~1" (
        echo kill file^(s^): "%TEMP%\%~1"
        del /f /q "%TEMP%\%~1"
    ) else (
        echo file^(s^) not exist: %~1
    )

    shift
    goto kill_list

:end
    if "%pause%"=="1" pause
