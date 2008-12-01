@echo off

    setlocal

    call :fsck "%LAPIOTA%\etc\lams"
    call :fsck "%HOME%\etc\lams"

    exit /b

:fsck
    set _lams=%~1
    if not exist "%_lams%" exit /b

    for /f "tokens=1,2 usebackq" %%i in ("%_lams%") do (
        if exist "%%i\*" (
            for /d %%d in (%%i) do (
                set _attr=%%~ad
                set _junc=!_attr:~-1!
                if "!_junc!"=="l" (
                    echo fsck for %%d:
                    chkdsk /x "%%d"
                )
            )
        )
    )
