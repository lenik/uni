@echo off

                              echo [%TIME%] Executing %1... 1>&2
    if "%DCALL_VERBOSE%"=="1" echo [%TIME%] cmdline: %* 1>&2

                          rem printf '[%TIME%] ' 1>&2
                              call %*

    if "%DCALL_VERBOSE%"=="1" echo [%TIME%] dcall %1 end 1>&2
