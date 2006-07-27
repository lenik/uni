@echo off

	rem $Id: cvscosf.bat,v 1.3 2006-07-27 15:49:07 lenik Exp $

	if "%1"=="" goto help

	set _mkdir=1

:arg_loop
	if "%1"=="-h" goto help
	if "%1"=="-r" set _cvsroot=-a
	if "%1"=="-d" set _mkdir=-d
	if "%1"=="-x" set _exit=1
	set _prj=%1
	shift
	if not "%1"=="" goto arg_loop
:arg_exit

	echo cvsco %_mkdir% %_cvsroot% -p %_prj%
	call cvsco %_mkdir% %_cvsroot% -p %_prj%
	goto cleanup


:help
	echo [CVSCOSF] CVS Checkout Source on SourceForge.net
	echo Written by Snima Denik, Oct 2004
	echo.
	echo Syntax:
	echo 	CVSCOSF -r -d unixname
	goto cleanup


:cleanup
	set _cvsroot=
	set _mkdir=
	set _prj=
	set _count=
	set _ret=
	if "%_exit%"=="1" exit

:end
