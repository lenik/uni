@echo off

:check_os
	if "%OS%"=="" goto check_cmd
	if "%OS%"=="Windows_NT" goto check_cmd
	echo The operating system isn't supported: %OS%
	goto end

:check_cmd
	verify other 2>nul
	setlocal enableextensions
	if not errorlevel 1 goto level_0
	echo The cmd extensions isn't supported.
	echo Maybe your windows version is too old.
	goto end

:check_more

:level_0
:find_lapiota_home
	endlocal
	set _prog=%~dp0
	set _level=%~1
	set /a _level = _level + 1
	shift

	if exist "%LAPIOTA%\__LAPIOTA__" goto level_1
	set LAPIOTA=%_prog%

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
	set /a _level = _level - 1
	if %_level% leq 0 exit /b 0

	which lapiota-init >nul 2>nul
	if not errorlevel 1 goto level_2

	set PATH=%LAPIOTA%\bin\xt\bin\overwrite;%LAPIOTA%\bin\xt\bin;%LAPIOTA%\bin\xt\sbin\overwrite;%LAPIOTA%\bin\xt\sbin;%LAPIOTA%\bin;%LAPIOTA%\sbin;%PATH%;%LAPIOTA%\usr\bin;%LAPIOTA%\local\bin

:level_2
:init_auto_env
	set /a _level = _level - 1
	if %_level% leq 0 exit /b 0

	if exist %LAPIOTA%\.env.as (
		call as-env %LAPIOTA%\.env.as
	)

:level_3
	set /a _level = _level - 1
	if %_level% leq 0 exit /b 0

:end
	set _level=
