@echo off

    if not "%OS%"=="Windows_NT" goto err_os

    setlocal
    set _pl=%~dpn0.pl
    if not exist "%_pl%" (
        for %%i in (p pc pld) do (
            if exist "%~dpn0.%%i" (
                set _pl=%~dpn0.%%i
                goto start
            )
        )
        echo is file %~dpn0.pl lost?
        goto end
    )

:start
    %perl% "%_pl%" %*
    goto end

:err_os
	echo You must run this program under Windows NT/2000 or above.
	goto end

:end
