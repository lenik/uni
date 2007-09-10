@echo off

    rem Generated by plbat 1.4

    if not "%OS%"=="Windows_NT" goto err_os

    setlocal
    set _nam=%~n0
    set _ext=
    if not exist "%~dp0%_nam%" (
        for %%i in (pl p pc pld) do (
            if exist "%~dp0%_nam%.%%i" (
                set _ext=.%%i
                goto start
            )
        )
        echo is file %~dp0%_nam%.^(,p,pc,pl,pld^) lost?
        goto end
    )

:start
    %perl% "%~dp0%_nam%%_ext%" %*
    goto end

:err_os
    echo You must run this program under Windows NT/2000 or above.
    goto end

:end
