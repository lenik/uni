@echo off

    setlocal

    set name=%~n0
    call findabc %name%

    if exist "%_home%\%name%.exe" goto _implicit
    for /d %%i in (%_home%\*) do (
        set bindir=%%~nxi
        if exist "%_home%\%%~nxi\%name%.exe" goto _bin
    )
    echo Can't find the target of %name% under %_home%.
    exit /b 1

:_implicit
    "%_home%\%name%" %*
    exit /b

:_bin
    "%_home%\%bindir%\%name%" %*
    exit /b
