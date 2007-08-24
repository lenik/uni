@echo off

    setlocal

    call findabc smlnj

    set SMLNJ_HOME=%_home%
    set CM_PATHCONFIG=%_home%\lib\pathconfig

    set _="@SMLload=%SMLNJ_HOME%\bin\.heap\sml" %1 %2 %3 %4 %5 %6 %7 %8 %9

    "%SMLNJ_HOME%\bin\.run\run.x86-win32.exe" %_%
