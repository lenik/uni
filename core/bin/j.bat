@echo off
    rem $Id: j.bat,v 1.4 2004-11-24 03:38:14 dansei Exp $

    if "%OS%"=="Windows_NT" goto winnt

	echo The script doesn't support OS other than Windows NT.
	echo The Windows NT includes Windows 2000/XP/2003.
	echo.
	goto help


:winnt
    if "%1"=="-v" (
        set _VERBOSE=1
        shift
        goto winnt
        )
    if "%1"=="-c" (
        set _COMPILE=1
        shift
        goto winnt
        )
    if "%1"=="-i" goto init
    if "%1"=="-p" goto classpath
    if "%1"=="-h" goto help

    goto parse


:help
	echo [J] Javashell      Java utility for environment usage
	echo Written by Danci.Z, S-FIA / TC  All rights reserved.
	echo.
	echo Syntax:
	echo    J [options] [items]
	echo Options:
	echo    -h          Print this help page
	echo    -i          Initilize shell environment
	echo    -c          Compile only
	echo    -p [path]   Print classpath or add path to classpath
	echo    -v          Verbose mode
	echo Items:
	echo    *           Compile and run all java classes
	echo    d           Delete all classes (del *.class)
	echo    filen.java  compile and run the specified java class
	echo    ClassName   compile and run the specified java class
	goto cleanup


:compile
    shift
	if "%_VERBOSE%"=="1" echo javac %_JARGS% %1 %2 %3 %4 %5 %6 %7 %8 %9
	javac %_JARGS% %1 %2 %3 %4 %5 %6 %7 %8 %9
	goto cleanup


:classpath
    if "%2"=="" (
        echo %CLASSPATH%|tr ";" "\n"
        goto cleanup
        )
:add
    shift
    if not "%1"=="" (
		if "%_VERBOSE%"=="1" echo add "%1" to classpath
		set CLASSPATH=%CLASSPATH%;%1
		goto add
	)
	goto cleanup


:init
    if not "%JOS_HOME%"=="" goto cleanup

    set JOS_HOME=X:\jos
	if "%JAVA_HOME%"=="" SET JAVA_HOME=c:\varoj\java\j2se-1_4
	if "%CATALINA_HOME%"=="" SET CATALINA_HOME=c:\varoj\java\tomcat5
	if "%CATALINA_BASE%"=="" SET CATALINA_BASE=c:\varoj\java\tomcat5

    for /D %%i in (%JOS_HOME%\usr\*) do (
        set CLASSPATH=!CLASSPATH!;%%i
        )
    for /F %%i in ('dir %JOS_HOME%\lib\*.jar /s/b') do (
        set CLASSPATH=!CLASSPATH!;%%i
        )
	set CLASSPATH=.;X:\jos\var\out;%JAVA_HOME%\lib\tools.jar;%JAVA_HOME%\lib\dt.jar;%CLASSPATH%

	SET PATH=%JOS_HOME%\bin;%JOS_HOME%\usr\bin;%JAVA_HOME%\bin;%CATALINA_HOME%\bin;C:\varoj\java\.dok;%PATH%
    goto end


:parse
    set _J=%0

:item
    if "%1"=="" goto item_end
    if "%1"=="d" (
        if exist *.class del *.class
        shift
        goto item
        )
    if "%1"=="*" (
        if "%_VERBOSE%"=="1" echo javac %_JARGS% *.java
        javac %_JARGS% *.java
        shift
        goto item
        )
    if exist "%1" (
	for %%i in (%1) do if "%%~xi"==".java" (
		if "%_VERBOSE%"=="1" echo javac %_JARGS% %%i
		javac %_JARGS% %%i
	    )
        if not "%_COMPILE%"=="1" (
            for %%i in (%~n1.class) do java %%~ni %2 %3 %4 %5 %6 %7 %8 %9
            )
        goto cleanup
        )

    set _MATCH=
	if exist "*%1*.java" set _MATCH="*%1*"
	if exist "%1*.java" set _MATCH="%1*"
	if exist "%1.java" set _MATCH="%1"
    if "%_MATCH%"=="" (
        echo "%1" or "%1.java" not existed
        goto cleanup
        )
	for %%i in (%_MATCH%.java) do (
		if "%_VERBOSE%"=="1" echo javac %_JARGS% %%i
		javac %_JARGS% %%i
	    )
	if not "%_COMPILE%"=="1" (
        for %%i in (%_MATCH%.class) do java %%~ni %2 %3 %4 %5 %6 %7 %8 %9
        )

:item_end


:cleanup
	set _VERBOSE=
	set _COMPILE_ONLY=
	set _JARGS=
	set _J=
    set _MATCH=

:end

