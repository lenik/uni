#ifndef __Frame_H
#define __Frame_H

#include <glib.h>

#include "SrcLang.h"

typedef struct _Frame Frame;
struct _Frame {
    Frame *parent;                      /* the including file */

    char *path;
    char *dir;                          /* this directory */
    char *fileName;                     /* base file name */
    char *fileExt;                      /* extension part of the fileName */

    char *qName;
    char *packageName;
    char *name;

    SrcLang lang;
    char *startSeq;                     /* start delimitor seq */
    char *stopSeq;                      /* stop delimitor seq */
    char *slStartSeq;                   /* single-line start delimitor seq */
    size_t nStartSeq;
    size_t nStopSeq;
    size_t nSlStartSeq;

    bool echo;               /* echo the commands (process instructions) */
    int echoAdd;             /* number of echo lines after \noecho (include) */
    bool copy;               /* copy the text, switch by \(no)skip, \(no)copy */

    GHashTable *vars;
};

Frame *Frame_new(Frame *parent, const char *path);

/**
 * @return The parent frame.
 */
Frame *Frame_free(Frame *frame);

void Frame_setPath(Frame *frame, const char *path);

void Frame_setSrcLang(Frame *frame, SrcLang lang);

void Frame_setDelim(const char *startSeq, const char *stopSeq, const char *slStartSeq);

char *Frame_qName2Href(Frame *frame, const char *qName);

#endif
