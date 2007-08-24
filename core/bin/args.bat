@echo off

    setlocal

:start
	echo arg.* = %*
	echo arg.0 = %0

:loop
    if "%~1"=="" (
        set _=%1.
        set _=!_:"=?!
        if !_!==. goto end
	)
	set /a i = i + 1
	echo arg.%i% = `%1'
	echo                                 ~ `%~1'
	shift
    goto loop

:end
