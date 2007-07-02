@echo off

    setlocal

    if "%~1"=="-t" (
        set timeonly=1
        shift
    )

    if "%~1"=="-d" (
        set dateonly=1
        shift
    )

    if "%~1"=="" (
        err 0
        goto end
    )

    if "%~t1"=="" (
        echo Error get time of file %1
        goto end
    )

    set        t=%~t1
    set     year=%t:~0,4%
    set    month=%t:~5,2%
    set      day=%t:~8,2%
    set     hour=%t:~-5,2%
    set   minute=%t:~-2%
    REM echo %t%
    REM echo %year%-%month%-%day% %hour%:%minute%

    set /a vdate = (year - 1900) * 372 + month * 31 + day
    set /a vtime = hour * 60 + minute
    if "%dateonly%"=="1" (
        set /a s = vdate
    ) else if "%timeonly%"=="1" (
        set /a s = vtime
    ) else (
        set /a s = vdate * 1440 + vtime
    )
    err %s%

:end
