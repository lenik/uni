@echo off

    set _CMDLINE=0
    if /i "%0"=="recv" set _CMDLINE=1

    set _PORT=51296
    if not "%1"=="" (
        set _PORT=%1
        shift
    )

    set _=nc %1 %2 %3 %4 %5 %6 %7 %8 %9 -L -p %_PORT%

    if "%verbose%"=="1" echo %_%

    if "%_CMDLINE%"=="1" (
        %_%
    ) else (
        start "Recv from %_PORT%> " %_%
    )

:end
    set _PORT=
    set _CMDLINE=
