@echo off
rem $Id: zero.bat,v 1.2 2004-09-22 08:39:11 dansei Exp $

:loop1
    if "%1"=="" goto end
    type nul >%1
    shift
    goto loop1

:end
