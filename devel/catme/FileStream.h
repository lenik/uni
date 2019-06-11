#ifndef __FileStream_H
#define __FileStream_H

#include <stdbool.h>
#include <stdio.h>

#include "Buffer.h"

/**
 * Read a line into memory.
 *
 * @param buf  allocated.
 */
bool FileStream_readLine(File *f, Buffer *buf);

#endif
