@echo off

    setlocal

    call findabc openssl bin

    set makef=%LAPIOTA%\lib\mk\certs.mk

    make -f %makef% %*
