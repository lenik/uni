@echo off
if "%1"=="" goto error
if "%2"=="" goto error
if "%3"=="" goto error
set pre=
if "%4"=="/t" set pre=echo
rem FReplS files source dest
wh %1 [%pre% frepl %%2 %2 %3 *]
goto end
:error
echo %0 files(as ?,*) source dest
:end
