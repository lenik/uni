#ifndef __BAS_STR_H
#define __BAS_STR_H

#include <stdbool.h>

bool streq(const char *a, const char *b);
char *startswith(const char *s, const char *t);
char *endswith(const char *s, const char *t);

#define LTRIM(s) while (isspace(*(s))) (s)++
char *ltrim(const char *s);
char *rtrim(char *s);
char *trim(char *s);

char *chop(char *s);
char *chomp(char *s);

char *readtok(char **endp);
char *strtok_eol(char *head, char **endp);

/* Break the quoted string, return the head token, and advance the *endp so as
   to prepare to return the next token. */
char *qstr_btok(char *head, char **endp, bool killq);

#endif
