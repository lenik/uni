@echo off

    setlocal
    set _strict=1
    goto init

:start

    REM _____________________________________________

    pushd "%_reposvn%" >nul
        set _prefix=%_base:~0,1%
        if not exist "%_prefix%\*" (
            echo create prefix %_prefix%
            mkdir "%_prefix%"
        )
        cd "%_prefix%"

        call mksvn "%_base%"
    popd >nul

    pushd "%~dp1" >nul
        set _server=
        set _user=
        set _pass=_NOTSET_

        set _autokey=%HOME%\etc\key\auto\dir2svn
        if exist "%_autokey%" (
            for /f "delims=| tokens=1,2,3 usebackq" %%i in ("%_autokey%") do (
                set _server=%%i
                set _user=%%j
                set _pass=%%k
            )
        )

        if "%_server%"=="" (
            set _url=file:///%_reposvn:\=/%/%_prefix%/%_base%/trunk
            set _opts=
            goto svn_import
        )
        set _url=svn://%_server%/%_prefix%/%_base%/trunk
        if "%_user%"=="" set /p _user=enter username, default %USERNAME%:
        if "%_user%"=="" set _user=%USERNAME%
        if "%_pass%"=="_NOTSET_" set /p _pass=enter password:
        set _opts=%_svnopts% --username "%_user%" --password "%_pass%"

      :svn_import
        echo import to the repository
        svn import %_opts% -m "" "%~nx1" "%_url%" >>%_logfile%

        echo rename original to %~nx1.dir2svn
        ren "%~nx1" "%~nx1.dir2svn"

        echo checkout the new imported dir
        svn checkout "%_url%" "%~nx1" >>%_logfile%
    popd >nul

    echo done.
    echo check the log file %_logfile% if you meet any problems.

    exit /b 0

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set _startdir=%~dp0
    set  _program=%~dpnx0
    set  _svnopts=
    set  _logfile=%TEMP%\dir2svn-%RANDOM%.log
    for /d %%d in ("%~dp0..") do set _reposvn=%%~dpnxd
    if not exist "%_reposvn%\etc\passwd" (
        echo can't locate //repo.svn
        exit /b 1
    )

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
    if not exist "%~1\*" (
        echo %~1 is not existed.
        exit /b 1
    )
    set _base=%~nx1

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
    echo [TITLE] CMD_simple_cli_program_template
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    exit /b 0

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% [OPTION] ...
    echo.
    echo Options:
    echo    -q, --quiet         repeat to get less info
    echo    -v, --verbose       repeat to get more info
    echo        --version       show version info
    echo    -h, --help          show this help page
    exit /b 0
