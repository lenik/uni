@echo off

    setlocal

    if not "%1"=="[" goto prep2
:prep
    if "%1"=="]" (
        shift
        goto prep2
    )

    set /a _num=%1
    if "%_num%"=="%1" if "%2"=="]" (
        set _delay=%_num%
    )

    rem more options...

    shift
    goto prep

:prep2
    if "%1"=="" goto begin
    set _cl=%_cl% %1
    shift
    goto prep2

:begin
    title "%_cl%"

:loop
	call %_cl%

    if not "%_delay%"=="" (
        sleep -m %_delay%
    )

    goto loop

:end
