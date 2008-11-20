!include "..\..\lib\nsis\lapiota.nsh"
!cd ${LAPIOTA}

Name "Lapiota Core"
OutFile "${PACKOUT}\lapi-core.exe"
InstallDir ${LAPIOTA}

; Page custom...
Page instfiles

Section "Core Files"
    !insertmacro Files  $INSTDIR . .
    !insertmacro Files  $INSTDIR . abc.d\bin
    !insertmacro SubDir $INSTDIR . bin
    !insertmacro Files  $INSTDIR . etc
    !insertmacro SubDir $INSTDIR . etc\conf.d
    !insertmacro SubDir $INSTDIR . etc\cygwin
    !insertmacro SubDir $INSTDIR . etc\db
    !insertmacro SubDir $INSTDIR . etc\install.d
    !insertmacro SubDir $INSTDIR . etc\paths
    !insertmacro SubDir $INSTDIR . etc\postinstall
    !insertmacro SubDir $INSTDIR . etc\preremove
    !insertmacro SubDir $INSTDIR . etc\profile.d
    !insertmacro SubDir $INSTDIR . etc\rc
    !insertmacro SubDir $INSTDIR . etc\rc.d
    !insertmacro SubDir $INSTDIR . etc\skel
    !insertmacro SubDir $INSTDIR . etc\startup.d
    !insertmacro SubDir $INSTDIR . home
    !insertmacro SubDir $INSTDIR . lib
   ;!insertmacro SubDir $INSTDIR . opt
    !insertmacro SubDir $INSTDIR . test
    !insertmacro SubDir $INSTDIR . usr\bin
    !insertmacro SubDir $INSTDIR . usr\include
    !insertmacro SubDir $INSTDIR . usr\src\mod
    !insertmacro SubDir $INSTDIR . xt
SectionEnd

Section "System Administration Scripts"
    !insertmacro SubDir $INSTDIR . sbin
SectionEnd

Section "Local Wares"
    !insertmacro SubDir $INSTDIR . local\bin
    !insertmacro SubDir $INSTDIR . local\dos
    !insertmacro SubDir $INSTDIR . local\lib
SectionEnd

Section "System Utilities"
    !insertmacro SubDir $INSTDIR . ${7zip_rel}
    !insertmacro SubDir $INSTDIR . ${autohotkey_rel}
    !insertmacro SubDir $INSTDIR . ${autoit_rel}
    !insertmacro SubDir $INSTDIR . ${bochs_rel}
    !insertmacro SubDir $INSTDIR . ${npp_rel}
SectionEnd
