@echo off
rem $Id: cvscopy.bat,v 1.1 2005-01-05 03:07:45 dansei Exp $

	set CC_CMDLINE=%0 %*
	set CC_CLEAN=1
	if "%1"=="-d" (
		set CC_CLEAN=0
		shift
	)

	if "%1"=="" goto help
	set CC_MOD=%1

	set CC_ROOT=%CVSROOT%
	if not "%2"=="" set CC_ROOT=%2


rem 1, Checkout
	set CC_TEMP=CCTemp.%random%
	mkdir %CC_TEMP%
	cd %CC_TEMP%
	cvs -d %CC_ROOT% co %CC_MOD%
	if not exist "%CC_MOD%\*" (
		echo Checkout failed.
		goto end
	)

rem 2, "Chroot"
	if "%CC_CLEAN%"=="1" (
		for /d /r %%i in (*) do (
			if "%%~nxi"=="CVS" rd /s /q %%i
		)
	)
	for /d %%i in ("%CC_MOD%") do set CC_NAME=%%~nxi
	xcopy /y /d /e "%CC_MOD%" ..\%CC_NAME%\

rem 3, Remove directories
	cd..
	rd /s /q %CC_TEMP%

rem 4, Update .sync-cvscopy-%mod%.bat file
	if not exist .VEX\sync\* md .VEX\sync
	echo @echo off				>.sync-cvscopy-%CC_NAME%.bat
	echo rem CVSCOPY-Rev: $Rev$		>>.sync-cvscopy-%CC_NAME%.bat
	echo rem Thie file is auto generated by CVSCOPY Program, and it will >>.sync-cvscopy-%CC_NAME%.bat
	echo rem be automaticly updated. Please don't edit this file. >>.sync-cvscopy-%CC_NAME%.bat
	echo %CC_CMDLINE%			>>.sync-cvscopy-%CC_NAME%.bat

	goto end


:help
	echo [CVSCOPY] CVS Local Copy and Synchronize
	echo Written by Snima Denik, Update $Date: 2005-01-05 03:07:45 $
	echo Syntax
	echo 	CVSCOPY -d module/path [cvsroot]
	echo Options
	echo 	-d	Don't remove CVS directory
	goto end

:end
	set CC_ROOT=
	set CC_MOD=
	set CC_CLEAN=
	set CC_TEMP=
	set CC_NAME=
	set CC_CMDLINE=
