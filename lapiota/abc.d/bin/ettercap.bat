@echo off

    setlocal

    set name=ettercap
    set target=ettercap
    set defext=.exe

    call findabc ettercap

    set defexec=start "%_home%"
    if "%exec%"=="" if "%cd%"=="%USERPROFILE%" set exec=%defexec%

    if exist "%_home%\%target%%defext%" goto _implicit
    for /d %%i in (%_home%\*) do (
        set bindir=%%~nxi
        if exist "%_home%\%%~nxi\%target%%defext%" goto _bin
    )
    echo Can't find the target of %target% under %_home%.
    exit /b 1

:_implicit
    %exec% "%_home%\%target%" %*
    exit /b

:_bin
    %exec% "%_home%\%bindir%\%target%" %*
    exit /b