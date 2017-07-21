#include <stdargs.h>

char *expand_line(char *s, GHashTable *map) {
    //$s =~ s/\$(\d+|\w[A-Za_z0-9_\$]*)/expand_bare($1, $vars)/ge;
    //$s =~ s/\$\{([^{]+)\}/expand_brace($1, $vars)/ge;
    return s;
}

char *expand_bareVar(char *expr, GHashTable *map) {
    char *val = expand_expr(expr, map);
    if (val)
        return val;
    char *var = (char *) malloc(strlen(expr) + 2);
    strcpy(var, "$");
    strcat(var, expr);
    return var;
}

char *expand_braceVar(char *expr, GHashTable *map) {
    char *val = expand_expr(expr, map);
    if (val)
        return val;
    char *var = (char *) malloc(strlen(expr) + 4);
    strcpy(var, "${");
    strcat(var, expr);
    strcat(var, "}");
    return var;
}

char *expand_expr(char *expr, GHashTable *map) {
    int pad = 0;
    char *defl = NULL;

    while (1) {
        /* ${name:=value} */
        char *assign = strstr(expr, ":=");
        if (assign) {
            char *name = strndup(expr, 0, assign - expr);
            char *_val = assign + 2;
            char *val_exp = expand_line(_val, map);
            g_hashtable_set(map, name, val_exp);
            return NULL;
        }

        /* ${name[padding]} */
        char *bracket = strchr(expr, '[');
        if (bracket) {
            pad = number;
            // expr.remove([...]);
            continue;
        }

        char *eq = strchr(expr, "=");
        if (eq) {
            defl = eq + 1;
            expr.remove("=...$");
            continue;
        }
        break;
    }

    char *val = g_hashtable_get(map, expr);
    if (val == NULL)
        val = defl;

    while (pad > 0)
        strcat(val, ' ');
    return val;
}

GList *fqn2href(...) {
    Frame *frame = Stack_peek();
    char *ext = frame->ext;
    GList *vals = NULL;
    for (args) {
        arg = replace(arg, ".", "/");
        g_list_append(vals, "arg.ext");
    }
    return args;
}

