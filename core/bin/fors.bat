@ECHO OFF

	SET _F_INDEX=
	IF "%1"=="/0" GOTO index_0
	IF "%1"=="/1" GOTO index_1
	IF "%1"=="/2" GOTO index_2
	IF "%1"=="/3" GOTO index_3
	IF "%1"=="/4" GOTO index_4
	IF "%1"=="/5" GOTO index_5
	IF "%1"=="/6" GOTO index_6
	IF "%1"=="/7" GOTO index_7
	IF "%1"=="/8" GOTO index_8
	IF "%1"=="/9" GOTO index_9
	SET _F_INDEX=A
	GOTO main_args

:help
	ECHO Fors Utility, Version 1
	ECHO.
	ECHO Syntax
	ECHO 	fors /0 names-exec arguments-list
	ECHO 	fors [/1] names-pattern [name-exec arguments-list]
	ECHO 	fors /2-9 names-pattern [name-exec arguments-list]
	ECHO 	(the default name-exec is 'echo')
	ECHO.
	ECHO Author
	ECHO 	Danci.Z, March 2004.  E-mail: jljljjl@yahoo.com
	GOTO end

:index_9
	SET _F_INDEX=%_F_INDEX%A
:index_8
	SET _F_INDEX=%_F_INDEX%A
:index_7
	SET _F_INDEX=%_F_INDEX%A
:index_6
	SET _F_INDEX=%_F_INDEX%A
:index_5
	SET _F_INDEX=%_F_INDEX%A
:index_4
	SET _F_INDEX=%_F_INDEX%A
:index_3
	SET _F_INDEX=%_F_INDEX%A
:index_2
	SET _F_INDEX=%_F_INDEX%A
:index_1
	SET _F_INDEX=%_F_INDEX%A
:index_0
	SHIFT

:main_args
	IF "%1"=="" GOTO help

	SET _F_NAMES=%1
	IF "%2"=="" (
		SET _F_CMD=echo
	) ELSE (
		SET _F_CMD=%2
	)
	SHIFT
	SHIFT

	SET _F_ARGS_PRE=
	SET _F_ARGS_POST=
	SET _F_INDEX_SEARCH=A
	SET _F_INDEX_FLAG=toPre
:args_loop
	IF "%1"=="" GOTO args_end
	IF "%_F_INDEX_SEARCH%"=="%_F_INDEX%" (
		SET _F_INDEX_FLAG=toPost
	)
	SET _F_INDEX_SEARCH=%_F_INDEX_SEARCH%A
	IF "%_F_INDEX_FLAG%"=="toPre" (
		IF "%_F_ARGS_PRE%"=="" (
			SET _F_ARGS_PRE=%1
		) ELSE (
			SET _F_ARGS_PRE=%_F_ARGS_PRE% %1
		)
	) ELSE (
		IF "%_F_ARGS_POST%"=="" (
			SET _F_ARGS_POST=%1
		) ELSE (
			SET _F_ARGS_POST=%_F_ARGS_POST% %1
		)
	)
	SHIFT
	GOTO args_loop

:args_end
	IF "%_F_INDEX%"=="" (
		FOR %%i IN (%_F_NAMES%) DO (
			%%i %_F_CMD% %_F_ARGS_PRE% %_F_ARGS_POST%
		)
	) ELSE (
		FOR %%i IN (%_F_NAMES%) DO (
			IF "%_F_ARGS_PRE%"=="" (
				%_F_CMD% %%i %_F_ARGS_POST%
			) ELSE (
				%_F_CMD% %_F_ARGS_PRE% %%i %_F_ARGS_POST%
			)
		)
	)

:end
	SET _F_NAMES=
	SET _F_CMD=
	SET _F_ARGS_PRE=
	SET _F_ARGS_POST=
	SET _F_INDEX=
	SET _F_INDEX_SEARCH=
	SET _F_INDEX_FLAG=
