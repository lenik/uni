@echo off

:check_os
    if "%OS%"=="" goto check_cmd
    if "%OS%"=="Windows_NT" goto check_cmd
    echo The operating system isn't supported: %OS%
    exit /b

:check_cmd
    verify other 2>nul
    setlocal enableextensions
    if not errorlevel 1 goto level_0
    echo The cmd extensions isn't supported.
    echo Maybe your windows version is too old.
    exit /b

:check_more

:level_0
:find_lapiota_home
    endlocal

    rem if "%initlevel%" gtr "%~1" ( SKIPING... )
    if "%~1"=="start" (
        set initlevel=9
    ) else if "%~1"=="logo" (
        set initlevel=9
    )

    if "%initlevel%"=="" set initlevel=%~1
    set /a initlevel=initlevel+0

    if exist "%LAPIOTA%\__LAPIOTA__" shift & goto level_1
    set LAPIOTA=%~dp0

  :loop_0
    set LAPIOTA=%LAPIOTA:~0,-1%
    if "%LAPIOTA:~-1%"=="\" set LAPIOTA=%LAPIOTA:~0,-1%
    if "%LAPIOTA%"=="" goto err_0
    if exist "%LAPIOTA%\__LAPIOTA__" goto level_1
    goto loop_0
  :err_0
    echo Can't locate the root directory of lapiota.
    exit /b 1

:level_1
:init_basic_vars
    if %initlevel% lss 1 exit /b 0

    which lapiota-init >nul 2>nul
    if not errorlevel 1 goto level_2

    set PATH=%LAPIOTA%\bin\xt\bin\overwrite;%LAPIOTA%\bin\xt\bin;%LAPIOTA%\bin\xt\sbin\overwrite;%LAPIOTA%\bin\xt\sbin;%LAPIOTA%\bin;%LAPIOTA%\sbin;%LAPIOTA%\lib;%LAPIOTA%\local\bin;%PATH%;%LAPIOTA%\usr\bin;%LAPIOTA%\local\lib

:level_2
:init_auto_env
    if %initlevel% lss 2 exit /b 0

    if exist %LAPIOTA%\.env.as (
        call as-env %LAPIOTA%\.env.as
    )

:level_3
:init_cygwin
    if %initlevel% lss 3 exit /b 0

    if not exist b:\ subst b: "%CYGWIN_HOME%"
    if "%USERNAME%"=="" set USERNAME=someone
    set HOME=%LAPIOTA%\home\%USERNAME%
    if not exist "%HOME%" mkdir "%HOME%"
    if "%CD%"=="%USERPROFILE%" (
        if not "%INITDIR%"=="" (
            cd /d "%INITDIR%"
        ) else (
            cd /d "%HOME%"
        )
    )

:level_9
    if %initlevel% lss 9 exit /b 0

    if "%~0"=="logo" (
        reg delete "HKLM\Software\Microsoft\Command Processor" /v AutoRun /f >nul
    )

:level_10
    if %initlevel% lss 10 exit /b 0

    set _err=0
    for %%f in (%LAPIOTA%\etc\profile.d\*) do (
        rem echo loading %%f
        set _f=%%f
        set _level=%%~nf
        set _level=!_level:~0,2!
        set _mesg=
        if %initlevel% geq !_level! (
            set /p _mesg=<%%f
            if not "!_mesg!"=="" printf ">> %%-60s" "!_mesg:~5!"
            call !_f!
            if not "!_mesg!"=="" (
                if errorlevel 1 (
                    set _mesg=[ FAILED ]
                    set /a _err = _err + 1
                ) else (
                    set _mesg=[   OK   ]
                )
                echo !_mesg!
            )
        )
    )
    set _f=
    set _level=
    set _mesg=
    err %_err%
    set _err=
