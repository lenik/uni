#include "Nullable.h"

char *g_strdup(const char *s) {
    if (s == NULL)
        return NULL;
    else
        return strdup(s);
}

size_t N_strlen(const char *s) {
    if (s == NULL)
        return 0;
    else
        return strlen(s);
}
