@echo off

:args_loop
	if "%1"=="-v" (
		set _cmdex_verb=1
	) else (
		goto args_exit
	)
	shift
	goto args_loop
:args_exit
	shift
	goto %0


:notfound
	echo Subroutine not found.
	goto end


:strlen
	rem [_ret] = strlen [str]
	set /a _cmdex_nest=_cmdex_nest+1

	set _sl_str=%~1
	set _ret=0
	if "%_sl_str%"=="" goto strlen_exit
:strlen_loop
	if "%_sl_str%"=="" goto strlen_exit
	set _sl_str=%_sl_str:~1%
	set /a _ret=_ret+1
	goto strlen_loop
:strlen_exit
	set _sl_str=
	goto end


:match
	rem [_ret] = match [pattern] [str]
	set /a _cmdex_nest=_cmdex_nest+1

	set _mt_pat=%~1
	set _mt_str=%~2
	set _mt_ret=0
	call:strlen %_mt_pat%
	set /a _mt_patlen=%_ret%
	set /a _mt_i=0
:match_loop
	set _mt=_mt_str:~%_mt_i%,%_mt_patlen%

	rem for /f "delims=*" %%i in ('echo ".!%_mt%!"') do set _mt_seg=%%~i
	set _mt_seg=!%_mt%!

	if "%_mt_seg%"=="" goto match_exit
	if "%_mt_seg%"=="%_mt_pat%" (
		set _mt_ret=1
		goto match_exit
	)
	set /a _mt_i=_mt_i+1
	goto match_loop
:match_exit
	set _mt_pat=
	set _mt_str=
	set _mt_seg=
	set _mt_i=
	set _ret=%_mt_ret%
	goto end


:leftof
	rem [_ret] = leftof [str] [delim]
	set /a _cmdex_nest=_cmdex_nest+1

	set _str=%~1
	set _delim=%~2
	set _ret=
	if "%_str%"=="" goto leftof_exit
:leftof_loop
	if "%_str%"=="" goto leftof_exit
	set _char=%_str:~0,1%
	set _str=%_str:~1%
	if "%_char%"=="%_delim%" goto leftof_exit
	set _ret=%_ret%%_char%
	goto leftof_loop
:leftof_exit
	set _str=
	set _char=
	goto end


:end
	if "%_cmdex_nest%"=="1" (
		if "%_cmdex_verb%"=="1" echo Return: %_ret%
		set _cmdex_verb=
	)
	set /a _cmdex_nest=_cmdex_nest-1
