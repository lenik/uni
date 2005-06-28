@echo off

	set rfc_home=L:\Files\standard\rfc
	set rfc_edit=ue

	if "%1"=="u" (
		set rfc_edit=ue
		shift
	)

	if "%1"=="" goto help

	set rfc_n=%1
	set rfc_m=%rfc_n:~-2%
	call libstr sn "%rfc_n%"

	if "%_ret%"=="2" set rfc_p=00xx
	if "%_ret%"=="3" set rfc_p=0%rfc_n:~0,1%xx
	if "%_ret%"=="4" set rfc_p=%rfc_n:~0,2%xx

:start
	pushd %rfc_home% >nul

	if exist "%rfc_p%\%1.*" (
		for %%i in (%rfc_p%\%1.*) do start %rfc_edit% "%%i"
		goto end
	)

:listing
	echo RFC-index::
	grep -i "%1" rfc-index.txt

	if /i not "%0"=="rfc" pause

	goto end

:help
	echo rfc [index]
	goto end

:cleanup
	popd >nul
	set rfc_home=
	set rfc_n=
	set rfc_m=
	set rfc_p=

:end
