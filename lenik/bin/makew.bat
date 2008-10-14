@echo off

    setlocal

    call vc6

    rem  /DCMDW
    set CC=CL /nologo /W4 atls.lib ole32.lib uuid.lib shell32.lib

    make -e %*
