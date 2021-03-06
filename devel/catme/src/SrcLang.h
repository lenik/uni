#ifndef __SrcLang_H
#define __SrcLang_H

typedef enum _SrcLang {
    SrcLang_Unknown = 0,
    SrcLang_C,
    SrcLang_SQL,
    SrcLang_UNIX,
    SrcLang_XML,
} SrcLang;

SrcLang SrcLang_parse(const char *s);
const char *SrcLang_toString(SrcLang lang);

SrcLang SrcLang_fromExt(const char *ext);

#endif
