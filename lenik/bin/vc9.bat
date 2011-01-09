@echo off

    call findabc -r C:\lam\nt.util\abc.d mssdk

    rem Platform SDK
    set          INCLUDE=%_home%\include;%INCLUDE%
    set              LIB=%_home%\lib;%LIB%
    set             PATH=%_home%\bin;%PATH%

    call findabc -r C:\lam\nt.util\abc.d msvs-9

    rem Installs
    set        DevEnvDir=%_home%\Common7\IDE
    set     VCINSTALLDIR=%_home%\VC
    set     VSINSTALLDIR=%_home%

    rem Framework SDK
    set       _FRAMEWORK=C:\WINDOWS\Microsoft.NET\Framework
    set          LIBPATH=%_FRAMEWORK%\v3.5;%_FRAMEWORK%\v2.0.50727;%_home%\VC\ATLMFC\LIB;%_home%\VC\LIB;
    set             PATH=%_FRAMEWORK%\v3.5;%_FRAMEWORK%\v2.0.50727;%PATH%

    rem VC
    set          INCLUDE=%_home%\VC\ATLMFC\INCLUDE;%_home%\VC\INCLUDE;%INCLUDE%
    set              LIB=%_home%\VC\ATLMFC\LIB;%_home%\VC\LIB;%LIB%
    set             PATH=%_home%\Common7\IDE;%_home%\VC\BIN;%_home%\Common7\Tools;%_home%\VC\VCPackages;%PATH%
