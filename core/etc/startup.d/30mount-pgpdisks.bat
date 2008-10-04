rem #Mount PGP disks

    setlocal

    set _MIAJ=C:\.radiko\.miaj

    if exist "%_MIAJ%\reset-junctions.bat" (
        cd /d "%_MIAJ%"
        echo.
        call reset-junctions.bat
    )

    if exist "%_MIAJ%\image\*" (
        cd /d "%_MIAJ%\image"
        for %%i in (*) do (
            if "%%~ni"=="crit-mirror" "%%i"
            if "%%~ni"=="crit-cvs" "%%i"
            if "%%~ni"=="pack-misc-box" "%%i"
        )
    )
