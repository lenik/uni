
@ECHO OFF

IF "%1"=="" GOTO SYNTAX
IF "%2"=="" MCopy %1 .
GOTO STEP

:SYNTAX
ECHO	Mcopy Source-dir Target-dir [-a]
ECHO.
ECHO	Options:
ECHO	-a		Verbose output
ECHO.
ECHO	Environment Variables:
ECHO	MCOPY_EX	A directory contains excluded filenames, separated by :
ECHO.
ECHO	(C)Copyright 2004
ECHO	Danci.Z (dansei@163.com?subject=aad. ...)

GOTO END

:STEP
	SET _PWD=%1
	SET _PWD=%_PWD:~0,55%

	IF EXIST %2 (
		IF "%3"=="-a" ECHO NCY*			%_PWD%
	) ELSE (
		ECHO DIR*%_PWD%
		MD %2
	)

REM	1) add each file
	FOR %%f IN (%1\*) DO (
		SET _Skip=
		SET _BaseName=%%~nf%%~xf
		IF "!_BaseName:~3,100!"=="" (
			SET _DispName=!_BaseName!
		) ELSE (
			IF "!_BaseName:~11,100!"=="" (
				SET _DispName=!_BaseName!
			) ELSE (
				SET _DispName=!_BaseName!
			)
		)
		IF NOT "%MCOPY_EX%"=="" (
			IF EXIST "%MCOPY_EX%"\"!_BaseName!" (
				REM Matches someone in the Excluded names list
				SET _Skip=1
				IF "%3"=="-a" ECHO FLT			!_DispName!	%_PWD%
			)
		)
		IF "!_Skip!"=="" (
			IF EXIST %2\"!_BaseName!" (
				SET _MCOPY_T1=%%~tf
				SET _MCOPY_T2=_Magic_XYZ_
				FOR %%i IN (%2\"!_BaseName!") DO SET _MCOPY_T2=%%~ti
				IF "!_MCOPY_T1!"=="!_MCOPY_T2!" (
					REM The same date
					SET _Skip=1
					IF "%3"=="-a" ECHO SKP			!_DispName!	%_PWD%
				) ELSE (
					FC "%%f" %2\"!_BaseName!" /b >nul
					IF NOT ERRORLEVEL 1 (
						REM Not changed yet
						SET _Skip=1
						IF "%3"=="-a" ECHO NCY			!_BaseName!	%_PWD%
					)
				)
			)
		)
		IF "!_Skip!"=="" (
			IF EXIST %2\"!_BaseName!" (
				ECHO UPD !_DispName!	%_PWD%
				COPY /Y "%%f" %2\"!_BaseName!" >nul
			) ELSE (
				ECHO ADD !_DispName!	%_PWD%
				COPY /Y "%%f" %2\"!_BaseName!" >nul
			)
		)
	)
	SET _Skip=
	SET _MCOPY_T1=
	SET _MCOPY_T2=
	SET _PWD=

REM	2) remove extra files in target directory
	FOR %%f IN (%2\*) DO (
		SET _Skip=
		SET _BaseName=%%~nf%%~xf
		IF "!_BaseName:~3,100!"=="" (
			SET _DispName=!_BaseName!
		) ELSE (
			IF "!_BaseName:~11,100!"=="" (
				SET _DispName=!_BaseName!
			) ELSE (
				SET _DispName=!_BaseName!
			)
		)

		IF NOT EXIST %1\!_BaseName! (
			IF NOT "%MCOPY_EX%"=="" IF EXIST "%MCOPY_EX%"\"!_BaseName!" SET _Skip=1
			IF "!_Skip!"=="" (
				ECHO DRP !_DispName!	%_PWD%
				DEL %2\"!_BaseName!" >nul
			)
		)
	)

REM	3) remove extra directories in target directory
	FOR /d %%d IN (%2\*) DO (
		SET _Skip=
		SET _BaseName=%%~nd%%~xd
		IF "!_BaseName:~3,100!"=="" (
			SET _DispName=!_BaseName!
		) ELSE (
			IF "!_BaseName:~11,100!"=="" (
				SET _DispName=!_BaseName!
			) ELSE (
				SET _DispName=!_BaseName!
			)
		)

		IF NOT EXIST %1\!_BaseName! (
			IF NOT "%MCOPY_EX%"=="" IF EXIST "%MCOPY_EX%"\"!_BaseName!" SET _Skip=1
			IF "!_Skip!"=="" (
				ECHO DRP*!_DispName!	%_PWD%
				RD /S /Q %2\"!_BaseName!" >nul
			)
		)
	)
	SET _PWD=

REM	4) add each sub-directory
	FOR /d %%d IN (%1\*) DO (
		SET _Skip=
		SET _BaseName=%%~nd%%~xd
		IF "!_BaseName:~3,100!"=="" (
			SET _DispName=!_BaseName!
		) ELSE (
			IF "!_BaseName:~11,100!"=="" (
				SET _DispName=!_BaseName!
			) ELSE (
				SET _DispName=!_BaseName!
			)
		)

		IF NOT "%MCOPY_EX%"=="" (
			IF EXIST "%MCOPY_EX%"\"!_BaseName!" (
				REM Matches someone in the Excluded names list
				SET _Skip=1
				IF "%3"=="-a" ECHO FLT*			!_DispName!
			)
		)
		IF "!_Skip!"=="" (
			CALL MCopy "%%d" %2\"!_BaseName!" %3
		)

		SET _Empty=1
		FOR %%i IN (%2\"!_BaseName!"\*) DO SET _Empty=
		FOR /d %%i IN (%2\"!_BaseName!"\*) DO SET _Empty=
		IF "!_Empty!"=="1" RD %2\"!_BaseName!"
	)
	SET _Empty=

	SET _BaseName=
	SET _DispName=
	GOTO END

:END
