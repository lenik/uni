@ECHO OFF

SET _READONLY=
IF "%1"=="-O" (
	SET _READONLY=1
	SHIFT
)

IF "%1"=="" GOTO Help
GOTO Start

:Help
	ECHO Apply [-O] [-R base-dir] RE-script file-pattern
	ECHO.
	ECHO Options:
	ECHO	-O work in read-only mode
	ECHO	-R apply to all files within base-dir recursively.
	GOTO EndH

:Start
	SET _FP=*.*
	IF NOT "%_FP%"=="!_FP!" (
		ECHO You must enable DelayedExpansion to run this program.
		ECHO.
		ECHO Registry path:
		ECHO 	HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Command Processor\DelayedExpansion
		GOTO End0
	)

	IF "%1"=="-R" (
		IF "%3"=="" GOTO Help
		SET _RE=%3
		IF NOT "%4"=="" SET _FP=%4
		ECHO #Applying files list >%TMP%\apply.lst
		FOR /R %2 %%f IN (!_FP!) DO (
			ECHO %%f>>%TMP%\apply.lst
		)
	) ELSE (
		IF "%1"=="" GOTO Help
		SET _RE=%1
		IF NOT "%2"=="" SET _FP=%2
		ECHO #Applying files list >%TMP%\apply.lst
		FOR %%f IN (!_FP!) DO (
			ECHO %%f>>%TMP%\apply.lst
		)
	)
	SET _CNT_SKIPPED=0
	SET _CNT_WRITTEN=0
	FOR /F "eol=# delims=;" %%f IN (%TMP%\apply.lst) DO (
		CALL :do_apply "%_RE%" "%%f"
	)
	DEL /Q %TMP%\apply.lst

	ECHO.
	ECHO Total written %_CNT_WRITTEN% files, skipped %_CNT_SKIPPED% files.
	ECHO.

	SET _CNT_SKIPPED=
	SET _CNT_WRITTEN=

	GOTO End0

:do_apply
	SET _OUT_TMP=%~dpn2.tmp%~x2
	SET _APP_MODEL=sed
	SET _EXEC_AGENT=%1
	IF "%~x1"==".p" SET _APP_MODEL=perl
	IF "%~x1"==".pl" SET _APP_MODEL=perl
	IF "%~x1"==".P" SET _APP_MODEL=perl
	IF "%~x1"==".PL" SET _APP_MODEL=perl
	IF "%~x1"==".bat" SET _APP_MODEL=shell
	IF "%~x1"==".BAT" SET _APP_MODEL=shell
	IF "%~x1"==".cmd" SET _APP_MODEL=shell
	IF "%~x1"==".CMD" SET _APP_MODEL=shell
	IF EXIST "%1" (
		IF "%_APP_MODEL%"=="sed" (
			IF "%_READONLY%"=="1" (
				SET _EXEC_AGENT=sed -n -f %1
			) ELSE (
				SET _EXEC_AGENT=sed -f %1
			)
		)
		IF "%_APP_MODEL%"=="perl"	SET _EXEC_AGENT=perl %1
		IF "%_APP_MODEL%"=="shell"	SET _EXEC_AGENT=call %1
	)
	IF "%_READONLY%"=="1" (
		ECHO File %~f2...
		%_EXEC_AGENT% %2
	) ELSE (
		%_EXEC_AGENT% %2 >"%_OUT_TMP%"
		FC %2 "%_OUT_TMP%" /b >NUL
		IF ERRORLEVEL 1 (
			REM Changed, save-back.
			ECHO Written to %~f2
			move /y "%_OUT_TMP%" %2
			SET /A _CNT_WRITTEN=!_CNT_WRITTEN!+1
		) ELSE (
			ECHO Skipped %~f2
			DEL /q "%_OUT_TMP%"
			SET /A _CNT_SKIPPED=!_CNT_SKIPPED!+1
		)
	)
	SET _OUT_TMP=
	GOTO End

:End0
	SET _FP=
	SET _RE=

:EndH
	SET _READONLY=

:End
