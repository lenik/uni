#ifndef __FileStream_H
#define __FileStream_H

#include <stdbool.h>
#include <stdio.h>

#include <glib.h>

/**
 * Read a line into memory. Includes the trailing EOL if any.
 *
 * @param buf allocated.
 * @return false when eof reached.
 */
bool FileStream_readLine(File *f, GString *buf, bool chomp);

#endif
