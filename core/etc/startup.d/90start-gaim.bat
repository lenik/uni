rem #Start GAIM

    set _PF=%ProgramFiles%

    if not exist "%_PF%\Gaim\gaim.exe" exit /b 1

    cd /d "%_PF%\Gaim"
    start gaim.exe