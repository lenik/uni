rem #Setup hotkeys

    call findabc autohotkey >nul
    if errorlevel 1 exit /b 1

    start %_home%\autohotkey %LAPIOTA%\etc\hotkey
