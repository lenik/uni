@echo off
    rem $Id: em.bat,v 1.6 2007-04-30 15:24:39 lenik Exp $

    setlocal

    set _T=Emacs-%RANDOM%

    title %_T%
    rem sleep -m 500
    rem wm -v -hide %_T%

    wm -hide

    C:\.cirkonstancoj\Emacs\bin\emacs %*

    wm -show