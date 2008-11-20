!include "..\..\lib\nsis\lapiota.nsh"
!cd ${LAPIOTA}

Name "Lapiota Workshops"
OutFile "${PACKOUT}\lapi-workshop.exe"
InstallDir ${LAPIOTA}

; Page custom...
Page instfiles

Section "Program Files"
    !insertmacro Files  $INSTDIR . .
    !insertmacro SubDir $INSTDIR . ${audacity_rel}
    !insertmacro SubDir $INSTDIR . ${ffmpeg_rel}
    !insertmacro SubDir $INSTDIR . ${gimp_rel}
    !insertmacro SubDir $INSTDIR . ${graphviz_rel}
    !insertmacro SubDir $INSTDIR . ${gsketchpad_rel}
    !insertmacro SubDir $INSTDIR . ${mencoder_rel}
    !insertmacro SubDir $INSTDIR . ${paint_net_rel}
SectionEnd
