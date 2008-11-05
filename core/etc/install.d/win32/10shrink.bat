@echo off

    setlocal

    echo Disable .zip folder
    regsvr32 /u /s zipfldr
    del %SystemRoot%\SYSTEM32\dllcache\zipfldr.dll 2>Nul
    del %SystemRoot%\SYSTEM32\zipfldr.dll 2>nul

    echo Disable .cab folder
    regsvr32 /u /s cabview
    del %SystemRoot%\SYSTEM32\dllcache\cabview.dll 2>Nul
    del %SystemRoot%\SYSTEM32\cabview.dll 2>nul

    echo Disable low disk space checks
    set HK_EXPL=HKLM\Software\Microsoft\Windows\CurrentVersion\Policies\Explorer
    reg add %HK_EXPL% /v NoLowDiskSpaceChecks /t REG_DWORD /d 1 /f >nul

    rem disable services:
    rem     shell hardware
    rem     win help, update?
