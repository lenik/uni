@echo off

    setlocal

    call findabc smlnj bin

    set SMLNJ_HOME=%_home%
    set CM_PATHCONFIG=%SMLNJ_HOME%\lib\pathconfig

    set SMLNJ=%SMLNJ_HOME%\bin\.run\run.x86-win32.exe
    set _="@SMLload=%SMLNJ_HOME%\bin\.heap\sml"

    %SMLNJ% %_% %*
