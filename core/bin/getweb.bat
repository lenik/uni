@echo off
    rem $Id: getweb.bat,v 1.4 2005-06-19 08:28:16 dansei Exp $

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


	echo so -r=%gw_host% -s="GET %gw_arg%\n\n"
	call so -r=%gw_host% -s="GET %gw_arg%\n\n" -g=print
	goto end

:help
	echo getweb hostname querystring
	goto end

:end
	set gw_host=
	set gw_arg=
