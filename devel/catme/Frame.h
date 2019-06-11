#ifndef __Frame_H
#define __Frame_H

#include <glib.h>

typedef struct _Frame {
    Frame *parent;                      /* the including file */
    
    char *dir;                          /* this directory */
    char *fileName;                     /* base file name */
    char *fileExt;                      /* extension part of the fileName */

    char *startSeq;                     /* left delimitor */
    char *stopSeq;                      /* right delimitor */
    char *startSeq1;                    /* inline left delimitor */
    char *stopSeq1;                     /* inline right delimitor */

    bool echo;               /* echo the commands (process instructions) */
    int echoAdd;             /* number of echo lines after \noecho (include) */
    bool copy;               /* copy the text, switch by \(no)skip, \(no)copy */
} Frame;

Frame *Frame_new(Frame *parent, const char *dir, const char *fileName);

/**
 * @return The parent frame.
 */
Frame *Frame_free(Frame *frame);

#endif
