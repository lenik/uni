@echo off

	set rfc_home=L:\files\refer\rfc
	set rfc_edit=ue

	if "%1"=="u" (
		set rfc_edit=ue
		shift
	)

	if "%1"=="" goto help

:start
	pushd %rfc_home% >nul

	if exist "rfc%1.*" (
		for %%i in (rfc%1.*) do %rfc_edit% "%%i"
		goto end
	)

	if exist "rfc%1 *.*" (
		for %%i in ("rfc%1 *.*") do %rfc_edit% "%%i"
		goto end
	)

:listing
	echo RFC-files::
	for %%i in ("*%1*") do echo %%i
	echo.

	echo RFC-index::
	grep -i "%1" index\rfc-index.txt

	goto end

:help
	echo rfc [index]
	goto end

:cleanup
	popd >nul
	set rfc_home=

:end
