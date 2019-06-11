#ifndef __File_H
#define __File_H

#include <stdbool.h>
#include <stdio.h>

mode_t File_mode(const char *file);
bool File_isReg(const char *file);
bool File_isDir(const char *file);

#endif
