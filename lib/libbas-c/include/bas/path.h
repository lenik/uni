#ifndef __BAS_PATH_H
#define __BAS_PATH_H

/* Normalize a given path. Return the normalized path if succeeded, otherwise
   NULL.

   The returned string should be freed after use. */
char *path_normalize(const char *path);

/* Find the name in PATH environ. Return the absolute pathname if the name is
   found in directory denoted by PATH. Otherwise return NULL.

   The returned string should be freed after use. */
char *path_find(const char *name);

/* The combination of path_find and path_normalize.

   The returned string should be freed after use.*/
char *path_find_norm(const char *name);

#endif
