@echo off
rem $Id$

if "%1"=="-d" shift & set _opt_md=1

if "%1"=="-h" goto help
if "%1"=="" goto help

goto start

:help
    set _idrev=$Revision: 1.4 $
    set _iddate=$Date: 2004-09-24 00:42:38 $

    echo export cvs module in current directory to specified directory
    echo written by s.n.m.   version %_idrev:~11,-2%   date %_iddate:~7,-11%
    echo.
    echo syntax:
    echo    cvsexp [-d] target-dir
    echo options:
    echo    -d      create directory structure
    echo.
    echo notice: this program is distributed under FreeBSD license.

    set _idrev=
    set _iddate=
    goto end


:start
    for /f %%i in (cvs\root) do set _cvsroot=%%i
    for /f %%i in (cvs\repository) do set _cvsrep=%%i
    for %%i in ("%cd%") do set _cvscd=%%i

    set _s=%_cvsrep%
    set _cvsrep_base=
:findbase
    if "%_s%"=="" goto x_findbase
    set _c=%_s:~-1%
    if "%_c%"=="/" goto x_findbase
    if "%_c%"=="\" goto x_findbase
    set _cvsrep_base=%_c%%_cvsrep_base%
    set _s=%_s:~,-1%
    goto findbase
:x_findbase
    set _s=
    set _c=

    set _cvsdest=%1
    if "%_cvsdest:~-1%"==":" goto x_dest
    if "%_cvsdest:~-1%"=="\" goto x_dest
    set _cvsdest=%_cvsdest%\
:x_dest

    if not exist "%_cvsdest%" md "%_cvsdest%"
    pushd "%_cvsdest%" >nul
    if "%_opt_md%"=="1" (
        cvs -d "%_cvsroot%" export -r HEAD "%_cvsrep%"
    ) else (
        if not exist "%_cvsdest%%_cvsrep_base%" md "%_cvsdest%%_cvsrep_base%"
        cvs -d "%_cvsroot%" export -r HEAD -d "%_cvsrep_base%" "%_cvsrep%"
    )
    popd >nul


:end
    set _opt_md=
    set _cvsroot=
    set _cvsrep=
    set _cvsdest=
    set _cvscd=
