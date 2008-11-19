
!include "..\..\lib\nsis\lapiota.nsh"
!include "ioutil.nsh"

Name "Lapiota Cygwin"

InstallDir

; Page custom...
Page instfiles

Section main
    DetailPrint "Cygwin=$CYGWIN"
SectionEnd
