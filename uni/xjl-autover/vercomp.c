#include "config.h"

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <string.h>
#include <ctype.h>

#include <glib.h>

#define MAX_LINE 1024

#define LOG0 if (opt_verbose >= 0)
#define LOG1 if (opt_verbose >= 1)
#define LOG2 if (opt_verbose >= 2)

char *      opt_incrfield = NULL;
gboolean    opt_odd       = 0;
gboolean    opt_even      = 0;
int         opt_verbose   = 0;
char **     opt_files;
gboolean    opt_stdout    = FALSE;      /* write to stdout instead of save */
gboolean    opt_update    = FALSE;      /* always save? */

char *      filename;
int         line = 0;

GHashTable *vartab = NULL;

gboolean error(const char *fmt, ...);
gboolean parse_error(const char *fmt, ...);

const char *str_next_tok(const char *start, char **tok);
gboolean parse_component(const char *field, const char *s,
                         long *pval, const char **pnext);

int do_main();
gboolean scan(FILE *in, gboolean save, GString *out);

gboolean field_incr(const char *field, int delta);
gboolean field_refresh_all();
gboolean field_cascade(const char *field, int index);

gboolean set_verbose_arg(const char *opt,
                         const char *val,
                         gpointer data,
                         GError **err) {
    while (*opt == '-') opt++;
    if (*opt == 'q') {                  /* q, quiet */
        opt_verbose--;
    } else if (*opt == 'v') {           /* v, verbose */
        opt_verbose++;
    }
    return TRUE;
}

void show_version(const char *opt, const char *val,
                  gpointer data, GError **err) {
    printf("vercomp 0.1\n"
           "written by Lenik, (at) 99jsj.com\n");
    exit(0);
}

static GOptionEntry entries[] = {
    { "incr-field",'i', 0, G_OPTION_ARG_STRING, &opt_incrfield,
      "increase the specified field and reset minor versions", },

    { "odd",       'o', 0, G_OPTION_ARG_NONE, &opt_odd,
      "increase to odd number (unstable)", },

    { "even",      'e', 0, G_OPTION_ARG_NONE, &opt_even,
      "increase to even number (stable)", },

    { "stdout",    'c', 0, G_OPTION_ARG_NONE, &opt_stdout,
      "write to stdout instead of save", },

    { "update",    'u', 0, G_OPTION_ARG_NONE, &opt_update,
      "always update the file", },

    { "quiet",     'q', G_OPTION_FLAG_NO_ARG,
      G_OPTION_ARG_CALLBACK, set_verbose_arg,
      "output less verbose info", },

    { "verbose",   'v', G_OPTION_FLAG_NO_ARG,
      G_OPTION_ARG_CALLBACK, set_verbose_arg,
      "output more verbose info", },

    { "version",   '\0', G_OPTION_FLAG_NO_ARG,
      G_OPTION_ARG_CALLBACK, show_version,
      "show version info", },

    { G_OPTION_REMAINING, '\0', 0,
      G_OPTION_ARG_FILENAME_ARRAY, &opt_files, "FILES", },

    { NULL },
};

int main(int argc, char **argv) {
    GError *gerr = NULL;
    GOptionContext *opts;
    int err;

    opts = g_option_context_new("[VERSION.av]");
    g_option_context_add_main_entries(opts, entries,
                                      NULL /* translation-domain */
                                      );

    if (! g_option_context_parse(opts, &argc, &argv, &gerr)) {
        fprintf(stderr, "Couldn't parse options: %s\n", gerr->message);
        return 1;
    }

    if (opt_incrfield && ! opt_stdout)
        opt_update = TRUE;

    if (opt_files) {
        if (opt_files[0])
            filename = g_strdup(opt_files[0]);
        g_strfreev(opt_files);
        opt_files = NULL;
    }
    if (filename == NULL)
        filename = g_strdup("VERSION.av");

    vartab = g_hash_table_new_full(g_str_hash, g_str_equal,
                                   g_free, g_free);

    err = do_main();

    g_free(filename);
    g_hash_table_unref(vartab);

    g_option_context_free(opts);
    g_free(opt_incrfield);

    return err;
}

gboolean error(const char *fmt, ...) {
    va_list ap;
    va_start(ap, fmt);
    vfprintf(stderr, fmt, ap);
    va_end(ap);
    return FALSE;
}

gboolean parse_error(const char *fmt, ...) {
    va_list ap;
    fprintf(stderr, "%s:%d:", filename, line);
    va_start(ap, fmt);
    vfprintf(stderr, fmt, ap);
    va_end(ap);
    return FALSE;
}


