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
    set _prog=%~dp0

    set _level_start=10
    set _=!_level_%~1!
    set _level_start=

    if "%_%"=="" set _=%~1
    set /a _=_+0
    shift

    if exist "%LAPIOTA%\__LAPIOTA__" goto level_1
    set LAPIOTA=%_prog%
    set _prog=

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
    set /a _=_-1& if %_% leq 0 exit /b 0

    which lapiota-init >nul 2>nul
    if not errorlevel 1 goto level_2

    set PATH=%LAPIOTA%\bin\xt\bin\overwrite;%LAPIOTA%\bin\xt\bin;%LAPIOTA%\bin\xt\sbin\overwrite;%LAPIOTA%\bin\xt\sbin;%LAPIOTA%\bin;%LAPIOTA%\sbin;%LAPIOTA%\local\bin;%PATH%;%LAPIOTA%\usr\bin

:level_2
:init_auto_env
    set /a _=_-1& if %_% leq 0 exit /b 0

    if exist %LAPIOTA%\.env.as (
        call as-env %LAPIOTA%\.env.as
    )

:level_3
:init_cygwin
    set /a _=_-1& if %_% leq 0 exit /b 0

    if not exist b:\ subst b: "%CYGWIN_HOME%"
    if "%USERNAME%"=="" set USERNAME=someone
    set HOME=%LAPIOTA%\home\%USERNAME%
    if not exist "%HOME%" mkdir "%HOME%"
    cd %HOME%
    if exist "a:\.*" (
        a:
    ) else if not exist "a:\" (
        subst a: "%HOME%"
        a:
    )

:level_4
    set /a _=_-1& if %_% leq 0 exit /b 0

:level_5
    set /a _=_-1& if %_% leq 0 exit /b 0

:level_6
    set /a _=_-1& if %_% leq 0 exit /b 0

:level_7
    set /a _=_-1& if %_% leq 0 exit /b 0

:level_8
    set /a _=_-1& if %_% leq 0 exit /b 0

:level_9
    set /a _=_-1& if %_% leq 0 exit /b 0

:level_10
:user_profile
    set /a _=_-1& if %_% leq 0 exit /b 0
