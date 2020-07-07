#include <assert.h>
#include <ctype.h>
#include <stdbool.h>
#include <string.h>
#include <bas/str.h>

bool streq(const char *a, const char *b) {
    if (a == b)
        return true;
    if (a == NULL || b == NULL)
        return false;
    return strcmp(a, b) == 0;
}

char *startswith(const char *s, const char *t) {
    int n = strlen(t);
    if (strncmp(s, t, n) == 0)
        return (char *) (s + n);
    else
        return NULL;
}

char *endswith(const char *s, const char *t) {
    int ns = strlen(s);
    int nt = strlen(t);
    if (ns < nt)
        return NULL;
    if (strcmp(s + ns - nt, t) == 0)
        return (char *) (s + ns - nt);
    else
        return NULL;
}

char *ltrim(const char *s) {
    assert(s != NULL);

    while (isspace(*s))
        s++;

    return (char *) s;
}

char *rtrim(char *s) {
    assert(s != NULL);

    char *end = s + strlen(s) - 1;
    while (end >= s && isspace(*end))
        *end-- = '\0';

    return s;
}

char *trim(char *s) {
    s = ltrim(s);
    s = rtrim(s);
    return s;
}

char *chop(char *s) {
    assert(s != NULL);

    int len = strlen(s);
    if (len != 0)
        s[len - 1] = '\0';

    return s;
}

char *chomp(char *s) {
    assert(s != NULL);

    int len = strlen(s);
    char *end = s + len - 1;

    if (len > 0 && *end == '\n') {
        *end-- = '\0';
        len--;
    }

    if (len > 0 && *end == '\r') {
        *end-- = '\0';
        len--;
    }

    return s;
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

char *qstr_btok(char *head, char **endp, bool killq) {
    char *tok = head;
    char *end;
    char delim = 0;                     /* delim by space or NUL. */
    char ch;

    /* trim left. */
    while (isspace(*tok)) tok++;
    end = tok;

    switch (*tok) {
    case 0:
        return NULL;                    /* EOT or trailing space only */
    case '"':
        delim = '"';
        end++;
        break;
    case '\'':
        delim = '\'';
        end++;
        break;
    }
    if (killq) tok = end;

    if (delim == 0) {
        while (ch = *end) {
            if (isspace(ch))
                break;
            end++;
        }
    } else {
        while (ch = *end) {
            if (ch == delim) {
                if (! killq)
                    end++;
                break;
            }
            end++;
        }
    }

    if (*end)
        *end++ = '\0';
    if (endp)
        *endp = end;
    return tok;
}
