
!include "FileFunc.nsh"
!include "TextFunc.nsh"
!include "WordFunc.nsh"

!execute lapienv
!include "lapienv.nsh"

XPStyle on

Page license
Page components
Page directory

UninstPage uninstConfirm
UninstPage instfiles

; Languages
; First is default
LoadLanguageFile "${NSISDIR}\Contrib\Language files\English.nlf"            ; 1033
LoadLanguageFile "${NSISDIR}\Contrib\Language files\Dutch.nlf"              ; 1043
LoadLanguageFile "${NSISDIR}\Contrib\Language files\French.nlf"             ; 1036
LoadLanguageFile "${NSISDIR}\Contrib\Language files\German.nlf"             ; 1031
LoadLanguageFile "${NSISDIR}\Contrib\Language files\Korean.nlf"             ; 1042
LoadLanguageFile "${NSISDIR}\Contrib\Language files\Russian.nlf"            ; 1049
LoadLanguageFile "${NSISDIR}\Contrib\Language files\Spanish.nlf"            ; 1034
LoadLanguageFile "${NSISDIR}\Contrib\Language files\Swedish.nlf"            ; 1053
LoadLanguageFile "${NSISDIR}\Contrib\Language files\SimpChinese.nlf"        ; 2052
LoadLanguageFile "${NSISDIR}\Contrib\Language files\TradChinese.nlf"        ; 1028
LoadLanguageFile "${NSISDIR}\Contrib\Language files\Slovak.nlf"             ; 1052

LicenseLangString LicData ${LANG_ENGLISH}     "${LAPIOTA}\etc\db\licenses\GPLv2"
LicenseLangString LicData ${LANG_SIMPCHINESE} "${LAPIOTA}\etc\db\licenses\GPLv2"
LicenseData $(LicData)

Caption "Lapiota Package Installer"

ShowInstDetails show
