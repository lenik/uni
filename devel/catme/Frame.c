#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <glib.h>
#include <bas/str.h>

#include "type.h"

Frame *Frame_new(Frame *parent, const char *path) {
    Frame *frame = g_new0(Frame, 1);
    frame->parent = parent;
    
    frame->lang = parent->lang;
    frame->startSeq = parent->startSeq;
    frame->stopSeq = parent->stopSeq;
    frame->slStartSeq = parent->slStartSeq;
    
    Frame_setPath(frame, path);
    return frame;
}

Frame *Frame_free(Frame *frame) {
    if (frame == NULL)
        return NULL;
    
    Frame *parent = frame->parent;
    
    N_FREE(frame->path);
    N_FREE(frame->dir);
    N_FREE(frame->fileName);
    N_FREE(frame->fileExt);
    
    N_FREE(frame->packageName);
    N_FREE(frame->qName);
    N_FREE(frame->name);
    
    N_FREE(frame->startSeq);
    N_FREE(frame->stopSeq);
    N_FREE(frame->slStartSeq);
    
    N_FREE(frame);
    
    return parent;
}

void Frame_setPath(Frame *frame, const char *path) {
    char *dir;
    char *fileName;

    const char *lastSlash = strrchr(path, '/');
    if (lastSlash == NULL) {
        dir = strdup(".");
        fileName = strdup(path);
    } else {
        dir = strndup(path, lastSlash - path);
        fileName = strdup(lastSlash + 1);
    }
    
    char *fileExt = strrchr(fileName, '.');
    char *name;
    if (fileExt == NULL) {
        name = strdup(fileName);
    } else {
        name = strndup(fileName, fileExt - fileName);
        fileExt = strdup(fileExt + 1);
    }
    Chars_replace(name, '.', '_');
    
    char packageName[PATH_MAX];
    char qName[PATH_MAX];
    if (streq(dir, ".")) {
        strcpy(packageName, "");
        strcpy(qName, name);
    } else {
        strcpy(packageName, dir);
        Chars_replace(packageName, '.', '_');
        Chars_replace(packageName, '/', '.');
        
        strcpy(qName, qPackage);
        strcat(qName, "/");
        strcat(qName, name);
    }
    
    N_SET(frame->path,      N_strdup(path));
    N_SET(frame->dir,       dir);
    N_SET(frame->fileName,  fileName);
    N_SET(frame->fileExt,   fileExt);
    
    N_SET(frame->qName,     N_strdup(qName));
    N_SET(frame->packageName,N_strdup(packageName));
    N_SET(frame->name,      name);
    
    SrcLang lang = SrcLang_fromExt(fileExt);
    if (lang != SrcLang_Unknown)
        Frame_setSrcLang(frame, lang);
}

void Frame_setSrcLang(Frame *frame, SrcLang lang) {
    const char *startSeq = "#";
    const char *stopSeq = "";
    const char *slStartSeq = "";
    switch (lang) {
        case SrcLang_C:
            startSeq = "/*";
            stopSeq = "*/";
            slStartSeq = "//";
            break;
        case SrcLang_SQL:
            startSeq = "";
            stopSeq = "";
            slStartSeq = "--";
            break;
        case SrcLang_XML:
            startSeq = "<!--";
            stopSeq = "-->";
            break;
        case SrcLang_UNIX:
            startSeq = "";
            stopSeq = "";
            slStartSeq = "#";
            break;
    }
    frame->lang = lang;
    Frame_setDelim(startSeq, stopSeq, slStartSeq);
}

void Frame_setDelim(const char *startSeq, const char *stopSeq, const char *slStartSeq) {
    Frame *frame = Stack_peek();
    N_SET(frame->startSeq, N_strdup(startSeq));
    N_SET(frame->stopSeq, N_strdup(stopSeq));
    N_SET(frame->slStartSeq, N_strdup(slStartSeq));
    frame->nStartSeq = N_strlen(startSeq);
    frame->nStopSeq = N_strlen(stopSeq);
    frame->nSlStartSeq = N_strlen(slStartSeq);
}

char *Frame_qName2Href(Frame *frame, const char *qName) {
    char buf[MAX_PATH];
    strcpy(buf, qName);
    Chars_replace(buf, '.', '/');
    strcat(buf, "/");
    strcat(buf, frame->fileExt);
    return strdup(buf);
}
