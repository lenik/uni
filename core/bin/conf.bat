@echo off
rem $Id$


set DEFAULT_EDITOR=notepad

:Next
if "%1"=="" goto :EOF

if "%1"=="conf"		%DEFAULT_EDITOR% %~dpnx0
if "%1"=="hosts"	%DEFAULT_EDITOR% %windir%\system32\drivers\etc\hosts
if "%1"=="quotes"	%DEFAULT_EDITOR% %windir%\system32\drivers\etc\quotes

if "%1"=="boot" (
	attrib -h -s -r %SystemDrive%\boot.ini
	%DEFAULT_EDITOR% %SystemDrive%\boot.ini
)

shift
goto Next
