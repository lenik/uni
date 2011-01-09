@echo off

    setlocal

    call findabc bin
    set name=%~n0
    call findabc %name%
    if "%exec%"=="" (
        REM if not "%cd%"=="%_home%" set exec=start "%_home%"
        if "%cd%"=="%USERPROFILE%" set exec=start "%_home%"
    )

    if exist "%_home%\%name%.exe" goto _implicit
    for /d %%i in (%_home%\*) do (
        set bindir=%%~nxi
        if exist "%_home%\%%~nxi\%name%.exe" goto _bin
    )
    echo Can't find the target of %name% under %_home%.
    exit /b 1

:_implicit
    %exec% "%_home%\%name%" %*
    exit /b

:_bin
    %exec% "%_home%\%bindir%\%name%" %*
    exit /b
