@echo off

	if "%1"=="" (
		for /f "delims=*" %%i in ('set') do (
			call cmdex leftof "%%i" "="
			echo !_ret!
		)
		goto end
	)


:arg_loop
	for /f "delims=*" %%i in ('set %1') do (
		call cmdex leftof "%%i" "="
		echo !_ret!
	)
	shift
	if not "%1"=="" goto arg_loop
	goto end


:end
