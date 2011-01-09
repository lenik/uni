@echo off

setlocal
set PATH=C:\windows\system32;%~dp0bin
cd /d "%~dp0"
bash -c ./mount.sh
