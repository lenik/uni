@echo off

    setlocal

    if "%~1"=="-h" (
        echo mani [-r] [-f] BASENAME
        echo    -r  reset the MANIFEST file.
        echo    -f  force to generate the project, no time condition.
        goto end
    )

    if "%~1"=="-r" (
        shift
        set _reset=1
    )
    if "%~1"=="-f" (
        shift
        set _nocmp=1
    )

    set list=.MANIFEST
    set proj=.MANIFEST.prj
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
    set _tmp=%TEMP%\manitemp.%RANDOM%
    if not exist "%list%" (
        echo Generating list file...
        echo %list%>"%_tmp%"
        for %%i in (*) do (
            set f=%%i
            if not "%%~ni"=="" (
                echo !f!>>"%_tmp%"
            )
        )
        for /r /d %%d in (*) do (
            for %%i in ("%%d\*") do (
                set f=%%i
                set f=!f:~%len%!
                if not "%%~ni"=="" (
                    echo !f!>>"%_tmp%"
                )
            )
        )
        move /y "%_tmp%" "%list%"
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
