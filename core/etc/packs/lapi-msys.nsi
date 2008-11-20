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

    !insertmacro Files  $INSTDIR . .
    !insertmacro SubDir $INSTDIR . bin
    !insertmacro SubDir $INSTDIR . etc
    !insertmacro SubDir $INSTDIR . libexec
    !insertmacro SubDir $INSTDIR . man
    !insertmacro SubDir $INSTDIR . mingw32
    !insertmacro SubDir $INSTDIR . postinstall
    !insertmacro SubDir $INSTDIR . sbin
    !insertmacro SubDir $INSTDIR . share\man
    !insertmacro SubDir $INSTDIR . ssl
    !insertmacro SubDir $INSTDIR . usr\local\bin
    !insertmacro SubDir $INSTDIR . usr\share\man
SectionEnd
