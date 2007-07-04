@echo off

    setlocal

    if "%~1"=="-r" (
        shift
        set _reset=1
    )

    set list=MANIFEST
    if not "%~1"=="" set list=%~1

    call libstr sn "%CD%\"
    set len=%_ret%

    if "%_reset%"=="1" (
        del "%list%" >nul
        del "%list%.prj" >nul
    )

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

    call filetime "%list%"
    set T_LST=!ERRORLEVEL!

    call filetime "%list%.prj"
    set T_PRJ=!ERRORLEVEL!

    REM echo LST=%T_LST%, PRJ=%T_PRJ%
    if %T_LST% GTR %T_PRJ% (
        echo Generating project file...
        call lstconv ue "%list%" >"%list%.prj"
    )

    start ue "%list%.prj"
