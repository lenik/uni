@echo off
rem $Id$

rem ?? who wrote this ??


if "%1"=="/D" goto SetParameters
if "%1"=="/d" goto SetParameters
goto Start
:SetParameters
set ANY_PARAMETERS=%1
shift

:Start
if "%1"=="" goto Usage

if not "%2"=="" goto LinkPatterns
:WithoutPattern
set ANY_PATTERN=*.*
goto DoCmd

:Usage
echo %0 [files-pattern] do-cmd
goto End

:LinkPatterns
set ANY_PATTERN=%ANY_PATTERN% %1
shift
if not "%2"=="" goto LinkPatterns

:DoCmd
for %ANY_PARAMETERS% %%i in (%ANY_PATTERN%) do %1 %%i

:End
set ANY_PATTERN=
set ANY_PARAMETERS=
