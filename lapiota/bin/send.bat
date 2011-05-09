@echo off

    set _CMDLINE=0
    if /i "%0"=="send" set _CMDLINE=1

    set _HOST=127.0.0.1
    set _PORT=51296
    if not "%1"=="" (
        set _HOST=%1
        if not "%2"=="" (
            set _PORT=%2
            shift
        )
        shift
    )

    set _=nc %1 %2 %3 %4 %5 %6 %7 %8 %9 %_HOST% %_PORT%

    if "%verbose%"=="1" echo %_%

    if "%_CMDLINE%"=="1" (
        %_%
    ) else (
        start "Send to %_HOST%:%_PORT%> " %_%
    )

:end
    set _HOST=
    set _PORT=
    set _CMDLINE=
