@echo off
    rem $Id: tabsizes.bat,v 1.2 2004-09-22 08:39:12 dansei Exp $

	if "%1"=="" goto help

	pushd %1 >nul
	set OLDSIZE=8
	set NEWSIZE=4
	if not "%2"=="" set OLDSIZE=%2
	call repl.pl "-c=tabsize::--space -o=%OLDSIZE% -n=%NEWSIZE% $file" -r *
	set OLDSIZE=
	popd >nul
	goto end

:help
	echo tabsizes directory [tabsize-from] [tabsize-to]
	goto end

:end
