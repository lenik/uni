#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <glib.h>
#include <bas/str.h>

#include "type.h"

Frame *Frame_new(Frame *parent, const char *dir, const char *fileName) {
    Frame *frame = g_new0(Frame, 1);
    frame->parent = parent;
    frame->dir = strdup(dir);
    frame->fileName = strdup(fileName);
    
    Frame_initExt(frame, fileExt);
    return frame;
}

Frame *Frame_free(Frame *frame) {
    if (frame == NULL)
        return NULL;
    
    Frame *parent = frame->parent;
    
    FREE(frame->dir);
    FREE(frame->fileName);
    
    FREE(frame->startSeq);
    FREE(frame->stopSeq);
    FREE(frame->startSeq1);
    FREE(frame->stopSeq1);
    FREE(frame);
    
    return parent;
}

void Frame_initExt(Frame *frame, const char *ext) {
    SrcLang lang = SrcLang_UNIX;
    const char *startSeq = "#";
    const char *stopSeq = "";
    
    SET_STRDUP(frame->fileExt, ext);
    if (ext) {
        if (streq(ext, "htm")
                 || streq(ext, "html")
                 || streq(ext, "xhtml")
                 || streq(ext, "xml"))
            lang = SrcLang_XML;
        else if (streq(ext, "p")        /* perl */
                 || streq(ext, "pl")
                 || streq(ext, "py"))        /* python */
            lang = SrcLang_UNIX;
        else if (streq(ext, "r")             /* ruby */
                 || streq(ext, "rb"))
            lang = SrcLang_UNIX;
        else if (streq(ext, "sql")           /* SQL */
                 || streq(ext, "ddl"))
            lang = SrcLang_SQL;
        else {
            /* fallback to UNIX */
            lang = SrcLang_UNIX;
        }
    }
    switch (lang) {
        case SrcLang_C:
            startSeq = "/*"; stopSeq = "*/"; break;
        case SrcLang_SQL:
            startSeq = "--"; stopSeq = ""; break;
        case SrcLang_XML:
            startSeq = "<!--"; stopSeq = "-->"; break;
        case SrcLang_UNIX:
            startSeq = "#"; stopSeq = ""; break;
    }

    SET_STRDUP(frame->startSeq, startSeq);
    SET_STRDUP(frame->stopSeq, stopSeq);
}
