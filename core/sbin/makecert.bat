@echo off

    setlocal

    call findabc openssl
    set path=%_home%;%PATH%

    set makef=%LAPIOTA%\lib\mk\certs.mk

    make -f %makef% %*
