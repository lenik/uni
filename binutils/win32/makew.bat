@echo off

    setlocal

    if "%VS80ComnTools%"=="" (
        echo VS8 isn't installed.
        exit /b
    )
    call "%VS80ComnTools%vsvars32"

    rem  /DCMDW
    set CC=CL /nologo /W4 atls.lib ole32.lib shell32.lib
    make -e
