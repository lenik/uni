@echo off

    echo Init environment variables...

    call :about_rel %PERL%

    set DEV_HOME=C:\strawberry-perl
    set     PATH=%DEV_HOME%\perl\bin;%DEV_HOME%\dmake\bin;%DEV_HOME%\mingw;%DEV_HOME%\mingw\bin;%PATH%
    set      LIB=%DEV_HOME%\mingw\lib;%DEV_HOME%\perl\bin;%LIB%
    set  INCLUDE=%DEV_HOME%\mingw\include;%DEV_HOME%\perl\lib\CORE;%DEV_HOME%\perl\lib\encode;%INCLUDE%
    set     PERL=%DEV_HOME%\perl\bin\perl.exe
    set  PERLLIB=%DEV_HOME%\perl\lib;%DEV_HOME%\perl\site\lib;%DIRT_HOME%\0\lib

    if not exist %DEV_HOME%\workspace mkdir %DEV_HOME%\workspace
    cd %DEV_HOME%\workspace

    rem %RANDOM%
    title Perldev Console Id.%RANDOM%

    %ComSpec% /K
    goto end

:about_rel
    set REL_HOME=%~dp1

    rem remove ...[/bin/]
      set REL_HOME=%REL_HOME:~0,-5%

    goto end

:end