const char *str_next_tok(const char *start, char **tok) {
    const char *s = start;

    if (s == NULL) {
        *tok = NULL;
        return NULL;
    }

    while (*s && !isblank(*s))
        s++;

    if (tok)
        *tok = g_strndup(start, s - start);

    if (*s == '\0')
        return NULL;

    while (isblank(*++s)) ;
    if (*s == 0)
        return NULL;

    return s;
}

gboolean parse_component(const char *field, const char *s,
                         long *pval, const char **pnext) {
    long        val = 0;
    const char *next = NULL;
    const char *end;

    // assert (s != NULL);
    val = strtol(s, (char **) &end, 0); /* may be hex */
    if (end == s) {
        error("Field %s: invalid number: \"%s\"\n", field, s);
        return FALSE;
    }

    while (isspace(*end)) end++;
    if (*end == '\0')
        end = NULL;

    if (pval) *pval = val;
    if (pnext) *pnext = end;
    return TRUE;
}

int do_main() {
    FILE *in;

    /* 1, pass 1, scan all fields */
    in = fopen(filename, "r");
    if (in == NULL) {
        fprintf(stderr, "Can't read from file %s: ", filename);
        perror("");
        return 1;
    }

    if (! scan(in, FALSE, NULL)) {
        fprintf(stderr, "Parse failure\n");
        return 1;
    }

    fclose(in);

    LOG2 {
        GHashTableIter it;
        gpointer key, value;

        fprintf(stderr, "Dump of pass 1\n");
        g_hash_table_iter_init(&it, vartab);
        while (g_hash_table_iter_next(&it, &key, &value)) {
            fprintf(stderr, "    %s = %s\n",
                    (const char *)key, (const char *) value);
        }
    }

    /* 2, do refresh and increment, and refresh related fields */

    if (! field_refresh_all())
        return 2;

    if (opt_incrfield)
        if (! field_incr(opt_incrfield, 1))
            return 2;                   /* field_incr fails */

    if (opt_stdout || opt_update) {
        FILE *   out;
        GString *outbuf;

        /* 3, scan 2, out to buffer */
        in = fopen(filename, "r");
        if (in == NULL) {
            fprintf(stderr, "Can't re-open %s: ", filename);
            perror("");
            return 3;
        }

        outbuf = g_string_sized_new(line * 60);
        if (! scan(in, TRUE, outbuf))
            return 3;

        fclose(in);

        /* 4, write the buffer */
        if (opt_stdout)
            out = stderr;
        else {
            LOG1 printf("update file %s\n", filename);

            out = fopen(filename, "w");
            if (out == NULL) {
                fprintf(stderr, "Can't write to %s: ", filename);
                perror("");
                return 4;
            }
        }

        fputs(outbuf->str, out);

        if (! opt_stdout)
            fclose(out);

        g_string_free(outbuf, TRUE);

    } // update

    LOG2 {
        GHashTableIter it;
        gpointer key, value;

        fprintf(stderr, "Dump of pass 2\n");
        g_hash_table_iter_init(&it, vartab);
        while (g_hash_table_iter_next(&it, &key, &value)) {
            fprintf(stderr, "    %s = %s\n",
                    (const char *)key, (const char *) value);
        }
    }

    /* 5, display format if no argument, or not stdout-mode */
    if (opt_incrfield == NULL || ! opt_stdout) {
        char *   format = g_hash_table_lookup(vartab, "format");
        char *   p      = format;
        int      c;
        char *   field;
        char *   val_text;
        long     val;

        if (format == NULL) {
            fprintf(stderr, "Undefined format. \n");
            return 5;
        }

        while (c = *p++) {
            switch (c) {
            case '$':                   /* $field */
                field = p;
                while (isalnum(*p)) p++;
                if (p == field)         /* ignore orphan '$' */
                    break;

                field = g_strndup(field, p - field);
                val_text = g_hash_table_lookup(vartab, field);
                if (val_text) {
                    /* ignore next field */
                    if (! parse_component(field, val_text,
                                          &val, NULL))
                        return 5;
                    printf("%ld", val);
                } else {
                    fprintf(stderr, "Undefined field: %s\n", field);
                    return 5;
                }

                g_free(field);
                continue;

            case '\\':
                if (*p) {
                    switch (*p++) {
                    case 't': c = '\t'; break;
                    case 'n': c = '\n'; break;
                    case 'r': c = '\r'; break;
                    case '0': c = '\0'; break;
                    }
                }
                break;
            }

            putchar(c);
        } // while c

        putchar('\n');
    }

    return 0;
}

