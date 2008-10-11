@echo off

setlocal

if "%~2"=="" (
    if exist "tmpdir\." rd /s /q tmpdir
    md tmpdir
    set _mpoint=%~dp0tmpdir
) else (
    set _mpoint=%~2
)

call partcp -a "%_mpoint%" -o "%~1" -z0x60
call partcp -f "%~1" -Dfill-range=12,16 -Pcrc.pgp -l0x160 -o "%~1" -z12

exit /b
