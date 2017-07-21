#include "file.h"

#include <stdio.h>
#include <stdarg.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>

static Buffer *_linebuf;

char *File_readLine1(File *f) {
    if (_linebuf == NULL)
        _linebuf = (Buffer *) malloc(sizeof(Buffer));
    if (File_readLine(f, _linebuf))
        return _linebuf->data;
    else
        return NULL;
}

bool File_readLine(File *f, Buffer *buf) {
    int ch;
    size_t size = 0;
    while ((ch = fgetc(f)) != EOF) {
        size++;
        if (buf->used == buf->size) {
            size_t newSize = buf->size * 2;
            char *p = realloc(buf->data, newSize);
            if (p == NULL) {
                perror("Memory out");
                exit(1);
            }
            buf->data = p;
            buf->size = newSize;
        }
        buf->data[buf->used++] = ch;
    }
    return size != 0;
}

mode_t File_mode(const char *file) {
    struct stat stat;
    if (stat(path, &stat) == 0)
        return S_ISREG(stat.st_mode);
    else
        return 0;
}

bool File_isReg(const char *file) {
    return S_ISREG(File_mode(file));
}

bool File_isDir(const char *file) {
    return S_ISDIR(File_mode(file));
}

GList *searchFileInDirs(char *base, GList *dirs, int n, ...) {
    GList *hits = NULL;
    GList *dir = dirs;
    char path[PATH_MAX];

    while (dir) {
        strcpy(path, dir->data);
        strcat(path, "/");
        strcat(path, base);
        if (File_isReg(path))
            hits = g_list_append(hits, strdup(path));
        dir = dir->next;
    }
    
    va_list ap;
    va_start(ap, n);
    while (n--) {
        char *arg = va_arg(ap, char *);
        strcpy(path, arg);
        strcat(path, "/");
        strcat(path, base);
        if (File_isReg(path))
            hits = g_list_append(hits, strdup(path));
    }
    va_end(ap);
    
    return hits;
}

void loadPathDir(GList **plist, const char *dirname) {
    DIR *dir = opendir(dirname);
    if (dir == NULL) {
        /* errno: EACCES, ENOENT, ENOTDIR */
        return;
    }
    
    struct dirent *ent;
    while (ent = readdir(dir)) {
        if (ent->d_type == DT_REG) {
            char path[PATH_MAX];
            strcpy(path, dirname);
            strcat(path, "/");
            strcat(path, ent->d_name);
            loadPathFile(plist, path);
        }
    }
    
    closedir(dir);
}

void loadPathFile(GList **plist, const char *file) {
    LOG2 printf("Parse path file %s", file);
    FILE *f = fopen(file, "rt");
    char *line;
    while (line = File_readLine1(f)) {
        
        if (File_isDir(line)) {
            LOG2 printf("Add search path %s", line);
            pathv = g_list_append(pathv, line);
        }
    }
    fclose(f);
}
