@echo off

set prov=PKCS11://C:/lapiota/local/lib/ngp11v211.dll#*?name=ft11
set p12=PKCS12://123@certs/test.p12

:: java.lang.UnsupportedOperationException: trusted certificates may only be set by token initialization application at sun.security.pkcs11.P11KeyStore.engineSetEntry
certimp -vp%prov% -tPKCS11://kl@ %p12%#123@test
