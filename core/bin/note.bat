@echo off


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

	if "%1"=="" (
		set note=%date:~0,10%
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
	)

	set note_ctr=%note_home%
	set note_rest=%note%
	set note_cur=
	set note_char=
:parse
	set note_char=%note_rest:~0,1%
	set note_rest=%note_rest:~1%
	if "%note_char%"=="-" set note_ctr=%note_ctr%\%note_cur%
	if "%note_rest%"=="" goto x_parse
	set note_cur=%note_cur%%note_char%
	goto parse
:x_parse

	if not exist "%note_ctr%" md "%note_ctr%"
	if not exist "%note_ctr%\%note%%note_ext%" (
		if exist "%note_home%\.vol\def%note_ext%" (
			copy "%note_home%\.vol\def%note_ext%" "%note_ctr%\%note%%note_ext%" >nul
		) else (
			type nul >"%note_ctr%\%note%%note_ext%"
		)
	)

	start %note_app% "%note_ctr%\%note%%note_ext%"


:cleanup
	set note_app=
	set note_ext=
	set note=
	set note_home=
	set note_ctr=
	set note_rest=
	set note_cur=
