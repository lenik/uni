@echo off

    setlocal
    set _strict=1
    set _delete=0
    set _setuid=cvs
    goto init

:start

    set _cvsfl=
    if %_verbose% leq 0 (
        set _cvsfl=%_cvsfl% -Q
    ) else if %_verbose% equ 1 (
        set _cvsfl=%_cvsfl% -q
    )

    if "%_delete%"=="0" (
        set CVS_GETPASS=%_password%
        if %_verbose% geq 1 echo add cvs user %_username%...
        cvs %_cvsfl% passwd -a -r "%_setuid%" "%_username%"
    ) else (
        if %_verbose% geq 1 echo remove cvs user %_username%...
        cvs %_cvsfl% passwd -X "%_username%"
    )

    goto cleanup

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set _startdir=%~dp0
    set  _program=%~dpnx0

:prep1
    if "%~1"==""            goto help
    if "%~1"=="--version"   goto version
    if "%~1"=="-h"          goto help
    if "%~1"=="--help"      goto help
    if "%~1"=="-" (
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
    ) else if "%~1"=="--add" (
        set _delete=0
    ) else if "%~1"=="-a" (
        set _delete=0
    ) else if "%~1"=="--delete" (
        set _delete=1
    ) else if "%~1"=="-d" (
        set _delete=1
    ) else if "%~1"=="--setuid" (
        set _setuid=%~2
        shift
    ) else if "%~1"=="-s" (
        set _setuid=%~2
        shift
    ) else if "%_arg:~0,1%"=="-" (
        if "%_strict%"=="1" (
            echo Invalid option: %1
            goto end
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
    set _username=%~1
    set _password=%~2
    shift
    shift

:prep3
    if "%~1"=="" goto init_ok
    set _rest=%_rest%%~1
    shift
    goto prep3

:init_ok
    ppid
    set t=%TEMP%\pwd_%ERRORLEVEL%_%RANDOM%

    if %_verbose% geq 1 (
        echo _startdir=%_startdir%
        echo  _program=%_program%
        echo     _rest=%_rest%
        echo         t=%t%
        echo   _delete=%_delete%
        echo   _setuid=%_setuid%
        echo _username=%_username%
        echo _password=%_password%
    )
    goto start

:version
    set _id=$Id: cvsuser.bat,v 1.1 2007-07-23 10:40:23 lenik Exp $
    for /f "tokens=3-6" %%i in ("%_id%") do (
        set   _version=%%i
        set      _date=%%j
        set      _time=%%k
        set    _author=%%l
    )
    echo [cvsuser] CVS User Management
    echo Written by %_author%,  Version %_version%,  Last updated at %_date%
    goto end

:help
    call :version
    echo.
    echo Syntax:
    echo    %_program% ^<options^> user password
    echo.
    echo Options:
    echo    --quiet             (q)
    echo    --verbose           (v, repeat to get more info)
    echo    --version
    echo    --help              (h)
    echo    --add               (a, add-user mode (default) )
    echo    --delete            (d, delete-user mode)
    echo    --setuid=^<sys-user^> (s, default 'cvs')
    goto end

:cleanup
    if exist %t% del %t% >nul

:end
