@echo off
rem $Id: paths.bat,v 1.2 2004-09-22 08:39:11 dansei Exp $

if "%1"=="" ECHO %PATH%|TR ; \n
if not "%1"=="" (
	IF "%2"=="" (
		ECHO %%%1%%|TR ; \n
	) ELSE (
		ECHO %%%1%%|TR %2 \n
	)
)
