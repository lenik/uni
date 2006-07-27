@echo off

    set _PORT=51296
    nc -L -p %_PORT%

:end
    set _PORT=
