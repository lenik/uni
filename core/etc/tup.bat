@echo off
rem $Id: tup.bat,v 1.3 2004-09-22 08:39:12 dansei Exp $

:begin
	if "%temp%"=="" set temp=%tmp%
	if "%temp%"=="" (
		md c:\temp
		set temp=c:\temp
	)
	echo open q.host.bodz.net>%temp%\ftp-log.tmp
	echo anonymous>>%temp%\ftp-log.tmp
	echo t-user@q>>%temp%\ftp-log.tmp
	echo cd /public/export>>%temp%\ftp-log.tmp
	echo lcd %temp%>>%temp%\ftp-log.tmp
	echo get q.local>>%temp%\ftp-log.tmp
	echo close>>%temp%\ftp-log.tmp
	echo quit>>%temp%\ftp-log.tmp
	ftp -s:"%temp%\ftp-log.tmp" >nul 2>nul

	set tup_src=
	for /f %%i in (%temp%\q.local) do (
		set tup_src=\\%%i\public_dir-t
	)

	del "%temp%\ftp-log.tmp" >nul 2>nul
	del "%temp%\q.local" >nul 2>nul

	if not exist %tup_src% echo dir-t %tup_src% on public isn't available.
	if not exist %tup_src% goto end

	if not "%1"=="" goto t_found

:find_dst
for %%i in (c d e f g h i j k l m n o p q r s t u v w x y z) do (
	if exist %%i:\t\0\null.bat set tup_dst=%%i:\t
	if exist %%i:\t\0\null.bat goto t_found

	for %%j in (%%i\t*) do (
		if exist %%j\0\null.bat set tup_dst=%%j
		if exist %%j\0\null.bat goto t_found
	)
)

:t_not_found
	echo source directory of dir-t not found
	goto end

:t_found
	echo updating
	echo     source: %tup_src%
	echo     target: %tup_dst%
	xcopy /d /e /y "%tup_src%" "%tup_dst%"
	echo dir-t update successfully.
	pushd "%tup_dst%\0" >nul
	call syset.bat env
	popd >nul

:end
	set tup_src=
	set tup_dst=
