@echo off

SET TE_CMDLINE=

:l_Collect
	IF "%1"=="" GOTO l_Test

	SET TE_CMDLINE=%TE_CMDLINE% %1
	SHIFT
	GOTO l_Collect

:l_Test
	%TE_CMDLINE%

	ECHO The ErrorLevel: %ERRORLEVEL%
