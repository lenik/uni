@echo off

rem for axilias iconworkshop
rem

for %%i in (*) do (
    set name=%%~ni
    set ext=%%~xi
    echo ren %%i !name:~,-3!!ext!
    ren %%i !name:~,-3!!ext!
)
