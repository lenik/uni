@echo off
    rem $Id: em.bat,v 1.7 2007-08-24 10:32:22 lenik Exp $

    setlocal

    set _T=Emacs-%RANDOM%

    title %_T%
    rem sleep -m 500
    rem wm -v -hide %_T%

    wm -hide

    %cirk_home%\Emacs\bin\emacs %*

    wm -show