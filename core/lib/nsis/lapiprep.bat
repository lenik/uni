@echo off

echo ^^!define __DIR__ "%CD%">%TEMP%\lapienv.nsh

call shtext "%~dp0lapienv.sht" >>%TEMP%\lapienv.nsh
