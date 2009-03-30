!include "..\..\lib\nsis\lapiota.nsh"
!cd ${LAPIOTA}

Name "Lapiota Java Development"
OutFile "${PACKOUT}\lapi-java.exe"
InstallDir ${LAPIOTA}

; Page custom...
Page instfiles

Section "Java Development Kit"
    !insertmacro DirRec $INSTDIR . ${jdk_rel}\bin
    !insertmacro DirRec $INSTDIR . ${jdk_rel}\jre
    !insertmacro DirRec $INSTDIR . ${jdk_rel}\lib
SectionEnd

Section "Java Extra Libraries"
    !insertmacro DirRec $INSTDIR . usr\lib\java
    !insertmacro DirRec $INSTDIR . usr\src\java
SectionEnd

Section "Jawin"
    !insertmacro DirRec $INSTDIR . ${jawin_rel}
SectionEnd

Section "Eclipse SDK"
   ;!insertmacro DirRec $INSTDIR . ${eclipse_rel}
    !insertmacro DirSub $INSTDIR . ${eclipse_rel}\*
    !insertmacro DirRec $INSTDIR . ${eclipse_rel}\dropins
    !insertmacro DirRec $INSTDIR . ${eclipse_rel}\features
    !insertmacro DirRec $INSTDIR . ${eclipse_rel}\links
    !insertmacro DirRec $INSTDIR . ${eclipse_rel}\links.disabled
    !insertmacro DirRec $INSTDIR . ${eclipse_rel}\p2
    !insertmacro DirRec $INSTDIR . ${eclipse_rel}\plugins
    !insertmacro DirRec $INSTDIR . ${eclipse_rel}\readme
SectionEnd

Section "Eclipse Extensions"
    !insertmacro DirRec $INSTDIR . usr\lib\eclipse
SectionEnd
