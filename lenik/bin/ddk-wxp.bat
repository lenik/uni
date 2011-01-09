@echo off

    set DDK=Z:\.radiko\varoj\SDK\WINDDK\2600.1106
    set PREVD=%CD%
    call "%DDK%\bin\setenv" %DDK% %* WXP
    cd /d "%PREVD%"
    cmd
