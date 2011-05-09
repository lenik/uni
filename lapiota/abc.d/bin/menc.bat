@echo off

    setlocal

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
    if "%~1"=="-q" (
        set _quiet=1
        shift
    )
    if "%~1"=="" (
        echo Syntax:
        echo     %0 [OPTIONS]
        echo     %0 [-q] ~MAJORTYPE[.PROFILE or .BITRATE] [MORE-OPTIONS]]
        echo Example:
        echo     %0 -q ~mpeg4.8000 clip.avi
        exit /b 1
    )

    set _major=%~1
    if not "%_major:~0,1%"=="~" goto prep2
    for %%p in (%_major:~1%) do (
        set _major=%%~np
        set _profile=%%~xp
    )
    if not "%_profile%"=="" set _profile=%_profile:~1%

    call findabc mencoder %LAPIOTA%\etc\conf.d
    set _conf=%_home%
    for %%i in ("%_conf%\%_major%.*") do (
        set _majorinc=%%~nxi
        set _majorext=%%~xi
    )
    if "%_majorinc%"=="" (
        echo major type "%_major%" isn't supported.
        exit /b 1
    )

    for /f "delims== tokens=1,2* usebackq" %%i in ("%_conf%\%_majorinc%") do (
        if "%%i"=="#" (
            set %%j=%%k
        ) else (
            goto mj_parsed
        )
    )
    :mj_parsed
    set _args=-include "%_conf%\%_majorinc%"
    shift

    rem PROFILE := SECTION-NAME
    rem PROFILE := PROFILE BITRATE
    if not "%_profile%"=="" (
        set /a _ = _profile + 0
        if "%_profile%" equ "!_!" (
            set _args=%_args% -%BITRATE%=%_profile%
        ) else (
            set _args=%_args% -profile "%_profile%"
        )
    )

    if exist "%~1" (
        set _origbase=%~dpn1
        set _origtime=%~t1
        set _prefix=[%_major%]
        if not "%_profile%"=="" set _prefix=[%_major%.%_profile%]
        set _out=%~dp1!_prefix!%~n1%_majorext%
        set _args=%_args% -o "!_out!"
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
    if "%_quiet%"=="1" (
        %exec% "%_program%" %_args% 2>&1 | grep %% | pc -e
    ) else (
        %exec% "%_program%" %_args%
    )

    if exist "%_out%" call :touch "%_out%" "%_origtime%"

    exit /b

:touch
    rem %2: YYYY-MM-DD hh:mm
    set _t=%~2
    set _tYY=%_t:~2,2%
    set _tMMDD=%_t:~5,2%%_t:~8,2%
    set _thhmm=%_t:~11,2%%_t:~14,2%
    rem [[CC]YY]MMDDhhmm[.ss]
    touch -t "%_tYY%%_tMMDD%%_thhmm%" "%~1"
    exit /b
