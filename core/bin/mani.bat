@echo off

    setlocal

    if "%~1"=="-r" (
        shift
        set _reset=1
    )
    if "%~1"=="-f" (
        shift
        rem Force to re-generate the project file, without time-compare
        set _nocmp=1
    )

    set list=MANIFEST
    set proj=MANIFEST.prj
    if not "%~1"=="" (
        set list=%~1
        set proj=%~dpn1.prj
    )

    call libstr sn "%CD%\"
    set len=%_ret%

    if "%_reset%"=="1" (
        del "%list%" >nul
        del "%proj%" >nul
    )

:auto_list
    if not exist "%list%" (
        echo Generating list file...
        for %%i in (*) do (
            set f=%%i
            if not "%%~ni"=="" (
                echo !f!>>"%list%"
            )
        )
        for /r /d %%d in (*) do (
            for %%i in ("%%d\*") do (
                set f=%%i
                set f=!f:~%len%!
                if not "%%~ni"=="" (
                    echo !f!>>"%list%"
                )
            )
        )
    )

:conv_req
    call filetime "%list%"
    set T_LST=!ERRORLEVEL!

    call filetime "%proj%"
    set T_PRJ=!ERRORLEVEL!

    REM echo LST=%T_LST%, PRJ=%T_PRJ%
    if %T_LST% gtr %T_PRJ% goto conv
    if "%_nocmp%"=="1" goto conv
    goto start

:conv
    echo Generating project file...
    call lstconv ue "%list%" >"%proj%"

:start
    start ue "%proj%"

:end
