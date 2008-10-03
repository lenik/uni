rem #Mount mapping drives

    set _MIAJ=C:\.radiko\.miaj
    if exist "%_MIAJ%\drive\mapping-drives.bat" (
        cd /d "%_MIAJ%\drive"
        echo.
        call mapping-drives.bat
    )
