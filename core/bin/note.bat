@echo off
rem $Id: note.bat,v 1.16 2004-12-07 04:04:16 dansei Exp $

rem 0, options
    set note_exec=%~dp0
    set note_serial=%date:~0,10%

    if "%1"=="/del" (
        set note_delete=1
        shift
    )

    set note_a_doc=winword
    set note_a_xls=excel
    set note_a_xml=ue
    set note_a_txt=notepad
    set note_extf=%1
    if "%note_extf:~0,1%"=="/" (
        set note_ext=%note_extf:~1%
        shift
        if "!note_ext!"=="/" set note_serial=%date:~0,7%-index
    ) else (
        set note_extf=
        set note_ext=txt
    )


rem 1, get the note title
    if "%1"=="" (
        set note=%note_serial%
        goto args_3
    )
    if "%1"=="." (
        set note=%note_serial%
        shift
        goto args_3
    )

    set note=%1
    if "%note:~0,1%"=="." set note=.vol-%note:~1%
:part_1
    shift
    if "%1"=="" goto part_x
    set _tmp=%1
    if "%_tmp:~0,1%"=="/" goto part_x
    set note=!note! %1
    goto part_1
:part_x
    set _tmp=
    if "%note:~-1%"=="-" (
        set note=%note%%note_serial%
    ) else (
        if "!note_ext!"=="/" set note=%note%-index
    )

:args_3
    rem note [/ext] [title] [/app]
    if not "%1"=="" (
        set note_app=%1
        set note_app=!note_app:~1!
    )


rem 2, special files
    if exist "%note%" (
        start notepad "%note%"
        goto cleanup
    )


rem 3, find note home directory
    set note_home=%userprofile%\My Documents\Notes
    if exist "%note_home%.link" (
        for /f %%i in ('type "%note_home%.link"') do set note_home=%%i
        if not exist "!note_home!\*" (
            call:ss "!note_home!" "\" "\\"
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
                call:pj "!note_ctr!" "!note_ctr_link!"
                set note_ctr=!_ret!
                set note_ctr_link=
                if not exist "!note_ctr!\*" if not exist "!note_ctr!.link" (
                    call:ss "!note_ctr!" "\" "\\"
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
        set note=%note%-index
    )
    set note_cur=
    set note_char=
    set note_rest=


rem 5, open method
    if not exist "%note_ctr%" md "%note_ctr%"

    rem if // option, then build index
    if "%note_ext%"=="/" (
        pushd "%note_ctr%"
        grep -R "^>" * >%note_ctr%\%note%.txt
        start %note_a_txt% "%note_ctr%\%note%.txt"
        popd
        goto cleanup
    )

    rem otherwise, open the note
    if "%note_extf%"=="" if exist "%note_ctr%\%note%.*" (
        pushd "%note_ctr%"
        for %%i in (%note%.*) do (
            set note_x=%%~xi
            call:open "%%i" "!note_x:~1!"
        )
        set note_x=
        popd
        goto cleanup
    )

    rem create new file if not existed.
    if not "%note_delete%"=="1" if not exist "%note_ctr%\%note%.%note_ext%" (
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

    call:open "%note%.%note_ext%" "%note_ext%"

    goto cleanup


:open
    if "%note_delete%"=="1" (
        if not exist "%note_ctr%\%~1" (
            lc /nologo "user32::MessageBoxA(0,'Note file was not existed', 'Note', 16)"
            goto end
        )
        del "%note_ctr%\%~1" >nul
        if exist "%note_ctr%\%~1" (
            lc /nologo "user32::MessageBoxA(0,'Note file can not be deleted', 'Note', 16)"
            goto end
        )
        goto end
    )

    if not "%note_app%"=="" goto open_start
    set note_app=!note_a_%~2!
    if "%note_app%"=="" set note_app=ue
:open_start
    start %note_app% "%note_ctr%\%~1"
    goto end


    rem ######################################################################
    rem # Function: String Substitute
    rem # Synopsis: ss(string str, char source, string target)
    rem # Revision: 1.3
:ss
    set _ss_src=%~1
    set _ss_from=%~2
    set _ss_to=%~3
    set _ret=
:ss_1
    if "%_ss_src%"=="" goto ss_x
    set _ss_chr=%_ss_src:~0,1%
    set _ss_src=%_ss_src:~1%
    if "%_ss_chr%"=="%_ss_from%" (
        set _ret=%_ret%%_ss_to%
    ) else (
        set _ret=%_ret%%_ss_chr%
    )
    goto ss_1
:ss_x
    set _ss_chr=
    set _ss_src=
    set _ss_from=
    set _ss_to=
    goto end


    rem ######################################################################
    rem # Function: Path Join
    rem # Synopsis: pj(string path1, string path2)
    rem # Return:   path1\path2 (with no trialing slash)
    rem # Revision: 1.3
:pj
    set _ret=%~2
    if "%_ret:~1,1%"==":" goto pj_x
    if "%_ret:~0,1%"=="\" goto pj_x
    if "%_ret:~0,1%"=="/" goto pj_x
    set _pj_a=%~1
    set _pj_b=%~2
    if "%_pj_a:~-1%"=="\" set _pj_a=%_pj_a:~0,-1%
    if "%_pj_a:~-1%"=="/" set _pj_a=%_pj_a:~0,-1%
    set _ret=%_pj_a%\%_pj_b%
:pj_x
    set _pj_a=
    set _pj_b=
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
