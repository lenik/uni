@echo off

    setlocal

    call findabc bin
    set name=mencoder
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
    set _program=%_home%\%name%
    goto prep1

:_bin
    set _program=%_home%\%bindir%\%name%
    goto prep1

:prep1
    if "%~1"=="" (
        echo mencoder [OPTIONS]
        echo mencoder ~MAJORTYPE [PROFILE [MORE-OPTIONS]]
        exit /b 1
    )

    set _major=%~1
    if not "%_major:~0,1%"=="~" goto prep2
    set _major=%_major:~1%

    call findabc -r /etc/conf.d mencoder
    set _conf=%_home%
    for %%i in ("%_conf%\%_major%.*") do (
        set _majorinc=%%~nxi
        set _majorext=%%~xi
    )
    if "%_majorinc%"=="" (
        echo major type "%_major%" isn't supported.
        exit /b 1
    )

    set _args=-include "%_conf%\%_majorinc%"
    shift

    if not "%~1"=="" (
        set _profile=%~1
        set _args=%_args% -profile "%~1"
        shift
    )

    if exist "%~1" (
        set _origbase=%~dpn1
        set _prefix=[%_major%]
        if not "%_profile%"=="" set _prefix=[%_major%.%_profile%]
        set _args=%_args% -o "%~dp1!_prefix!%~n1%_majorext%"
    )

    if exist "%_origbase%.edl" (
        set _args=%_args% -edl "%_origbase%.edl"
    )

:prep2
    if "%~1"=="" goto exec
    set _args=%_args% %1
    shift
    goto prep2

:exec
    echo %exec% "%_program%" %_args%
    %exec% "%_program%" %_args%
    exit /b
