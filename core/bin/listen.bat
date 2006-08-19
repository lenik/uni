@echo off

    set _PORT=51296
    if not "%1"=="" set _PORT=%1

    nc -L -p %_PORT%

:end
    set _PORT=