gboolean scan(FILE *in, gboolean save, GString *out) {
    char *p;
    char *end;
    char linebuf[MAX_LINE];

    line = 0;

    while (p = fgets(linebuf, MAX_LINE, in)) {
        char *end;
        char *key = NULL;
        char *val = NULL;
        char *val0;

        line++;
        LOG2 fprintf(stderr, "%d> %s", line, p);

        if (! *p) break;                /* EOF */

        if (strlen(linebuf) == MAX_LINE - 1 &&
                linebuf[MAX_LINE - 2] != '\n')
            return parse_error("Exceeds line limit (%d).", MAX_LINE);

        while (isblank(*p)) {           /* ltrim */
            if (save) g_string_append_c(out, *p);
            p++;
        }

        if (*p == '#') {                /* skip comments */
            if (save) g_string_append(out, p);
            continue;
        }

        end = p + strlen(p);
        while (end > p && isspace(*(end - 1))) end--; /* rtrim + chomp */
        *end = '\0';

        if (end == p) {                 /* skip empty lines */
            if (save) g_string_append_c(out, '\n');
            continue;
        }

        val = strchr(p, '=');
        if (val) {
            key = p;
            end = val;

            if (save) {                  /* output key= with padding */
                *end = '\0';
                g_string_append(out, key);
                g_string_append_c(out, '=');
            }

            while (end > key && isblank(*(end - 1))) end--; /* rtrim(key) */
            *end = '\0';

            if (end == key)             /* if -z key */
                return parse_error("Key name is empty!");

            while (isblank(*++val))     /* ltrim(val) */
                if (save) g_string_append_c(out, *val);

            val0 = g_hash_table_lookup(vartab, key);
            if (save) {
                if (val0 == NULL) {      /* Removed entry?  */
                    // g_string_append(out, "");
                } else {
                    g_string_append(out, val0);
                }
            } else {
                if (val0 != NULL)
                    return parse_error("Key %s is already defined as %s. ",
                                       key, val0);
                key = g_strdup(key);
                val = g_strdup(val);
                g_hash_table_insert(vartab, key, val);
            }

            if (out) g_string_append_c(out, '\n');
        } else {
            // key = g_strdup(p);
            return parse_error("Expected '=': %s\n", p);
        } // strchr('=')
    } // while fgets

    return TRUE;
}

/**
 * Only the first occurence of an integer is changed.
 * If the field doesn't exist, new entry will be created.
 *
 * Format of the specified field:
 *
 * field = value (\s+ next)*
 */
gboolean field_incr(const char *field, int delta) {
    char *      val_text;
    long        val  = 0;

    char *      nexts  = NULL;
    const char *next   = NULL;
    gboolean    retval = TRUE;

    val_text = g_hash_table_lookup(vartab, field);
    if (val_text != NULL) {
        if (! parse_component(field, val_text,
                              &val, (const char **) &nexts))
            return FALSE;
        /* The _replace function will destroy the old val_text */
        if (nexts)
            nexts = g_strdup(nexts);
    }

    val += delta;
    if (opt_odd)
        if ((val & 1) == 0) {
            LOG1 printf("fix odd by +1\n");
            val++;
        }

    if (opt_even)
        if ((val & 1) == 1) {
            LOG1 printf("fix even by +1\n");
            val++;
        }

    val_text = g_strdup_printf("%ld %s", val, nexts ? nexts : "");

    /* insert/replace, cuz field may be not existed */
    g_hash_table_insert(vartab,
                        g_strdup(field),
                        val_text);

    next = nexts;
    while (next) {
        char *next_field;
        next = str_next_tok(next, &next_field);
        if (! field_cascade(next_field, 0)) {
            retval = FALSE;
            break;
        }
    }

    if (nexts)
        g_free(nexts);

    return retval;
}

