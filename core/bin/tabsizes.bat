@echo off
    rem $Id: tabsizes.bat,v 1.3 2006-04-23 05:40:54 lenik Exp $

	if "%1"=="-h" goto help

	set OLDSIZE=8
	set NEWSIZE=4
	if not "%1"=="" set OLDSIZE=%1
	call repl.pl "-c=tabsize::--space -o=%OLDSIZE% -n=%NEWSIZE% $file" -r *
	set OLDSIZE=
	goto end

:help
	echo tabsizes [tabsize-from] [tabsize-to]
	goto end

:end
