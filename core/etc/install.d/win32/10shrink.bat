@echo off

    setlocal

    regsvr32 /u /s zipfldr
    regsvr32 /u /s cabview

    set HK_EXPL=HKLM\Software\Microsoft\Windows\CurrentVersion\Policies\Explorer
    reg add %HK_EXPL% /v NoLowDiskSpaceChecks /t REG_DWORD /d 1 /f

    rem HKEY_CURRENT_USER\Software\Microsoft\Windows\ShellNoRoam\MUICache
    del %SystemRoot%\SYSTEM32\dllcache\zipfldr.dll
    del %SystemRoot%\SYSTEM32\zipfldr.dll
