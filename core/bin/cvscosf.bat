@echo off

	rem $Id: cvscosf.bat,v 1.1 2004-10-11 00:40:08 dansei Exp $

	if "%1"=="" goto help

	set _mkdir=1

:arg_loop
	if "%1"=="-h" goto help
	if "%1"=="-r" set _cvsroot=1
	if "%1"=="-d" set _mkdir=1
	if "%1"=="-x" set _exit=1
	set _prj=%1
	shift
	if not "%1"=="" goto arg_loop
:arg_exit

	if "%_mkdir%"=="1" (
		if not exist "%_prj%\*" mkdir "%_prj%"
		cd "%_prj%"
	)

	set _count=-99999
	for /f "delims=*" %%i in ('getweb cvs.sourceforge.net http://cvs.sourceforge.net/viewcvs.py/%_prj%/') do (
		set _line=%%i
		set /a _count=_count+1
		if "!_count!"=="1" (
			set "_line=!_line:>=.!"
			set "_line=!_line:<=.!"
			call cmdex leftof "!_line!" "/"
			if "!_ret!"=="CVSROOT" if not "%_cvsroot%"=="1" set _ret=
			if not "!_ret!"=="" (
				cvs -d :pserver:anonymous@cvs.sourceforge.net:/cvsroot/%_prj% co !_ret!
			)
			set _count=99999
		)
		if "!_line:~10,20!"=="/icons/small/dir.gif" (
			set _count=0
		)
	)
	if "%_mkdir%"=="1" cd..
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
