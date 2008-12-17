!include "..\..\lib\nsis\lapiota.nsh"
!cd ${cygwin_home}

Name "Lapiota Cygwin"
OutFile "${PACKOUT}\lapi-cygwin.exe"
InstallDir ${cygwin_home}

; Page custom...
Page instfiles

Section "Cygwin Minimum"
    SetOutPath $INSTDIR\home
    SetOutPath $INSTDIR\media
    SetOutPath $INSTDIR\mnt
    SetOutPath $INSTDIR\tmp
    SetOutPath $INSTDIR\var\log
    SetOutPath $INSTDIR\var\tmp

    !insertmacro Files  $INSTDIR . .
    !insertmacro SubDir $INSTDIR . bin
    !insertmacro SubDir $INSTDIR . dev
    !insertmacro Files  $INSTDIR . etc
    !insertmacro SubDir $INSTDIR . etc\defaults
    !insertmacro SubDir $INSTDIR . etc\fonts
    !insertmacro SubDir $INSTDIR . etc\fstab.d
    !insertmacro SubDir $INSTDIR . etc\profile.d
    !insertmacro SubDir $INSTDIR . etc\rc.d
    !insertmacro SubDir $INSTDIR . etc\setup
    !insertmacro SubDir $INSTDIR . etc\skel
    !insertmacro SubDir $INSTDIR . etc\sysconfig
    !insertmacro SubDir $INSTDIR . etc\terminfo
    !insertmacro SubDir $INSTDIR . etc\xml
    !insertmacro SubDir $INSTDIR . lib\perl5
    !insertmacro SubDir $INSTDIR . lib\python2.5
    !insertmacro SubDir $INSTDIR . lib\ruby
    !insertmacro SubDir $INSTDIR . sbin
    !insertmacro SubDir $INSTDIR . srv
    !insertmacro SubDir $INSTDIR . usr\libexec
    !insertmacro SubDir $INSTDIR . usr\local
    !insertmacro SubDir $INSTDIR . usr\sbin
    !insertmacro SubDir $INSTDIR . usr\share\csih
    !insertmacro SubDir $INSTDIR . usr\share\man\man1
    !insertmacro SubDir $INSTDIR . usr\share\man\man2
    !insertmacro SubDir $INSTDIR . usr\share\man\man3
    !insertmacro SubDir $INSTDIR . usr\share\man\man4
    !insertmacro SubDir $INSTDIR . usr\share\man\man5
    !insertmacro SubDir $INSTDIR . usr\share\man\man6
    !insertmacro SubDir $INSTDIR . usr\share\man\man7
    !insertmacro SubDir $INSTDIR . usr\share\man\man8
    !insertmacro SubDir $INSTDIR . var\www
SectionEnd
