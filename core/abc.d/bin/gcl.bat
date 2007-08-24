@echo off

    setlocal

    call findabc gcl bin

    set C_INCLUDE_PATH=%_home%\lib\gcl-2.6.1\h
    set GCL=%_home%\lib\gcl-2.6.1\unixport\saved_gcl.exe
    set _=
    set _=%_% -dir    %_home%/lib/gcl-2.6.1/unixport/
    set _=%_% -libdir %_home%/lib/gcl-2.6.1/
    set _=%_% -eval   "(setq si::*allow-gzipped-file* t)"
    %GCL% %_% %*
