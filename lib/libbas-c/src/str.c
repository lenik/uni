#include <ctype.h>
#include <stdbool.h>
#include <string.h>
#include <bas/str.h>

bool streq(const char *a, const char *b) {
    return strcmp(a, b) == 0;
}

char *readtok(char **endp) {
    char *head = *endp;
    char *end;

    /* trim left. */
    while (isspace(*head)) head++;

    if (*head == '\0')
        return NULL;

    end = head;
    while (*end && !isspace(*end))
        end++;

    if (*end)
        *end++ = '\0';
    *endp = end;

    return head;
}

/* String tokenizer, by replacing the end-of-line character by NUL.
   Return the head, or NULL if EOT it met. */
char *strtok_eol(char *head, char **endp) {
    char *end = head;
    char ch;
    if (*end == '\0')
        return NULL;

    while ((ch = *end)) {
        if (ch == '\n') {
            //if (end > head && end[-1] == '\r')
            //    end[-1] = '\0';         /* Replace CR.LF by NUL.NUL. */
            *end++ = '\0';
            break;
        }
        end++;
    }

    if (endp)
        *endp = end;
    return head;
}

char *qstr_btok(char *head, char **endp) {
    char *tok = head;
    char *end;
    char delim = 0;                     /* delim by space or NUL. */
    char ch;

    /* trim left. */
    while (isspace(*tok)) tok++;

    switch (*tok) {
    case 0:
        return NULL;                    /* EOT or trailing space only */
    case '"':
        delim = '"';
        tok++;
        break;
    case '\'':
        delim = '\'';
        tok++;
        break;
    }

    end = tok;
    if (delim == 0) {
        while ((ch = *end)) {
            if (isspace(ch))
                break;
            end++;
        }
    } else {
        while ((ch = *end++)) {
            if (ch == delim)
                break;
        }
    }

    if (*end)
        *end++ = '\0';
    if (endp)
        *endp = end;
    return tok;
}
