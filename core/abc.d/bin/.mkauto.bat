@echo off

    setlocal

    set tmp=%TMP%\mka.%RANDOM%

    for /f "eol=# tokens=1-4" %%a in (.autofind.lst) do (
	m4 -P -DNAME="%%a" -DSUB="%%b" -DDEFEXT="%%c" -DEXEC="%%d" .autofind.bat >%tmp%
        diff -q %tmp% "%%a.bat" >nul 2>nul
        if errorlevel 1 (
            echo %%a
            copy /y %tmp% "%%a.bat" >nul
        )
    )

    del %tmp% 2>nul
