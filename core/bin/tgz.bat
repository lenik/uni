@echo off

cd "%~dp1"
set basename=%~nx1
if exist "%basename%.tar.gz" del "%basename%.tar.gz"

tar zcvf "%basename%.tar.gz" "%basename%"

pause Finished, press any key to continue.
