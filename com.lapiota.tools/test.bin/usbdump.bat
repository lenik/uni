@echo off

set prov=PKCS11://C:/lapiota/local/lib/ngp11v211.dll#*?name=ft11

certdump -p %prov% PKCS11://kl@
