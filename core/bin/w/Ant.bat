@echo off

    rem Generated by GenerateBatches 0.784  Last updated: 2008-01-15

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
    set _strict=1
    goto init

:start

    REM _____________________________________________

    if %_verbose% geq 1 set CLASSPATH

    if "%_start%"=="" (
        if %_verbose% geq 1 set _start=startc
    )

    if "%_start%"=="" (
        set _start=startc
        if "%_shell%"=="cmdw" set _start=startw
        if "%_shell%"=="CMDW" set _start=startw
    )
    goto %_start%

:startc
    if "%JAVA%"=="" set JAVA=java
    if %_verbose% geq 1 (
        echo "%JAVA%" %_javaopts% %_nam% %_rest%
    )
    "%JAVA%" %_javaopts% %_nam% %_rest%
    exit /b

:startw
    if "%JAVAW%"=="" set JAVAW=javaw
    if %_verbose% geq 1 (
        "%JAVAW%" %_javaopts% %_nam% %_rest%
    )
    "%JAVAW%" %_javaopts% %_nam% %_rest%
    exit /b

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set _startdir=%~dp0
    set  _program=%~dpnx0

    set      _nam=net.bodz.lapiota.exttools.Ant
    set     _namf=%_nam:.=\%
    set      _ext=
    set _javaopts=-ea -DGenerateBatches=1

    if not "%JAVA_HOME%"=="" set PATH=%JAVA_HOME%\bin;%PATH%

    if "%_shell%"=="" (
        rem CMDCMDLINE doesn't work, this is a Windows' BUG.
        for %%a in (%COMSPEC%) do (
            for %%c in (%%a) do (
                set _shell=%%~nc
                goto initcp
            )
        )
    )
    set _shell=cmd

:initcp
    set _libver=
    for %%f in (setcp.bat) do set _dir%%~nf=%%~dp$PATH:f
    if exist "%_dirsetcp%setcp.bat" (
        call "%_dirsetcp%setcp.bat"
        set _libver=1
    )

    set _morecp=
    if not "%_dir%"=="" (
        set _morecp=%_morecp%;%_dir:\=/%
    )
    set _morecp=%_morecp%;%JAVA_LIB%\ant.jar
    set _morecp=%_morecp%;%JAVA_LIB%\ant-launcher.jar


    if not "%_libver%"=="1" goto morecp_f

    goto initcp2
  :morecp_f


:initcp2
    if not "%_morecp%"=="" set _morecp=%_morecp:~1%
    if not "%_morecp%"=="" set CLASSPATH=%_morecp%;%CLASSPATH%

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
    set _id=$Id: .bat 784 2008-01-15 10:53:24Z lenik $
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
