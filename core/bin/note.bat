@echo off
rem $Id: note.bat,v 1.4 2004-10-02 05:11:17 dansei Exp $

rem 0, options
	if "%1"=="w" (
		set note_app=winword
		set note_ext=.doc
		shift
	) else if "%1"=="e" (
		set note_app=excel
		set note_ext=.xls
		shift
	) else if "%1"=="x" (
		set note_app=uedit32
		set note_ext=.xml
	) else (
		set note_app=notepad
		set note_ext=.txt
	)


rem 1, get the note title

	set note_serial=%date:~0,10%
	if "%1"=="" (
		set note=%note_serial%
		goto exit_arg
	)

	set note=%1
:next_arg
	shift
	if "%1"=="" goto exit_arg
	set note=!note! %1
	goto next_arg
:exit_arg


rem 2, special files
	if exist "%note%" (
		start %note_app% "%note%"
		goto cleanup
	)


rem 3, build '-' separated directory structure

	set note_home=%userprofile%\My Documents\Notes
	if exist "%note_home%" if not exist "%note_home%\*" (
		if not exist %temp%\notehome.tmp type "%note_home%" >%temp%\notehome.tmp
		for /f %%i in (%temp%\notehome.tmp) do set note_home=%%i
		if not exist "!note_home!\*" (
		    call:replace "!note_home!" "\" "\\"
		    lc /nologo "user32::MessageBoxA(0, 'Not mounted: !rp_ret!', 'Notes', 16)"
		    set rp_ret=
		    goto cleanup
		)
	)

	set note_ctr=%note_home%
	set note_cur=
	set note_char=%note:~0,1%
	set note_rest=%note:~1%
:parse
	if "%note_char%"=="-" set note_ctr=%note_ctr%\%note_cur%
	if "%note_rest%"=="" goto x_parse
	set note_cur=%note_cur%%note_char%
	set note_char=%note_rest:~0,1%
	set note_rest=%note_rest:~1%
	goto parse
:x_parse
	if "%note_char%"=="-" set note=%note%%note_serial%
	if exist "%note_ctr%\%note%\*" (
	    set note_ctr=%note_ctr%\%note%
	    set note=%note%-overall
	)
	set note_cur=
	set note_char=
	set note_rest=

	if not exist "%note_ctr%" md "%note_ctr%"
	if not exist "%note_ctr%\%note%%note_ext%" (
		if exist "%note_home%\.vol\def%note_ext%" (
			copy "%note_home%\.vol\def%note_ext%" "%note_ctr%\%note%%note_ext%" >nul
			rem touch "%note_ctr%\%note%%note_ext%"
		) else (
			type nul >"%note_ctr%\%note%%note_ext%"
		)
	)

	start %note_app% "%note_ctr%\%note%%note_ext%"

	goto cleanup


:replace
    set rp_src=%~1
    set rp_from=%~2
    set rp_to=%~3
    set rp_ret=
:replace_loop
    if "%rp_src%"=="" goto replace_end
    set rp_chr=%rp_src:~0,1%
    set rp_src=%rp_src:~1%
    if "%rp_chr%"=="%rp_from%" (
        set rp_ret=%rp_ret%%rp_to%
    ) else (
        set rp_ret=%rp_ret%%rp_chr%
    )
    goto replace_loop
:replace_end
    set rp_chr=
    set rp_src=
    set rp_from=
    set rp_to=
    goto end


:cleanup
	set note_app=
	set note_ext=
	set note=
	set note_home=
	set note_ctr=
	set note_serial=

:end
