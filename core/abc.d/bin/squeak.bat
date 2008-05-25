@echo off
    setlocal

    set name=squeak
    set defext=.exe

    call findabc %name%

    set defexec=start "%_home%"
    if "%exec%"=="" if "%cd%"=="%USERPROFILE%" set exec=%defexec%

    if exist "%_home%\%name%%defext%" goto _implicit
    for /d %%i in (%_home%\*) do (
        set bindir=%%~nxi
        if exist "%_home%\%%~nxi\%name%%defext%" goto _bin
    )
    echo Can't find the target of %name% under %_home%.
    exit /b 1

:_implicit
    %exec% "%_home%\%name%" %*
    exit /b

:_bin
    %exec% "%_home%\%bindir%\%name%" %*
    exit /b
