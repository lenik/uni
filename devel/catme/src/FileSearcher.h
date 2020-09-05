#ifndef __FileSearcher_H
#define __FileSearcher_H

#include <stdbool.h>
#include <stdio.h>

typedef struct _FileSearcher {
    GList *pathList;    /** Which  directories to be searched. */
} FileSearcher;

/**
 * Search a file in directories.
 *
 * @param searcher Contains the path list.
 * @param base The basename of the file.
 * @param var-args Additional dir names to be searched.
 * @return List of full pathnames of found files.
 */
GList *FileSearcher_search(FileSearcher *searcher, char *base, ...);

/**
 * Load all path files in the dir.
 *
 * @param searcher Contains the path list.
 * @param dirname Where files to be find.
 * @return Count of dirs added, -1 if error.
 */
int FileSearcher_addPathEnv(FileSearcher *searcher, const char *env);

/**
 * Load all path files in the dir.
 *
 * @param searcher Contains the path list.
 * @param dirname Where files to be find.
 * @return Count of dirs added, -1 if error.
 */
int FileSearcher_addPathDir(FileSearcher *searcher, const char *dirname);

#endif
