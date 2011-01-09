@echo off

    rem Generated by Mkbat 0.3.94
    rem Template: Mkbat.batTempl

:check_os
    if "%OS%"=="" goto check_cmd
    if "%OS%"=="Windows_NT" goto check_cmd
    echo The operating system isn't supported: %OS%
    exit /b 1

:check_cmd
    verify other 2>nul
    setlocal enableextensions
    if not errorlevel 1 goto check_more
    echo The cmd extensions isn't supported.
    echo Maybe your windows version is too old.
    exit /b 1

:check_more
    if "%_javabat%"=="" set _javabat=0
    goto init

:start
    if %_verbose% geq 1 set CLASSPATH

    if "%_start%"=="" (
        set _start=
        if %_verbose% geq 1 set _start=startc
    )

    if "%_start%"=="" (
        set _start=startc
        if /i "%_shell%"=="cmdw" set _start=startw
    )
    goto %_start%

:startc
    if "%JAVA%"=="" set JAVA=java
    if %_verbose% geq 1 (
        echo %JAVA% %_javaopts% %_nam% %_rest%
    )
    %JAVA% %_javaopts% %_nam% %_rest%
    exit /b

:startw
    if "%JAVAW%"=="" set JAVAW=javaw
    if %_verbose% geq 1 (
        echo %JAVAW% %_javaopts% %_nam% %_rest%
    )
    %JAVAW% %_javaopts% %_nam% %_rest%
    exit /b

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set _startdir=%~dp0
    set  _program=%~dpnx0
    set    _start=

    set      _nam=net.bodz.lapiota.crypt.HashesTest
    set     _namf=%_nam:.=\%
    set      _ext=
    set _javaopts=%JAVA_OPTS%

    if not "%JAVA_HOME%"=="" set PATH=%JAVA_HOME%\bin;%PATH%

    if not "%_shell%"=="" goto initcp
    echo %CMDCMDLINE% >%TEMP%\.ccl
    for /f %%i in (%TEMP%\.ccl) do (
        set _shell=%%i
        goto parseshell
    )
    :parseshell
    for %%f in (%_shell%.*) do set _shell=%%~nf

:initcp
    for %%f in (loadlib.bat) do (
        if exist "%%~dp$PATH:f" (
            set has_%%~nf=1
        )
    )

    set _morecp=
    call :load "bodz_lapiota" "net.bodz.lapiota.jar"

    goto initcp2

:loadlibs
    set _libs=%~1
    call loadlib %_libs%
    exit /b

:load
    set _libname=%~1
    set _libfile=!lib_%_libname%!
    if "%_libfile%"=="" (
        for %%d in (. .. ..\lib "%JAVA_LIB%" "%JAVA_HOME%\lib") do (
            if exist "%%~d\%~2" (
                set _libfile=%%~dpnxd\%~2
                goto got
            )
        )
        if "%has_loadlib%"=="1" (
            call :loadlibs "%JAVA_LIB%"
            set has_loadlib=
            goto load
        )
        echo can't find library: %_libname% ^(%~2^)
        exit /b 1
    )
    :got
    set _morecp=%_morecp%;%_libfile%
    exit /b

:initcp2
    if not "%_morecp%"=="" set _morecp=%_morecp:~1%
    if not "%_morecp%"=="" set CLASSPATH=%_morecp%;%CLASSPATH%

    if "%_javabat%"=="0" (
        set _rest=%*
        goto start
    )

:prep1
    if "%~1"==""            goto prep2
    if "%~1"=="--jb-ver"    goto version
    if "%~1"=="--jb-help"   goto help
    if "%~1"=="-Jq" (
        set /a _verbose = _verbose - 1
    ) else if "%~1"=="--jb-quiet" (
        set /a _verbose = _verbose - 1
    ) else if "%~1"=="-Jv" (
        set /a _verbose = _verbose + 1
    ) else if "%~1"=="--jb-verbose" (
        set /a _verbose = _verbose + 1
    ) else if "%~1"=="-Jw" (
        set _start=startw
    ) else if "%~1"=="--jb-win" (
        set _start=startw
    ) else (
        goto prep2
    )
    shift
    goto prep1

:prep2

:prep3
    if "%~1"=="" (
        set _=%1.
        set _=!_:"=?!
        if !_!==. goto init_ok
    )
    set _rest=%_rest%%1
    shift
    goto prep3

:init_ok
    if %_verbose% geq 1 (set _ | tabify -b -d==)
    goto start

:version
    set _id=$Id$
    for /f "tokens=3-6" %%i in ("%_id%") do (
        set   _version=%%i
        set      _date=%%j
        set      _time=%%k
        set    _author=%%l
    )
    echo [javabat] java program launcher
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [JB-OPTION] ARGUMENTS...
    echo.
    echo Options:
    echo    -Jw,--jb-win        start with javaw.exe
    echo    -Jq,--jb-quiet      repeat to get less info
    echo    -Jv,--jb-verbose    repeat to get more info
    echo        --jb-ver        show version info
    echo    -Jh,--jb-help       show this help page
    exit /b 0
