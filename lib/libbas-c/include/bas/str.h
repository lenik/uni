#ifndef __BAS_STR_H
#define __BAS_STR_H

#include <stdbool.h>

bool streq(const char *a, const char *b);

char *readtok(char **endp);
char *strtok_eol(char *head, char **endp);

/* Break the quoted string, return the head token, and advance the *endp so as
   to prepare to return the next token. */
char *qstr_btok(char *head, char **endp);

#endif
