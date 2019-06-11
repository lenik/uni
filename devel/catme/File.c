#include "File.h"

#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>

mode_t File_mode(const char *file) {
    struct stat stat;
    if (stat(path, &stat) == 0)
        return stat.st_mode;
    else
        return 0;
}

bool File_isReg(const char *file) {
    return S_ISREG(File_mode(file));
}

bool File_isDir(const char *file) {
    return S_ISDIR(File_mode(file));
}
