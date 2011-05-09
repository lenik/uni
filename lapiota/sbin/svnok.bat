@echo off

    setlocal
    set _strict=1
    goto init

:start
    set _outf=
    if %_verbose% lss 1 set _outf=^>nul

    set a_clear=-actn clear -clr "dacl"
    set a_owner=-actn setowner -ownr "n:S-1-5-32-544;s:y"
    set a_reset=-actn setprot -op "dacl:np;sacl:nc"

    for /d %%d in (.) do (
        if exist %%d\.svn\* call :svndir "%%~dpnxd"
    )
    exit /b 0

:svndir
    if %_verbose% geq 0 echo [reset] "%~1\.svn"
    rem yes | cacls %1\.svn %_caclsopt% %_outf%
    setacl -on "%~1\.svn" -ot file -rec cont_obj %a_owner% %_outf%
    if errorlevel 1 echo   error reset owner
    setacl -on "%~1\.svn" -ot file -rec cont_obj %a_reset% %a_clear% %_outf%
    if errorlevel 1 echo   error reset dacl/sacl

    for /d %%d in (%1\*) do (
        if exist "%%d\.svn\*" call :svndir "%%d"
    )
    exit /b

:init
    set  _verbose=0
    set      _ret=
    set     _rest=
    set _startdir=%~dp0
    set  _program=%~dpnx0
    set _caclsopt=/p everyone:f /t

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
    echo [BUGFIX] Clear ACL attributes of .svn directories for NT.
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
