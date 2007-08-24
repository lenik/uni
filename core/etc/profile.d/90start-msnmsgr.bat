rem #Start MSN Messenger

    set _PF=%ProgramFiles%

    if not exist "%_PF%\MSN Messenger\msnmsgr.exe" exit /b 1

    cd /d "%_PF%\MSN Messenger"
    start msnmsgr.exe
