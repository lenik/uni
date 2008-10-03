rem #Start Tencent QQ

    set _VAROJ=C:\.radiko\varoj

    if not exist "%_VAROJ%\Network\Messenger\QQ\QQ.exe" exit /b 1

    cd /d "%_VAROJ%\Network\Messenger\QQ"
    start QQ.exe
