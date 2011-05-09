!include "..\..\lib\nsis\lapiota.nsh"
!cd ${LAPIOTA}

Name "Lapiota Core"
OutFile "${PACKOUT}\lapi-core.exe"
InstallDir ${LAPIOTA}

; Page custom...
Page instfiles

Section "Core Files"
    !insertmacro DirSub $INSTDIR . .
    !insertmacro DirSub $INSTDIR . abc.d\bin
    !insertmacro DirRec $INSTDIR . bin
    !insertmacro DirSub $INSTDIR . etc
    !insertmacro DirRec $INSTDIR . etc\conf.d
    !insertmacro DirRec $INSTDIR . etc\cygwin
    !insertmacro DirRec $INSTDIR . etc\db
    !insertmacro DirRec $INSTDIR . etc\install.d
    !insertmacro DirRec $INSTDIR . etc\paths
    !insertmacro DirRec $INSTDIR . etc\postinstall
    !insertmacro DirRec $INSTDIR . etc\preremove
    !insertmacro DirRec $INSTDIR . etc\profile.d
    !insertmacro DirRec $INSTDIR . etc\rc
    !insertmacro DirRec $INSTDIR . etc\rc.d
    !insertmacro DirRec $INSTDIR . etc\skel
    !insertmacro DirRec $INSTDIR . etc\startup.d
    !insertmacro DirRec $INSTDIR . home
    !insertmacro DirRec $INSTDIR . lib
   ;!insertmacro DirRec $INSTDIR . opt
    !insertmacro DirRec $INSTDIR . test
    !insertmacro DirRec $INSTDIR . usr\bin
    !insertmacro DirRec $INSTDIR . usr\include
    !insertmacro DirRec $INSTDIR . usr\lib\perl5
    !insertmacro DirRec $INSTDIR . usr\src\mod
    !insertmacro DirRec $INSTDIR . xt
SectionEnd

Section "System Administration Scripts"
    !insertmacro DirRec $INSTDIR . sbin
SectionEnd

Section "Local Wares"
    !insertmacro DirRec $INSTDIR . local\bin
    !insertmacro DirRec $INSTDIR . local\dos
    !insertmacro DirRec $INSTDIR . local\lib
SectionEnd

Section "System Utilities"
    !insertmacro DirRec $INSTDIR . ${7zip_rel}
    !insertmacro DirRec $INSTDIR . ${autohotkey_rel}
    !insertmacro DirRec $INSTDIR . ${autoit_rel}
    !insertmacro DirRec $INSTDIR . ${bochs_rel}
    !insertmacro DirRec $INSTDIR . ${npp_rel}
SectionEnd
