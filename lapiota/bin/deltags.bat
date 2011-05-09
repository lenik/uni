@echo off

	if "%1"=="" goto help

	if not exist temp-dts\* mkdir temp-dts
	for %%i in (%1) do deltag %%i >temp-dts\%%i
	move /y temp-dts\* .
	rd temp-dts

	goto end

:help
	echo [DELTAGS] HTML/XML Tag Removal
	echo Sytnax:
	echo   deltags filename(wild chars)

:end
