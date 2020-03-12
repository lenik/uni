#ifndef __Options_H
#define __Options_H

typedef struct _Options {
    char **searchPath;

    gboolean echo;

    char **files;

} Options;

extern Options cmdOptions;

#endif
