#ifndef __BAS_STR_H
#define __BAS_STR_H

#include <stdbool.h>

bool streq(const char *a, const char *b);
bool startswith(const char *s, const char *t);
bool endswith(const char *s, const char *t);

#define LTRIM(s) while (isspace(*(s))) (s)++
const char *ltrim_c(const char *s);
char *ltrim(char *s);
char *rtrim(char *s);

char *chop(char *s);
char *chomp(char *s);

char *readtok(char **endp);
char *strtok_eol(char *head, char **endp);

/* Break the quoted string, return the head token, and advance the *endp so as
   to prepare to return the next token. */
char *qstr_btok(char *head, char **endp);

#endif
