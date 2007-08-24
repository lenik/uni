@echo off

    setlocal
    call findabc scilab bin
    if errorlevel 1 exit /b
    %_home%\bin\scilab %*
