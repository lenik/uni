!include "..\..\lib\nsis\lapiota.nsh"
!cd ${msys_home}

Name "Lapiota Msys"
OutFile "${PACKOUT}\lapi-msys.exe"
InstallDir ${msys_home}

; Page custom...
Page instfiles

Section "Msys Minimum"
    SetOutPath $INSTDIR/lapiota
    SetOutPath $INSTDIR/var
    SetOutPath $INSTDIR/xt

    !insertmacro DirSub $INSTDIR . .
    !insertmacro DirRec $INSTDIR . bin
    !insertmacro DirRec $INSTDIR . etc
    !insertmacro DirRec $INSTDIR . libexec
    !insertmacro DirRec $INSTDIR . man
    !insertmacro DirRec $INSTDIR . mingw32
    !insertmacro DirRec $INSTDIR . postinstall
    !insertmacro DirRec $INSTDIR . sbin
    !insertmacro DirRec $INSTDIR . share\man
    !insertmacro DirRec $INSTDIR . ssl
    !insertmacro DirRec $INSTDIR . usr\local\bin
    !insertmacro DirRec $INSTDIR . usr\share\man
SectionEnd
