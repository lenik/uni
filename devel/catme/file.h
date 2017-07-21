#ifndef __FILE_H
#define __FILE_H

#include <stdbool.h>
#include <stdio.h>

typedef struct _Buffer {
    char *data;
    size_t size;
    size_t used;
} Buffer;

void Buffer_append(Buffer *buffer, char ch);
void Buffer_appendStr(Buffer *buffer, const char *s);

/**
 * Read a line into memory.
 * 
 * @param buf  allocated.
 */
bool File_readLine(File *f, Buffer *buf);

/**
 * Search a file in directories.
 *
 * @param base The basename of the file.
 * @param dirs Which  directories to be searched.
 * @param n How many args following.
 * @return List of full pathnames of existing files.
 */
GList *searchFileInDirs(char *base, GList *dirs, int n, ...);

/**
 * Load all path files in the dir.
 *
 * @param plist Pointer to the path list.
 * @param dirname Where files to be find.
 */
void loadPathDir(GList **plist, const char *dirname);

#endif
