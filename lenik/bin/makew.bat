@echo off

    setlocal

    call vc9

    rem  /DCMDW
    set CC=CL /nologo /W4 kernel32.lib user32.lib shell32.lib atls.lib ole32.lib uuid.lib

    make -e %*
