@echo off

echo ^^!define __DIR__ "%CD%">%TEMP%\lapienv.nsh

rem /lib/nsis/__THIS__
set _root=%~dp0..\..
if exist "%_root%\bin\lapiota-init.*" (
    call "%_root%\bin\lapiota-init" init
)

call shtext "%~dp0lapienv.sht" >>%TEMP%\lapienv.nsh
