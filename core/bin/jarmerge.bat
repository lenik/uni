@echo off

    setlocal
    set _strict=1
    goto init

:start
    if "%~1"=="" (
        echo Nothing to merge.
        exit /b 0
    )

    set _merged=%TMP%\jarmerge-%RANDOM%
    if exist "%_merged%\." (
        echo tmp dir not empty: %_merged%
        exit /b 1
    )
    md "%_merged%" 2>nul

:each_jar
    if "%~1"=="" goto _merge
    for %%f in (%~1) do (
        if "%_mainjar%"=="%%~dpnxf" (
            echo Ignore %%~dpnxf
        ) else (
            echo Extracting %%~dpnxf
            pushd "%_merged%"
            jar -xf "%%~dpnxf"
            popd
        )
    )
    shift
    goto each_jar

:_merge
    if exist "%_mainjar%" (
        echo Extracting %_mainjar%
        pushd "%_merged%"
        jar -xf "%_mainjar%"
        popd
    )
    echo Merge to %_mainjar%
    pushd "%_merged%"
    jar -cf "%_mainjar%" .
    popd
    echo Clean up %_merged%
    rd /s /q "%_merged%"
    echo Done.
    exit /b 0

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set _startdir=%~dp0
    set  _program=%~dpnx0

:prep1
    if "%~1"==""            goto prep2
    if "%~1"=="--version"   goto version
    if "%~1"=="-h"          goto help
    if "%~1"=="--help"      goto help
    if "%~1"=="--" (
        shift
        goto prep2
    )
    set _arg=%~1

    if "%~1"=="-q" (
        set /a _verbose = _verbose - 1
    ) else if "%~1"=="--quiet" (
        set /a _verbose = _verbose - 1
    ) else if "%~1"=="-v" (
        set /a _verbose = _verbose + 1
    ) else if "%~1"=="--verbose" (
        set /a _verbose = _verbose + 1
    ) else if "%_arg:~0,1%"=="-" (
        if "%_strict%"=="1" (
            echo Invalid option: %1
            exit /b 1
        ) else (
            set _%_arg:~1%=%~2
            if %_verbose% geq 1 echo _%_arg:~1%=%~2
            shift
        )
    ) else (
        goto prep2
    )
    shift
    goto prep1

:prep2
    if "%~1"=="" goto help
    set _mainjar=%~dpnx1
    shift

:init_ok
    if %_verbose% geq 1 (set _ | tabify -b -d==)
    goto start

:version
    set _id=$Id: .bat 845 2008-09-29 11:45:47Z lenik $
    for /f "tokens=3-6" %%i in ("%_id%") do (
        set   _version=%%i
        set      _date=%%j
        set      _time=%%k
        set    _author=%%l
    )
    echo [jarmerge] Merge jars to a single jar
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [OPTION] DEST-JAR JARS...
    echo.
    echo Options:
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    exit /b 0
