!include "..\..\lib\nsis\lapiota.nsh"
!cd ${LAPIOTA}

Name "Lapiota Services"
OutFile "${PACKOUT}\lapi-serv.exe"
InstallDir ${LAPIOTA}

; Page custom...
Page instfiles

Section "Services Program Files"
   ;!insertmacro SubDir $INSTDIR . ${mysql_rel}
        SetOutPath $INSTDIR\${mysql_rel}
        File /a /r /x *.pdb .\${mysql_rel}\*
    !insertmacro SubDir $INSTDIR . ${openssl_rel}
    !insertmacro SubDir $INSTDIR . ${php_rel}
    !insertmacro SubDir $INSTDIR . etc\service
SectionEnd
