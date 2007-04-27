@echo off
rem $Id: tup.bat,v 1.9 2007-04-27 13:47:57 lenik Exp $

:begin
    rem find where to download

    call nu q
    set src_t=\\q\c$\t
    set src_cir=\\q\c$\.cirkonstancoj

    if not exist %src_t%\.dirt (
        echo dir-t %src_t% on public isn't available.
        goto end
    )

    if not "%1"=="" goto t_found

:find_dst
for %%i in (c d e f g h i j k l m n o p q r s t u v w x y z) do (
    if exist %%i:\t\.dirt set dst_t=%%i:\t
    if not "!dst_t!"=="" if not "!dst_cir!"=="" goto t_found
    if exist %%i:\t\2\.cirkonstancoj set dst_cir=%%i:\t\2
    if not "!dst_t!"=="" if not "!dst_cir!"=="" goto t_found
    for /d %%j in (%%i:\*) do (
        if exist %%j\.dirt set dst_t=%%j
        if not "!dst_t!"=="" if not "!dst_cir!"=="" goto t_found
        if exist %%j\.cirkonstancoj set dst_cir=%%j
        if not "!dst_t!"=="" if not "!dst_cir!"=="" goto t_found
    )
)

:t_not_found
    echo Unknown install-location of dirt and cirkonstancoj.
    echo You may need to re-install the full dirt.
    goto end

:t_found
    echo updating
    echo     source: %src_t%, %src_cir%
    echo     target: %dst_t%, %dst_cir%

    rem /C: continue on error
    rem /D: only copy newer files
    rem /E: sub-directories, even empty dirs.

    xcopy /c /d /e /y %src_t% %dst_t%
    for %%h in (0 1 3 4 5 6 7 8 9) do (
        call xdel "%src_t%\%%h" "%dst_t%\%%h"
    )
    echo dirt update successfully.

    xcopy /c /d /e /y %src_cir% %dst_cir%
    call xdel "%src_cir%" "%dst_cir%"
    echo cirkonstancoj update successfully.

    pushd "%dst_t%\0" >nul
    call syset.bat env
    popd >nul


:end
    set src_t=
    set src_cir=
    set dst_t=
    set dst_cir=
