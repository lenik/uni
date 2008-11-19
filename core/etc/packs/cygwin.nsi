!include "..\..\lib\nsis\lapiota.nsh"
!include "ioutil.nsh"

Name "Lapiota Cygwin"
OutFile "lapi-cygwin.exe"
InstallDir ${cygwin_home}

; Page custom...
Page instfiles

Section main
    DetailPrint "Cygwin=${cygwin_home}"
SectionEnd
