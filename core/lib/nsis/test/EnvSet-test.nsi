; !define DEBUG 1

!include "..\lapiota.nsh"

Name "lapiota-envset-test"
OutFile "$%DIST_HOME%\lapiota-envset-test.exe"
InstallDir $%TEMP%

; Page custom...
Page instfiles

Section "Test"
    !insertmacro EnvSet "test" "test"
SectionEnd