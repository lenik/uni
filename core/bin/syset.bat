@echo off
    rem $Id: syset.bat,v 1.2 2004-09-22 08:39:12 dansei Exp $

	if not "%OS%"=="Windows_NT" goto err_notsupp

	if "%1"=="" goto help
	goto %1

:err_label
	echo Syntax error: %1
	echo.
	goto help

:err_not_supp
	echo You must run this program under Windows NT/2000 or above.
	goto end

:help
	echo [SYSET] Config system settings   * dir-t public *
	echo Written by Snima Denik               Version 1
	echo.
	echo Syntax:  syset sub-function [arguments]
	echo sub-functions:
	echo     help    show this help page
	echo     env     initialize environment variables for dir-t
	echo.
	echo This program is distributed under GPL license.
	echo If you have problems with this program, you can
	echo   mail to: dansei@163.com
	goto end

:env
	set t_dir=%~dp0
	set t_dir=%t_dir:~,-3%
	echo dir-t is located at %t_dir%
	%t_dir%\0\syset_env %t_dir%
	if not errorlevel 1 (
		echo initialize shell commands...
		regedit /s %temp%\syset.scr
		del %temp%\syset.scr
	)
	echo binding file types...
	assoc .stx=txtfile >nul

	assoc .pl=PerlScript >nul
	assoc .p=PerlScript >nul
	ftype PerlScript=%t_dir%\2\bin\perl.exe "%%0" %%* >nul

	assoc .py=PythonScript >nul
	ftype PythonScript=%t_dir%\2\bin\Python.exe "%%0" %%* >nul

	assoc .Main=JosMain >nul
	ftype JosMain=jsh.bat "%%0" %%* >nul

	goto end

:end
