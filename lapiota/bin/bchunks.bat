@echo off

    for %%i in (*.cue) do (
        if exist "%%~ni.bin" (
            echo [BCHK] %%~ni
            bchunk "%%~ni.bin" "%%i" "%%~ni"

            echo [....] dummy for ctrl-break purpose 1  >nul
            echo [....] dummy for ctrl-break purpose 2  >nul
            echo [....] dummy for ctrl-break purpose 3  >nul
            echo [....] dummy for ctrl-break purpose 4  >nul
            echo [....] dummy for ctrl-break purpose 5  >nul
            echo [....] dummy for ctrl-break purpose 6  >nul
            echo [....] dummy for ctrl-break purpose 7  >nul
            echo [....] dummy for ctrl-break purpose 8  >nul
            echo [....] dummy for ctrl-break purpose 9  >nul
            echo [....] dummy for ctrl-break purpose 10 >nul

            echo [KILL] %%i
            del "%%i" >nul
            echo [KILL] %%~ni.bin
            del "%%~ni.bin" >nul
        )
    )
