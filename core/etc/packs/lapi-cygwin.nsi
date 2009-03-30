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

    !insertmacro DirSub $INSTDIR . .
    !insertmacro DirRec $INSTDIR . bin
    !insertmacro DirRec $INSTDIR . dev
    !insertmacro DirSub $INSTDIR . etc
    !insertmacro DirRec $INSTDIR . etc\defaults
    !insertmacro DirRec $INSTDIR . etc\fonts
    !insertmacro DirRec $INSTDIR . etc\fstab.d
    !insertmacro DirRec $INSTDIR . etc\profile.d
    !insertmacro DirRec $INSTDIR . etc\rc.d
    !insertmacro DirRec $INSTDIR . etc\setup
    !insertmacro DirRec $INSTDIR . etc\skel
    !insertmacro DirRec $INSTDIR . etc\sysconfig
    !insertmacro DirRec $INSTDIR . etc\terminfo
    !insertmacro DirRec $INSTDIR . etc\xml
    !insertmacro DirRec $INSTDIR . lib\perl5
    !insertmacro DirRec $INSTDIR . lib\python2.5
    !insertmacro DirRec $INSTDIR . lib\ruby
    !insertmacro DirRec $INSTDIR . sbin
    !insertmacro DirRec $INSTDIR . srv
    !insertmacro DirRec $INSTDIR . usr\libexec
    !insertmacro DirRec $INSTDIR . usr\local
    !insertmacro DirRec $INSTDIR . usr\sbin
    !insertmacro DirRec $INSTDIR . usr\share\csih
    !insertmacro DirRec $INSTDIR . usr\share\man\man1
    !insertmacro DirRec $INSTDIR . usr\share\man\man2
    !insertmacro DirRec $INSTDIR . usr\share\man\man3
    !insertmacro DirRec $INSTDIR . usr\share\man\man4
    !insertmacro DirRec $INSTDIR . usr\share\man\man5
    !insertmacro DirRec $INSTDIR . usr\share\man\man6
    !insertmacro DirRec $INSTDIR . usr\share\man\man7
    !insertmacro DirRec $INSTDIR . usr\share\man\man8
    !insertmacro DirRec $INSTDIR . var\www
SectionEnd
