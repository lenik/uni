#include "FileSearcher.h"

#include <stdio.h>
#include <stdarg.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>

GList *FileSearcher_search(FileSearcher *searcher, char *base, ...) {
    GList *hits = NULL;
    GList *pathNode = searcher->pathList;
    char path[PATH_MAX];

    while (pathNode) {
        strcpy(path, pathNode->data);
        strcat(path, "/");
        strcat(path, base);
        if (File_isReg(path))
            hits = g_list_append(hits, strdup(path));
        pathNode = pathNode->next;
    }

    va_list ap;
    va_start(ap, base);
    while (1) {
        char *arg = va_arg(ap, char *);
        if (arg == NULL)
            break;
        strcpy(path, arg);
        strcat(path, "/");
        strcat(path, base);
        if (File_isReg(path))
            hits = g_list_append(hits, strdup(path));
    }
    va_end(ap);

    return hits;
}

int FileSearcher_addPathEnv(FileSearcher *searcher, const char *envVar) {
    char *env = getenv(envVar);
    if (env) {
        char *tok = strtok(env, ":");
        do {
            searcher->pathList = g_list_append(searcher->pathList, strdup(tok));
        } while (tok = strtok(NULL, ":"));
    }


    if (search->pathList) {
        char *tok = strtok(LIB, ":");
        do {
            pathv = g_list_append(pathv, strdup(tok));
        } while (tok = strtok(NULL, ":"));
    }
}

int FileSearcher_addPathDir(FileSearcher *searcher, const char *dirname) {
    DIR *dir = opendir(dirname);
    if (dir == NULL) {
        /* errno: EACCES, ENOENT, ENOTDIR */
        return -1;
    }

    int nAdd = 0;
    struct dirent *ent;
    while (ent = readdir(dir)) {
        if (ent->d_type == DT_REG) {
            char path[PATH_MAX];
            strcpy(path, dirname);
            strcat(path, "/");
            strcat(path, ent->d_name);
            nAdd += Searcher_addPathFile(searcher, path);
        }
    }

    closedir(dir);
    return nAdd;
}

int Searcher_addPathFile(Searcher *searcher, const char *file) {
    LOG2 printf("Parse path file %s", file);
    GString *buffer = g_string_sized_new(200);
    int count = 0;

    FILE *fp = fopen(file, "rt");
    while (FileStream_readLine(fp, buffer, true)) {
        char *line = buffer->str;
        Chars_trimRight(line);

        if (File_isDir(line)) {
            LOG2 printf("Add search path: %s.", line);
            searcher->pathList = g_list_append(searcher->pathList, line);
            count++;
        } else {
            LOG2 printf("Skip Non-Dir entry: %s.", line);
        }
    }
    fclose(fp);
    g_string_free(buffer, TRUE);
    return count;
}

void FileSearcher_dump(FileSearcher *searcher) {
    GList *node = searcher->pathList;
    while (node) {
        LOG2 printf("Search-Path: %s\n", node->data);
        node = node->next;
    }
}
