

@echo off


lc /nologo user32::MessageBoxA(0, 'The Most Clever Tool for Initializing TOMCAT!!!', 'Author: Danci.Z', 0)

SET JAVA_HOME=C:\Program Files\j2sdk_nb\j2sdk1.4.2
SET CATALINA_HOME=C:\Program Files\j2sdk_nb\netbeans3.5.1\tomcat406
SET CATALINA_BASE=C:\Program Files\j2sdk_nb\netbeans3.5.1\tomcat406


SET CLASSPATH=.;X:\java\all.jar
SET CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\tools.jar
SET CLASSPATH=%CLASSPATH%;%CATALINA_HOME%\common\lib\xerces.jar

SET PATH=%JAVA_HOME%\bin;%CATALINA_HOME%\bin;%PATH%
