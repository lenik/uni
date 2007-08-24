@echo off
    rem $Id: em.bat,v 1.8 2007-08-24 11:44:24 lenik Exp $

    setlocal

    set _T=Emacs-%RANDOM%

    title %_T%
    rem sleep -m 500
    rem wm -v -hide %_T%

    wm -hide

    call findabc emacs bin
    emacs %*

    wm -show
