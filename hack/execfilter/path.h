#ifndef __PATH_H
#define __PATH_H

/* Normalize a given path. Return the normalized path if succeeded, otherwise
   NULL. The returned value should be freed after use. */
char *path_normalize(const char *path);

/* Find the name in PATH environ. Return the absolute pathname if the name is
   found in directory denoted by PATH. Otherwise return NULL.  */
char *path_find(const char *name);

/* The combination of path_find and path_normalize. */
char *path_find_norm(const char *name);

#endif
