#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <glib.h>
#include <bas/str.h>

#include "SrcLang.h"

SrcLang SrcLang_parse(const char *s) {
    if (s == NULL)
        return SrcLang_UNIX;
    if (streq(s, "unix"))
        return SrcLang_UNIX;
    if (streq(s, "c"))
        return SrcLang_C;
    if (streq(s, "sql"))
        return SrcLang_SQL;
    if (streq(s, "xml"))
        return SrcLang_XML;
    fprintf(stderr, "Unknown SrcLang: %s\n", s);
    exit(1);
}

const char *SrcLang_toString(SrcLang lang) {
    switch (lang) {
    case SrcLang_C:
        return "c";
    case SrcLang_SQL:
        return "sql";
    case SrcLang_XML:
        return "xml";
    case SrcLang_UNIX:
    default:
        return "unix";
    }
}

SrcLang SrcLang_fromExt(const char *ext, SrcLang defaultLang) {
    if (ext == NULL)
        return defaultLang;
    
    if (streq(ext, "htm")
             || streq(ext, "html")
             || streq(ext, "xhtml")
             || streq(ext, "xml"))
        return SrcLang_XML;
    
    if (streq(ext, "p")        /* perl */
             || streq(ext, "pl")
             || streq(ext, "py"))        /* python */
        return SrcLang_UNIX;
    
    if (streq(ext, "r")             /* ruby */
             || streq(ext, "rb"))
        return SrcLang_UNIX;
    
    if (streq(ext, "sql")           /* SQL */
             || streq(ext, "ddl"))
        return SrcLang_SQL;
    
    return defaultLang;
}
