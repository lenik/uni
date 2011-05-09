@echo off

    echo %%0 = [%0]
	echo %%* = [%*]
	echo.

    setlocal
    set _i=0

:loop
    if "%~1"=="" goto end
    set /a _i = _i + 1
    echo %%%_i% = [%1]
    echo    ~ [%~1]
    shift
    goto loop

:end
