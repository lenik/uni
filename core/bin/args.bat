@echo off

	set _arg_i=0
	set /a _arg_max=%1

	if %_arg_max% gtr 1 goto start
	set _arg_max=9

:start
	echo Arg-*: %*
	echo Arg-0: %0

:loop
	if %_arg_i% geq %_arg_max% goto loop_x

	set /a _arg_i=_arg_i+1

	echo Arg-%_arg_i%: %1

	shift

goto loop

:loop_x
	set _arg_i=
	set _arg_max=

:end
