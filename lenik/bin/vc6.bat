@echo off

    call findabc -r C:\lam\nt.util\abc.d msvs-6

    set INCLUDE=%_home%\VC98\INCLUDE;%_home%\VC98\ATL\INCLUDE;%_home%\VC98\MFC\INCLUDE;%INCLUDE%
    set LIB=%_home%\VC98\LIB;%_home%\VC98\MFC\LIB;%LIB%
    set PATH=%_home%\Common\IDE\IDE98;%_home%\Common\MSDEV98\BIN;%_home%\VC98\BIN;%PATH%
