@echo off

    rem init:
    rem     00 - find lapiota home
    rem     01 - init basic vars: PATH  (exec which)
    rem     02 - find CYGWIN_ROOT       (exec findabc)
    rem     03 -!get PID                (exec ppid)
    rem     04 - find SHELL             (parse passwd)
    rem login:
    rem     10 - run profile            (using CSG)

:check_os
    if "%OS%"=="" goto check_cmd
    if "%OS%"=="Windows_NT" goto check_cmd
    echo The operating system isn't supported: %OS%
    exit /b 1

:check_cmd
    verify other 2>nul
    setlocal enableextensions
    if not errorlevel 1 goto level_0
    echo The cmd extensions isn't supported.
    echo Maybe your windows version is too old.
    exit /b 1

:check_more

:level_0
:find_lapiota_home
    endlocal

    rem if "%initlevel%" gtr "%~1" ( SKIPING... )
    if "%~1"=="init" (
        set initlevel=9
    ) else if "%~1"=="login" (
        set initlevel=19
    ) else (
        set /a initlevel=%~1 + 0
    )

    if exist "%LAPIOTA%\.LAPIOTA" shift & goto level_1
    set LAPIOTA=%~dp0
    if "%LAPIOTA_VER%"=="" set LAPIOTA_VER=0.0.1

  :loop_0
    set LAPIOTA=%LAPIOTA:~0,-1%
    if "%LAPIOTA%"=="" goto err_0
    if "%LAPIOTA:~-1%"=="\" set LAPIOTA=%LAPIOTA:~0,-1%
    if "%LAPIOTA%"=="" goto err_0
    if exist "%LAPIOTA%\.LAPIOTA" goto level_1
    goto loop_0
  :err_0
    echo Can't locate the root directory of lapiota.
    exit /b 1

:level_1
:init_basic_vars
    if %initlevel% lss 1 goto done

    rem either no `which` or no `lapiota-init`
    which lapiota-init.bat >nul 2>nul
    if not errorlevel 1 goto level_2

    set PATH=%LAPIOTA%\bin;%PATH%

:level_2
:find_cygwin_root
    if %initlevel% lss 2 goto done

    if exist "%CYGWIN_ROOT%\.CYGWIN" goto add_cygwin_path
    call findabc cygwin
    if errorlevel 1 (
        echo Can't find cygwin
        exit /b 2
    )
    set CYGWIN_ROOT=%_HOME%

:add_cygwin_path
    which cygpath >nul 2>nul
    if not errorlevel 1 goto level_3
    set PATH=%CYGWIN_ROOT%\bin;%PATH%

:level_3
    if %initlevel% lss 3 goto done
:level_4
    if %initlevel% lss 4 goto done

:level_5
:get_pid
    if %initlevel% lss 5 goto done
    ppid
    set PID=%ERRORLEVEL%

:level_6
:find_shell
    if %initlevel% lss 6 goto done

    if exist "%SHELL%" goto level_7
    rem The lapiota-init program must have mounted the root(/) filesystem.
    set SHELL=/bin/bash
    if "%USERNAME%"=="" set USERNAME=someone
    for /f "delims=: tokens=1-7" %%i in (%CYGWIN_ROOT%\etc\passwd) do (
        rem i=name j=pwd k:uid l:gid m:fullname n:home o:shell
        if "%%i"=="%USERNAME%" (
            set SHELL=%%o
        )
    )
    set SHELL=%CYGWIN_ROOT%%SHELL:/=\%
    set SHELLW=%SHELL%w

:level_7
    if %initlevel% lss 7 goto done
:level_8
    if %initlevel% lss 8 goto done
:level_9
    if %initlevel% lss 9 goto done

:level_10
:run_profile
    if %initlevel% lss 10 goto done

    REM if not "%_profile%"=="1" goto level_11
    REM call csg %LAPIOTA%\etc\profile
    if exist "%USERPROFILE%\.homepath" (
        set /p HOME= <"%USERPROFILE%\.homepath"
    )
    if "%CD%"=="%USERPROFILE%" cd /d "%HOME%"
    if not "%~1"=="" cd /d "%~1"

:level_11
    if %initlevel% lss 11 goto done
:level_20
    if %initlevel% lss 20 goto done

:done
    set _err=0

:error
    if %initlevel% geq 10 (
        echo Welcome LAPIOTA %LAPIOTA_VER%
        echo This program is distributed under GPL license.
    )
    set initlevel=
    prompt $S       $+$P$_%USERNAME%$G$S

    rem BUGFIX to cmd: set errorlevel doesn't affect the exit code
    if "%_err%"=="" set _err=%ERRORLEVEL%
    exit /b %_err%
