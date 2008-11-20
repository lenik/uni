!include "..\..\lib\nsis\lapiota.nsh"
!cd ${LAPIOTA}

Name "Lapiota Java Development"
OutFile "${PACKOUT}\lapi-java.exe"
InstallDir ${LAPIOTA}

; Page custom...
Page instfiles

Section "Java Development Kit"
    !insertmacro SubDir $INSTDIR . ${jdk_rel}\bin
    !insertmacro SubDir $INSTDIR . ${jdk_rel}\jre
    !insertmacro SubDir $INSTDIR . ${jdk_rel}\lib
SectionEnd

Section "Java Extra Libraries"
    !insertmacro SubDir $INSTDIR . usr\lib\java
    !insertmacro SubDir $INSTDIR . usr\src\java
SectionEnd

Section "Jawin"
    !insertmacro SubDir $INSTDIR . ${jawin_rel}
SectionEnd

Section "Eclipse SDK"
   ;!insertmacro SubDir $INSTDIR . ${eclipse_rel}
    !insertmacro Files  $INSTDIR . ${eclipse_rel}\*
    !insertmacro SubDir $INSTDIR . ${eclipse_rel}\dropins
    !insertmacro SubDir $INSTDIR . ${eclipse_rel}\features
    !insertmacro SubDir $INSTDIR . ${eclipse_rel}\links
    !insertmacro SubDir $INSTDIR . ${eclipse_rel}\links.disabled
    !insertmacro SubDir $INSTDIR . ${eclipse_rel}\p2
    !insertmacro SubDir $INSTDIR . ${eclipse_rel}\plugins
    !insertmacro SubDir $INSTDIR . ${eclipse_rel}\readme
SectionEnd

Section "Eclipse Extensions"
    !insertmacro SubDir $INSTDIR . usr\lib\eclipse
SectionEnd
