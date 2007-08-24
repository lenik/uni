rem #Start Apache Server

    call findabc apache >nul
    if errorlevel 1 exit /b 1

    call dCall iisreset /stop
    if errorlevel 30 set _iisrestart=1
    net start Apache2
    start %_home%\bin\ApacheMonitor

    if "%_iisrestart%"=="1" (
        iisreset /start
    )
