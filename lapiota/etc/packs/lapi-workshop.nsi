!include "..\..\lib\nsis\lapiota.nsh"
!cd ${LAPIOTA}

Name "Lapiota Workshops"
OutFile "${PACKOUT}\lapi-workshop.exe"
InstallDir ${LAPIOTA}

; Page custom...
Page instfiles

Section "Program Files"
    !insertmacro DirSub $INSTDIR . .
    !insertmacro DirRec $INSTDIR . ${audacity_rel}
    !insertmacro DirRec $INSTDIR . ${ffmpeg_rel}
    !insertmacro DirRec $INSTDIR . ${gimp_rel}
    !insertmacro DirRec $INSTDIR . ${graphviz_rel}
    !insertmacro DirRec $INSTDIR . ${gsketchpad_rel}
    !insertmacro DirRec $INSTDIR . ${mencoder_rel}
    !insertmacro DirRec $INSTDIR . ${paint_net_rel}
SectionEnd
