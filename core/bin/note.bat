@echo off
rem $Id: note.bat,v 1.11 2004-11-25 07:11:34 dansei Exp $

rem 0, options
    set note_exec=%~dp0

    if "%1"=="/del" (
        set note_delete=1
        shift
    )

    set note_a_doc=winword
    set note_a_xls=excel
    set note_a_xml=uedit32
    set note_a_txt=notepad
    set note_extf=%1
    if "%note_extf:~0,1%"=="/" (
        set note_ext=%note_extf:~1%
        shift
    ) else (
        set note_extf=
        set note_ext=txt
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
        start notepad "%note%"
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

    if "%note_ext%"=="/" (
        cd "%note_home%"
        grep -R "^>" * >%TEMP%\notes.lst
        start notepad %TEMP%\notes.lst
        goto cleanup
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
    if "%note_extf%"=="" if exist "%note_ctr%\%note%.*" (
        pushd "%note_ctr%"
        for %%i in (%note%.*) do (
            set note_x=%%~xi
            call:open "!note_x:~1!" "%%i"
        )
        set note_x=
        popd
        goto cleanup
    )

    if not "%note_delete%"=="1" (
        if not exist "%note_home%\.vol\*" (
            echo Initialize .vol templates
            unzip "%note_exec%notes.zip" -d "%note_home%"
            )

        if exist "%note_home%\.vol\.vol-def.%note_ext%" (
            if "%note_ext%"=="doc" (
                noteauto "%note_home%" create doc "%note_ctr%\%note%.%note_ext%"
            ) else if "%note_ext%"=="xls" (
                noteauto "%note_home%" create xls "%note_ctr%\%note%.%note_ext%"
            ) else (
                copy "%note_home%\.vol\.vol-def.%note_ext%" "%note_ctr%\%note%.%note_ext%" >nul
            )
            rem touch "%note_ctr%\%note%.%note_ext%"
        ) else (
            type nul >"%note_ctr%\%note%.%note_ext%"
        )
    )

    call:open %note_ext% "%note%.%note_ext%"

    goto cleanup


:open
    if "%note_delete%"=="1" (
        if not exist "%note_ctr%\%~2" (
            lc /nologo "user32::MessageBoxA(0,'Note file was not existed', 'Note', 16)"
            goto end
        )
        del "%note_ctr%\%~2" >nul
        if exist "%note_ctr%\%~2" (
            lc /nologo "user32::MessageBoxA(0,'Note file can not be deleted', 'Note', 16)"
            goto end
        )
        goto end
    )

    set note_app=!note_a_%~1!
    if "%note_app%"=="" set note_app=uedit32
    start %note_app% "%note_ctr%\%~2"
    goto end


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
    set note_a_doc=
    set note_a_xls=
    set note_a_xml=
    set note_a_txt=
    set note_app=
    set note_ext=
    set note_extf=
    set note=
    set note_home=
    set note_ctr=
    set note_serial=
    set note_delete=
    set note_exec=
    set _ret=

:end
