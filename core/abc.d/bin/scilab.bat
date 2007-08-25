@echo off

    setlocal
    call findabc scilab bin
    if errorlevel 1 exit /b
    start %_home%\bin\wscilex %*
