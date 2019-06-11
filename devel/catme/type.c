#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <glib.h>
#include <bas/str.h>

#include "type.h"

FileLang Lang_parse(const char *s) {
    if (s == NULL)
        return FileLang_UNIX;
    if (streq(s, "unix"))
        return FileLang_UNIX;
    if (streq(s, "c"))
        return FileLang_C;
    if (streq(s, "sql"))
        return FileLang_SQL;
    if (streq(s, "xml"))
        return FileLang_XML;
    fprintf(stderr, "Unknown FileLang: %s\n", s);
    exit(1);
}

const char *Lang_toString(FileLang lang) {
    switch (lang) {
    case FileLang_C:
        return "c";
    case FileLang_SQL:
        return "sql";
    case FileLang_XML:
        return "xml";
    case FileLang_UNIX:
    default:
        return "unix";
    }
}
