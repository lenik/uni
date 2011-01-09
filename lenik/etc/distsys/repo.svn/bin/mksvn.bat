@echo off

    setlocal
    cd /d "%~dp0.."

    set svnd=
    set _svn1=%SystemDrive%\Program Files\CollabNet Subversion Server
    for %%i in (9 8 7 6 5 4 3 2 1) do (
        if exist "!_svn%%i!\svn.exe" set svnd=!_svn%%i!
    )
    set svn=%svnd%\svn.exe
    set svnadmin=%svnd%\svnadmin.exe

    set name=%~1
    set prefix=%name:~0,1%

    echo create repository %prefix%\%name%
    rem set svnopts=--config-dir %~dp0..\etc
    "%svnadmin%" create "%prefix%\%name%"

    echo configure
    copy /y "%~dp0..\etc\svnserve.conf" "%prefix%\%name%\conf" >nul

    echo done
