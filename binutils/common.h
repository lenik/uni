#ifndef __common_h
#define __common_h

#include <unistd.h>
#include <stdio.h>

int g_name;
int g_shortname;
int g_verbose;
int g_show_time;

typedef struct _option_t {
} option_t;

#define MAX_OPTIONS 100
option_t g_options[MAX_OPTIONS];
int g_noptions;

#define OPTION(x)

#endif
