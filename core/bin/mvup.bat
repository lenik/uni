@echo off

set mvup_dir=..

:next
if "%1"=="" goto end
if "%1"=="-c" goto create_dir
if "%1"=="-t" goto set_dir
if "%1"=="-s" goto do_shell
if exist %1 move %1 %mvup_dir%
shift
goto next

:create_dir
if not exist %2 mkdir %2
shift
shift
goto next

:set_dir
set mvup_dir=%2
shift
shift
goto next

:do_shell
%2 %3
shift
shift
shift
goto next

:end
set mvup_dir=
