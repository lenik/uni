@echo off

rem how to add .php to IIS?
rem     .php
rem     .py
rem     .pyc
rem     .pl
rem     .p

call findabc mysql/bin
mysqld --install-manual mysql.lapi

call findabc httpd/bin
httpd -f %LAPIOTA%\etc\conf.d\httpd\httpd.conf -k install -n httpd
