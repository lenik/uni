@echo off

    setlocal

    for /f "delims=: tokens=2" %%i in ('chcp') do set /a _cp=%%i

    if %_cp% neq 437 (
        start "ANSI" "%CYGWIN_ROOT%\bin\mc" %*
    ) else (
        "%CYGWIN_ROOT%\bin\mc" %*
    )
