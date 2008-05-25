@echo off
    setlocal

    set name=m4_ifelse(NAME, , %~n0, NAME)
    set defext=m4_ifelse(DEFEXT, , .exe, DEFEXT)

    call findabc %name%
    m4_ifelse(EXEC, , , if "%exec%"=="" set exec=EXEC)
    set defexec=start "%_home%" EXEC
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
