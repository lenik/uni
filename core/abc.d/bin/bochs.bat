@echo off

    setlocal

    call findabc bochs .

    if "%~1"=="" (
        %_home%\bochs -f %LAPIOTA%\etc\bochs
        exit /b
    )

    %_home%\bochs %*
