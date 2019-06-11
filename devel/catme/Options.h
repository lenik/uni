#ifndef __Options_H
#define __Options_H

typedef struct _Options {
    char *libPath;
    gboolean opt_echo;
} Options;

extern Options options;

#endif
