@echo off
    rem $Id: getweb.bat,v 1.2 2004-09-22 08:39:10 dansei Exp $

    rem http-get/post, powered by sockonce.


	if "%1"=="" goto help

	set gw_host=%1
	set gw_arg=/
	if "%2"=="" goto x_arg
	set gw_arg=%2
:nextarg
	shift
	if "%2"=="" goto x_arg
	set gw_arg=%gw_arg% %2
	goto nextarg
:x_arg


	echo sockonce -r=%gw_host% -s="GET %gw_arg%\n\n"
	sockonce -r=%gw_host% -s="GET %gw_arg%\n\n"
	goto end

:help
	echo getweb hostname querystring
	goto end

:end
	set gw_host=
	set gw_arg=
