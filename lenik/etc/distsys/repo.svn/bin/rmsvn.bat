@echo off

    setlocal
    cd /d "%~dp0.."

    set name=%~1
    set prefix=%name:~0,1%

    rd /s /q "%prefix%\%name%"
    rd %prefix% 2>nul
