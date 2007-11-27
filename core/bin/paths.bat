@echo off
rem $Id$

if "%1"=="" ECHO %PATH%|TR ; \n
if not "%1"=="" (
	IF "%2"=="" (
		ECHO %%%1%%|TR ; \n
	) ELSE (
		ECHO %%%1%%|TR %2 \n
	)
)
