
@echo off

:loop1

if "%1"=="" goto end
type nul >%1
shift
goto loop1

:end
