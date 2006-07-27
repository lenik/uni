@echo off

    set _HOST=127.0.0.1
    set _PORT=51296

    if not "%1"=="" set _HOST=%1
    if not "%2"=="" set _PORT=%2

    nc %_HOST% %_PORT%

:end
    set _PORT=
