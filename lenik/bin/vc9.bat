@echo off

    call findabc -r C:\lam\nt.util\abc.d visualstudio

    set DevEnvDir=%_home%\Common7\IDE
    set VCINSTALLDIR=%_home%\VC
    set VSINSTALLDIR=%_home%
    set WindowsSdkDir=%_home%\VC\PlatformSDK\

    set INCLUDE=%_home%\VC\ATLMFC\INCLUDE;%_home%\VC\INCLUDE;%_home%\VC\PlatformSDK\include;%INCLUDE%
    set LIB=%_home%\VC\ATLMFC\LIB;%_home%\VC\LIB;%_home%\VC\PlatformSDK\lib;%LIB%

    set _FRAMEWORK=C:\WINDOWS\Microsoft.NET\Framework
    set LIBPATH=%_FRAMEWORK%\v3.5;%_FRAMEWORK%\v2.0.50727;%_home%\VC\ATLMFC\LIB;%_home%\VC\LIB;
    set PATH=%_home%\Common7\IDE;%_home%\VC\BIN;%_home%\Common7\Tools;%_FRAMEWORK%\v3.5;%_FRAMEWORK%\v2.0.50727;%_home%\VC\VCPackages;%_home%\VC\PlatformSDK\bin;%PATH%
