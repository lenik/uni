@echo off
rem $Id: note.bat,v 1.5 2004-10-08 08:09:34 dansei Exp $

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
    if "%note:~-1%"=="-" set note=%note%%note_serial%


rem 2, special files
    if exist "%note%" (
        start %note_app% "%note%"
        goto cleanup
    )


rem 3, find note home directory
    set note_home=%userprofile%\My Documents\Notes
    if exist "%note_home%" if not exist "%note_home%\*" (
        for /f %%i in ('type "%note_home%"') do set note_home=%%i
        if not exist "!note_home!\*" (
            call:replace "!note_home!" "\" "\\"
            lc /nologo "user32::MessageBoxA(0, 'Not mounted: !_ret!', 'Notes', 16)"
            goto cleanup
        )
    )


rem 4, build '-' separated directory structure
    set note_ctr=%note_home%
    set note_cur=
    set note_char=%note:~0,1%
    set note_rest=%note:~1%
:parse
    if "%note_char%"=="-" (
        if not exist "%note_ctr%\%note_cur%.link" (
            set note_ctr=%note_ctr%\%note_cur%
        ) else (
:parse_link
            if exist "%note_ctr%\%note_cur%.link" (
                for /f %%i in ('type "%note_ctr%\%note_cur%.link"') do set note_ctr_link=%%i
                call:join "!note_ctr!" "!note_ctr_link!"
                set note_ctr=!_ret!
                set note_ctr_link=
                if not exist "!note_ctr!\*" if not exist "!note_ctr!.link" (
                    call:replace "!note_ctr!" "\" "\\"
                    lc /nologo "user32::MessageBoxA(0, 'Link target not existed: !_ret!', 'Notes', 16)"
                    goto cleanup
                )
                goto parse_link
            )
        )
    )
    if "%note_rest%"=="" goto x_parse
    set note_cur=%note_cur%%note_char%
    set note_char=%note_rest:~0,1%
    set note_rest=%note_rest:~1%
    goto parse
:x_parse
    rem create index file if the note file is a directory.
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
    set _ret=
:replace_loop
    if "%rp_src%"=="" goto replace_end
    set rp_chr=%rp_src:~0,1%
    set rp_src=%rp_src:~1%
    if "%rp_chr%"=="%rp_from%" (
        set _ret=%_ret%%rp_to%
    ) else (
        set _ret=%_ret%%rp_chr%
    )
    goto replace_loop
:replace_end
    set rp_chr=
    set rp_src=
    set rp_from=
    set rp_to=
    goto end


:join
    set _ret=%~2
    if "%_ret:~1,1%"==":" goto join_end
    if "%_ret:~0,1%"=="\" goto join_end
    if "%_ret:~0,1%"=="/" goto join_end
    set jp_a=%~1
    set jp_b=%~2
    if "%jp_a:~-1%"=="\" set jp_a=%jp_a:~0,-1%
    if "%jp_b:~-1%"=="\" set jp_b=%jp_b:~0,-1%
    set _ret=%jp_a%\%jp_b%
:join_end
    set jp_a=
    set jp_b=
    goto end


:cleanup
    set note_app=
    set note_ext=
    set note=
    set note_home=
    set note_ctr=
    set note_serial=
    set _ret=

:end
