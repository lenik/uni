@echo off

cd /d "%~dp0"

start "" "%~dp0truecrypt-6.1a\truecrypt" /a favorites /s /k %HOME%\db\keys\keyf /p cls

echo press any key after mounted...
pause>nul

echo recreate symlinks [using fstab.tc] ...
for /f "tokens=1-3" %%a in (fstab.tc) do (
    if exist "%%a:\*" (
        rem %%a = drive letter
        rem %%b = image file
        rem %%c = mount point
        if not "%%c"=="-" (
            for /f "usebackq" %%v in (`mountvol %%a:\ /l`) do (
                call :_mount "%%c" "%%v"
            )
        )
    )
)

exit /b 0

:_mount
    set _mpoint=%~1
    set _device=%~2
    echo ln -s %_device% %_mpoint%
:_tryloop
    rd "%_mpoint%" 2>nul
    md "%_mpoint%" 2>nul
    mountvol %_mpoint% %_device%
    if errorlevel 1 (
        set /p _retry=Failed to mount %_mpoint%: error %ERRORLEVEL%, try again?
        if "!_retry!"=="" goto _tryloop
        if "!_retry!"=="y" goto _tryloop
    )
