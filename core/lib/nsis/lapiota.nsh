!include "FileFunc.nsh"
!include "TextFunc.nsh"
!include "WordFunc.nsh"

!execute "$%LAPIOTA%\lib\nsis\lapiprep.bat"
!include "$%TEMP%\lapienv.nsh"
!addincludedir $%LAPIOTA%\lib\nsis
!define PACKOUT ${LAPIOTA}\opt

!macro DirRec outbase localbase subdir
    SetOutPath ${outbase}\${subdir}
    File /nonfatal /a /r /x .svn ${localbase}\${subdir}\*
!macroend

!macro DirSub outbase localbase subdir
    SetOutPath ${outbase}\${subdir}
    File /nonfatal /a ${localbase}\${subdir}\*
!macroend

!macro Files  outbase localbase pattern
    SetOutPath ${outbase}
    File /nonfatal /a ${localbase}\${pattern}
!macroend

!macro CpDir outbase localbase subdir
    # TODO...
!macroend

!macro EnvSet name value
    DetailPrint "Set system variable ${name}=${value}"
    WriteRegStr HKLM "SYSTEM\CurrentControlSet\Control\Session Manager\Environment" \
        "${name}" "${value}"
    SendMessage ${HWND_BROADCAST} ${WM_WININICHANGE} 0 "STR:Environment" /TIMEOUT=5000
!macroend

!macro EnvSetLocal name value
    DetailPrint "Set user variable ${name}=${value}"
    WriteRegStr HKCU "Environment" \
        "$name" "$value"
    SendMessage ${HWND_BROADCAST} ${WM_WININICHANGE} 0 "STR:Environment" /TIMEOUT=5000
!macroend

XPStyle on

Page license
Page components
Page directory

UninstPage uninstConfirm
UninstPage instfiles

; Languages
; First is default
LoadLanguageFile "${NSISDIR}\Contrib\Language files\English.nlf"            ; 1033
LoadLanguageFile "${NSISDIR}\Contrib\Language files\SimpChinese.nlf"        ; 2052

LicenseLangString LicData ${LANG_ENGLISH}     "${LAPIOTA}\etc\db\licenses\GPLv2"
LicenseLangString LicData ${LANG_SIMPCHINESE} "${LAPIOTA}\etc\db\licenses\GPLv2"
LicenseData $(LicData)

; Caption set default to title.
; Caption "Lapiota Package Installer"

ShowInstDetails show
