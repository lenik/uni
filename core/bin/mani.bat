@echo off

    setlocal

    set LST=MANIFEST
    if not "%~1"=="" set LST=%~1

    call libstr sn "%CD%\"
    set len=%_ret%

    if not exist "%LST%" (
        for /r %%i in (*) do (
            if not "%%~nxi"=="%LST%" (
                set f=%%i
                echo !f:~%len%! >>"%LST%"
            )
        )
    )

    call filetime "%LST%"
    set T_LST=!ERRORLEVEL!

    call filetime "%LST%.prj"
    set T_PRJ=!ERRORLEVEL!

    if %T_LST% GTR %T_PRJ% (
        echo Generating project file...
        call lstconv ue "%LST%" >"%LST%.prj"
    )

    start ue "%LST%.prj"
