!include "..\..\lib\nsis\lapiota.nsh"
!cd ${LAPIOTA}

Name "Lapiota Services"
OutFile "${PACKOUT}\lapi-serv.exe"
InstallDir ${LAPIOTA}

; Page custom...
Page instfiles

Section "Services Program Files"
   ;!insertmacro DirRec $INSTDIR . ${mysql_rel}
        SetOutPath $INSTDIR\${mysql_rel}
        File /a /r /x *.pdb .\${mysql_rel}\*
    !insertmacro DirRec $INSTDIR . ${openssl_rel}
    !insertmacro DirRec $INSTDIR . ${php_rel}
    !insertmacro DirRec $INSTDIR . etc\service
SectionEnd
