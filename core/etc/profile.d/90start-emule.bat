rem #Start eMule

    set _PF=%ProgramFiles%

    if not exist "%_PF%\eMule\emule.exe" exit /b 1

    cd /d "%_PF%\eMule"
    start emule.exe
