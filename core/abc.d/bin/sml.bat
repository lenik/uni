@echo off

if not exist "%SMLNJ_HOME%\bin\sml.bat" (
    echo Environment variable SMLNJ_HOME has not been set correctly.
    goto end
)

if "%0"=="ml" goto manual
if "%0"=="ML" goto manual

:auto
    start "Big" "%SMLNJ_HOME%\bin\sml" %1 %2 %3 %4 %5 %6 %7 %8 %9
    goto end

:manual
    "%SMLNJ_HOME%\bin\sml" %1 %2 %3 %4 %5 %6 %7 %8 %9

:end
