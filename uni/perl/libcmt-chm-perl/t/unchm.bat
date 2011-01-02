@echo off

    set fl=-v -m -s -o=%TEMP%\%~n1

    perl -I%~dp0..\blib\arch -I%~dp0..\blib\lib \0\%~n0.pl %fl% %*
