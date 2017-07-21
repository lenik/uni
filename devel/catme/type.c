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

Frame *Frame_new() {
    Frame *p = g_new0(Frame, 1);
    return p ;
}

void Frame_free(Frame *frame) {
    if (frame == NULL)
        return;
    FREE(frame->del);
    FREE(frame->der);
    FREE(frame->idel);
    FREE(frame->ider);
    FREE(frame->ext);
    FREE(frame->dir);
    free(frame);
}

void Frame_initExt(Frame *frame, const char *ext) {
    FileLang lang = FileLang_UNIX;
    const char *del = "#";
    const char *der = "";
    
    SET_STRDUP(frame->ext, ext);
    if (ext) {
        if (streq(ext, "htm")
                 || streq(ext, "html")
                 || streq(ext, "xhtml")
                 || streq(ext, "xml"))
            lang = FileLang_XML;
        else if (streq(ext, "p")        /* perl */
                 || streq(ext, "pl")
                 || streq(ext, "py"))        /* python */
            lang = FileLang_UNIX;
        else if (streq(ext, "r")             /* ruby */
                 || streq(ext, "rb"))
            lang = FileLang_UNIX;
        else if (streq(ext, "sql")           /* SQL */
                 || streq(ext, "ddl"))
            lang = FileLang_SQL;
        else {
            /* fallback to UNIX */
            lang = FileLang_UNIX;
        }
    }
    switch (lang) {
    case FileLang_C:
        del = "/*"; der = "*/"; break;
    case FileLang_SQL:
        del = "--"; der = ""; break;
    case FileLang_XML:
        del = "<!--"; der = "-->"; break;
    case FileLang_UNIX:
        del = "#"; der = ""; break;
    }

    SET_STRDUP(frame->del, del);
    SET_STRDUP(frame->der, der);
}

Frame *Stack_enter() {
    Frame *p = Frame_new();
    g_queue_push_head(stack, p);
    return p ;
}

void Stack_leave() {
    Frame *p = g_queue_pop_head(stack);
    if (p)                              /* NULL if stack empty */
        Frame_free(p);
}

Frame *Stack_peek() {
    return (Frame *) g_queue_peek_head(stack);
}
