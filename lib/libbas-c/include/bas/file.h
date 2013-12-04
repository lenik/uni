#ifndef __BAS_FILE_H
#define __BAS_FILE_H

#include <sys/types.h>
#include <stdio.h>

char *load_file(const char *path, size_t *sizep,
                size_t mem_off, size_t padding);

char *_load_file(FILE *in, const char *path, size_t *sizep,
                 size_t mem_off, size_t padding);

#endif
