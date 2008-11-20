!include "..\..\lib\nsis\lapiota.nsh"
!define USERNAME $%USERNAME%
!define HOME $%HOME%
!cd ${HOME}

Name "Lapiota User Package (${USERNAME})"
OutFile "${PACKOUT}\lapi-user-${USERNAME}.exe"
InstallDir ${HOME}

; Page custom...
Page instfiles

Section "User Files"
    !insertmacro Files  $INSTDIR . .
    !insertmacro SubDir $INSTDIR . bin
    !insertmacro SubDir $INSTDIR . etc
SectionEnd
