#ifndef __QSTR_H
#define __QSTR_H

/* Break the quoted string, return the head token, and advance the *headp so as
   to prepare to return the next token. */
char *qstr_btok(char **headp);

#endif
