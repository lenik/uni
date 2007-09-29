@echo off

    if "%~1"=="" goto help
    goto start

:help
    echo Install modes:
    echo    /quiet /passive /uninstall
    echo Restart:
    echo    /norestart /forcerestart
    echo Options:
    echo    /l  list installed packages
    echo    /o  don't prompt to overwrite OEM files
    echo    /n  don't backup
    echo    /f  force other programs to be closed when shutdown
    echo    /integrate:^<fullpath^>
    exit /b 1

:start
    echo Installing %~1...
    start /wait "Installing %~1..." "%~1" /quiet /norestart /o /n
