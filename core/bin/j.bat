
@echo off


if not "%OS%"=="" if not "%OS%"=="Windows_NT" goto error_version

goto j_begin

:error_version
	echo The script doesn't support OS other than Windows NT.
	echo The Windows NT includes Windows 2000/XP/2003.
	echo.
	goto show_version

goto exit_program


:j_begin
if not "%1"=="-javac" goto fi_java_wrapper
	if not "%J_QUIET%"=="1" echo javac %J_JAVAC_ARGS% %2 %3 %4 %5 %6 %7 %8 %9
	javac %J_JAVAC_ARGS% %2 %3 %4 %5 %6 %7 %8 %9
	goto exit_program
:fi_java_wrapper


if not "%1"=="-addcp" goto fi_add_classpath
	if not "%2"=="" (
		echo add item [%2]:
		set CLASSPATH=%CLASSPATH%;%2
	) else echo empty item is skipped.
	goto exit_program
:fi_add_classpath





REM *************************************************************
REM **
REM ** Parsing Arguments
REM **
REM *************************************************************

set J_EXEC_FILE=%0
:pre_loop


if not "%1"=="-v" goto fi_version
:show_version
	echo Author: Danci.Z      Labja Software Corp. (cvi)
	echo (C) Copyright CHINA, 2003.  All rights reserved.
	shift
	if "%1"=="" goto end
	goto pre_loop
:fi_version

if not "%1"=="-c" goto fi_compile
	set J_COMPILE_ONLY=1
	shift
	goto pre_loop
:fi_compile

if not "%1"=="-i" goto fi_init
	if "%JAVA_HOME%"=="" SET JAVA_HOME=c:\hejmo\java\j2se
	if "%CATALINA_HOME%"=="" SET CATALINA_HOME=c:\hejmo\java\tomcat5
	if "%CATALINA_BASE%"=="" SET CATALINA_BASE=c:\hejmo\java\tomcat5
	if "%JSYS_HOME%"=="" (
		set JSYS_HOME=X:\jos\common
	) else (
		REM avoid of appending to classpath.
		set CLASSPATH=
	)

	if "%CLASSPATH%"=="" (set CLASSPATH=.) else set CLASSPATH=%CLASSPATH%;.
	set CLASSPATH=%CLASSPATH%;X:\java\proj\out
	set CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\tools.jar

	REM for %%i in (%JSYS_HOME%\jar\*) do call j -addcp %%~fi
	REM 	REM set CLASSPATH=%CLASSPATH%;%%~fi

	echo generating script...
	if "%TEMP%"=="" (
		mkdir c:\temp
		SET TEMP=C:\temp
	)
	set J_BATCH_TEMP=%TEMP%\~$j-scp$.bat

	echo @echo off >%J_BATCH_TEMP%
	echo echo running generated script...>>%J_BATCH_TEMP%
	echo set CLASSPATH=.>>%J_BATCH_TEMP%

	pushd c:\hejmo\java\jar
		for /R %%i in (*) do (
			if "%%~xi"==".jar" (
				echo set CLASSPATH=%%CLASSPATH%%;%%~fi>>%J_BATCH_TEMP%
			)
		)
	popd

	pushd %CATALINA_HOME%\common\lib
		for /R %%i in (*) do (
			if "%%~xi"==".jar" (
				echo set CLASSPATH=%%CLASSPATH%%;%%~fi>>%J_BATCH_TEMP%
			)
		)
	popd

	set J_WEBAPP=X:\java\proj\J2eeGarbage\NewTams\WebApp
	if exist "%J_WEBAPP%\WEB-INF\lib" (
		pushd "%J_WEBAPP%\WEB-INF\lib"
			for /R %%i in (*) do (
				if "%%~xi"==".jar" (
					echo set CLASSPATH=%%~fi;%%CLASSPATH%%>>%J_BATCH_TEMP%
				)
			)
		popd

		echo set CLASSPATH=%J_WEBAPP%\WEB-INF\classes;%%CLASSPATH%%>>%J_BATCH_TEMP%
	)

	call %J_BATCH_TEMP%
	rem del %J_BATCH_TEMP%
	set J_BATCH_TEMP=

	SET PATH=%JAVA_HOME%\bin;%CATALINA_HOME%\bin;C:\hejmo\java\.dok;%PATH%

	call %JSYS_HOME%\..\bin\initsh.bat

	shift
	if "%1"=="" goto end
	goto pre_loop
:fi_init


set J_JAVAC_ARGS=
if not "%1"=="-q" goto fi_quiet
	set J_JAVAC_ARGS=
	shift
	goto pre_loop
:fi_quiet


if not "%1"=="-d" goto fi_debug
	shift
	echo on
	goto pre_loop
:fi_debug


if not "%1"=="" goto fi_syntax
	@echo off
	echo %J_EXEC_FILE% -v -c -i -q -d { * (or) d (or) filename.java (or) classname }
	echo option: Verbose, Compile-only, Init, Quiet, Debug
	echo 	*		compile and run all java classes
	echo 	d		delete all classes (del *.class)
	echo 	filename.java	compile and run the specified java class
	echo 	classname	compile and run the specified java class
	goto end
:fi_syntax



REM *************************************************************
REM **
REM ** Main Program
REM **
REM *************************************************************

REM try "j -i" once, if JSYS_HOME isn't set.
	if "%JSYS_HOME%"=="" if not "%JSYS_LAZY_INIT%"=="BUSY" (
		SET JSYS_LAZY_INIT=BUSY
		call %0 -i
		SET JSYS_LAZY_INIT=
	) else echo It is strongly recommended that you install JSYS_HOME.

if not "%1"=="d" goto fi_delete_all
	if exist *.class del *.class
	goto end
:fi_delete_all

if not "%1"=="*" goto fi_wild
	REM **** for %%i in (%1.class) do java %%~ni

	REM call %J_EXEC_FILE% -javac %1.java
	echo javac %J_JAVAC_ARGS% %1.java
	javac %J_JAVAC_ARGS% %1.java
	goto end
:fi_wild


if not exist "%1" goto fi_full_name
	REM for %%i in (%1) do if "%%~xi"==".java" call %J_EXEC_FILE% -javac %%i
	for %%i in (%1) do if "%%~xi"==".java" (
		echo javac %J_JAVAC_ARGS% %%i
		javac %J_JAVAC_ARGS% %%i
	)
	if "%J_COMPILE_ONLY%"=="1" goto fn_skip_run
	for %%i in (%~n1.class) do java %%~ni %2 %3 %4 %5 %6 %7 %8 %9
:fn_skip_run
	goto end
:fi_full_name


	set J_CRIT=
	if exist "*%1*.java" set J_CRIT="*%1*"
	if exist "%1*.java" set J_CRIT="%1*"
	if exist "%1.java" set J_CRIT="%1"
if "%J_CRIT%"=="" goto fi_fuzzy
	REM for %%i in (%1.java) do call %J_EXEC_FILE% -javac %%i
	for %%i in (%J_CRIT%.java) do (
		echo javac %J_JAVAC_ARGS% %%i
		javac %J_JAVAC_ARGS% %%i
	)
	if "%J_COMPILE_ONLY%"=="1" goto ne_skip_run
	for %%i in (%J_CRIT%.class) do java %%~ni %2 %3 %4 %5 %6 %7 %8 %9
:ne_skip_run
	goto end
:fi_fuzzy
	set J_CRIT=



:default
	echo "%1" or "%1.java" not existed!
	goto end


:end
	set J_COMPILE_ONLY=
	set J_JAVAC_ARGS=
	set J_EXEC_FILE=

:exit_program