gboolean field_refresh_all() {
    GHashTable *tmp;                    /* so as no to modify orig table within
                                           iteration. */

    GHashTableIter it;
    char *key;
    char *value;
    char *field = NULL;                 /* "field */
    gboolean retval = TRUE;

    /* tmp hash table don't destory any value. */
    tmp = g_hash_table_new(g_str_hash, g_str_equal);

    g_hash_table_iter_init(&it, vartab);
    while (g_hash_table_iter_next(&it,
                                  (gpointer *) &key,
                                  (gpointer *) &value)) {
        long base = 0;
        long val_set  = -1;             /* -1 if n/a */
        int  val_incr = 0;
        int  val_dirty = FALSE;

        if (strchr(key, '.') != NULL)
            continue;

        LOG2 printf("Refresh: %s = %s\n", key, value);

        field = key;

        /* .base section */
        char *base_field = g_strdup_printf("%s.base", field);
        char *base_text = g_hash_table_lookup(vartab, base_field);
        g_free(base_field);

        if (base_text != NULL)
            base = strtol(base_text, NULL, 0);

        /* .exec section */
        char *exec_field = g_strdup_printf("%s.exec", field);
        char *exec_text = g_hash_table_lookup(vartab, exec_field);
        g_free(exec_field);

        if (exec_text) {
            FILE *   proc_out;
            GString *cap   = NULL;
            char *   end;
            char     c;
            long     result_val;

            LOG1 {
                printf("Execute %s: ", exec_text);
                fflush(stdout);
            }

            proc_out = popen(exec_text, "r");
            if (proc_out == NULL) {
                fprintf(stderr, "Failed to execute: %s: ", exec_text);
                perror("");
                retval = FALSE;
                break;
            } else {
                cap = g_string_sized_new(100);
                while ((c = fgetc(proc_out)) != EOF) {
                    LOG1 { putchar(c); fflush(stdout); }
                    g_string_append_c(cap, c);
                }
                LOG1 putchar('\n');

                pclose(proc_out);

                result_val = strtol(cap->str, &end, 0);
                if (end == cap->str) {
                    fprintf(stderr, "%s: Expect an integer: %s\n",
                            key, cap->str);
                    retval = FALSE;
                }

                val_set = result_val - base;
                val_dirty = TRUE;

                g_string_free(cap, TRUE);

                if (! retval)
                    break;
            }

        }

        /* .incr section */
        char *incr_field = g_strdup_printf("%s.incr", field);
        char *incr_text = g_hash_table_lookup(vartab, incr_field);
        g_free(incr_field);

        if (incr_text) {
            val_incr = (int) strtol(incr_text, NULL, 0);
            val_dirty = TRUE;
        }

        if (val_dirty) {
            char *      val_text = value;
            long        val;
            const char *nexts = NULL;

            LOG2 printf("save field %s (%s)\n", field, val_text);

            if (! parse_component(field, val_text, &val, &nexts)) {
                retval = FALSE;             /* invalid stem val_text */
                break;
            }

            if (val_set != -1)
                val = val_set;
            val += val_incr;

            val_text = g_strdup_printf("%ld %s", val,
                                       nexts ? nexts : "");

            g_hash_table_insert(tmp,
                                g_strdup(field),
                                val_text);
        }

    } // while iter(.exec)

    /* write back. */
    g_hash_table_iter_init(&it, tmp);
    while (g_hash_table_iter_next(&it, (gpointer *) &key, (gpointer *) &value)) {
        LOG2 printf("commit field %s := %s\n", key, value);
        g_hash_table_insert(vartab, key, value);
    }

    g_hash_table_unref(tmp);

    return retval;
}

gboolean field_cascade(const char *field, int index) {
    char *      val_text;
    long        val  = 0;

    char *      nexts = NULL;
    const char *next  = NULL;

    char *      base_field;
    char *      base_text;

    val_text = g_hash_table_lookup(vartab, field);

    /* field = EMPTY */
    if (val_text != NULL) {
        if (! parse_component(field, val_text,
                              &val, (const char **) &nexts))
            return FALSE;
        if (nexts)
            nexts = g_strdup(nexts);
    }

    base_field = g_strdup_printf("%s.base", field);
    base_text = g_hash_table_lookup(vartab, base_field);
    if (base_text) {                    /* has .base */
        /* .base += val, val = 0 */
        char *end;
        long  base_val = strtol(base_text, &end, 0);
        char *new_text;

        if (end == base_text) {
            fprintf(stderr, "Expect an integer from %s: %s\n",
                    base_field, base_text);
            g_free(base_field);
            return FALSE;
        }

        base_val += val;
        val = 0;

        g_hash_table_replace(vartab,
                             g_strdup(base_field),
                             g_strdup_printf("%ld", base_val));
    } else {
        val = 0;
    }

    g_free(base_field);

    val_text = g_strdup_printf("%ld %s", val, nexts ? nexts : "");
    g_hash_table_insert(vartab,
                        g_strdup(field),
                        val_text);

    next = nexts;
    while (next) {
        char *next_field;
        next = str_next_tok(next, &next_field);
        if (! field_cascade(next_field, index + 1))
            return FALSE;
    }

    if (nexts)
        g_free(nexts);

    return TRUE;
}
