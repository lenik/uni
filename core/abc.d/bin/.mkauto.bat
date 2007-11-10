@echo off

for /f %%i in (.autofind.lst) do (
    echo %%i
	copy .autofind.bat "%%i.bat" >nul
)
