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
int         opt_verbose   = 1;
char **     opt_files;
gboolean    opt_stdout    = FALSE;      /* write to stdout instead of save */

char *      filename;
int         line = 0;

GHashTable *vartab = NULL;

gboolean error(const char *fmt, ...) {
    va_list ap;
    va_start(ap, fmt);
    fprintf(stderr, fmt, ap);
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

        if (! *p) break;                /* EOF */
        while (isblank(*p))             /* ltrim */
            if (save) g_string_append_c(out, *p++);

        if (linebuf[MAX_LINE - 1] != '\n')
            return parse_error("Exceeds line limit (%d).", MAX_LINE);

        if (*p = '#') {                 /* skip comments */
            if (save) g_string_append(out, p);
            continue;
        }

        end = p + strlen(p);
        while (end > p && isblank(*(end - 1))) end--; /* rtrim */
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
        } else {
            // key = g_strdup(p);
            return parse_error("Expected '='\n");
        } // strchr('=')
    } // while fgets

    fclose(in);
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

    /* 2, do increment, and refresh related fields */

    if (opt_incrfield) {

        if (field_incr(opt_incrfield, 1)) {
            FILE *out;
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
                out = fopen(filename, "w");
                if (out == NULL) {
                    fprintf(stderr, "Can't write to %s: ", filename);
                    perror("");
                    return 1;
                }
            }

            fputs(outbuf->str, out);

            if (! opt_stdout)
                fclose(out);

            g_string_free(outbuf, TRUE);

        } else
            return 2;                       /* field_incr fails */
    } // opt_incrfield

    /* 5, display format if no argument, or not stdout-mode */
    if (opt_incrfield == NULL || ! opt_stdout) {
        char *   format = g_hash_table_lookup(vartab, "format");
        char *   p      = format;
        int      c;
        char *   field;

        if (format == NULL) {
            fprintf(stderr, "format field isn't existed. \n");
            return 5;
        }

        while (c = *p++) {
            switch (c) {
            case '$':                   /* $field */
                field = p;
                while (isalnum(*p)) p++;

                while (field < p)       /* putchar field..p */
                    putchar(*field++);

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

const char *str_next_tok(const char *start, char **tok) {
    const char *s = start;

    if (s == NULL) {
        *tok = NULL;
        return NULL;
    }

    while (*s && !isblank(s))
        s++;

    if (tok)
        *tok = g_strndup(start, s - start);

    if (*s == '\0')
        return NULL;

    while (isblank(++s)) ;
    if (*s == 0)
        return NULL;

    return s;
}

gboolean parse_component(const char *field, const char *s,
                         long *pval, const char **pnext) {
    long        val = 0L;
    const char *next = NULL;
    char *      end;

    // assert (s != NULL);
    val = strtol(s, &end, 0); /* may be hex */
    if (end == s) {
        error("Field %s: invalid number: \"%s\"\n", field, s);
        return FALSE;
    }

    while (isspace(*end)) end++;
    if (*end == '\0')
        end = NULL;

    if (pval) *pval = val;
    if (pnext) *pnext = next;
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
    long        val  = 0l;
    const char *next = NULL;

    val_text = g_hash_table_lookup(vartab, field);
    if (val_text != NULL) {
        if (! parse_component(field, val_text, &val, &next))
            return FALSE;
    }

    val += delta;

    val_text = g_strdup_printf("%ld %s", val, next ? next : "");

    g_hash_table_insert(vartab,
                        g_strdup(field),
                        val_text);

    while (next) {
        char *next_field;
        next = str_next_tok(next, &next_field);
        if (! field_refresh(next_field, 0))
            return FALSE;
    }
    return TRUE;
}

gboolean field_refresh(const char *field, int index) {
    char *      val_text;
    long        val  = 0l;
    const char *next = NULL;

    char *   exec_text;
    char *   base_text;

    GString *buf;

    val_text = g_hash_table_lookup(vartab, field);

    /* field = EMPTY */
    if (val_text != NULL) {
        if (! parse_component(field, val_text, &val, &next))
            return FALSE;
    }

    buf = g_string_sized_new(20);
    g_string_assign(buf, field);
    g_string_append(buf, ".exec");
    exec_text = g_hash_table_lookup(vartab, buf->str);
    if (exec_text) {                    /* has .exec */
        FILE *   proc_out = popen(exec_text, "r");
        GString *cap;
        char *   end;
        char     c;
        if (proc_out == NULL) {
            fprintf(stderr, "Failed to execute: %s: ", exec_text);
            perror("");
            return FALSE;
        }

        cap = g_string_sized_new(100);
        while (c = fgetc(proc_out) != EOF)
            g_string_append_c(cap, c);

        pclose(proc_out);

        val = strtol(cap->str, &end, 0);
        if (end == cap->str) {
            fprintf(stderr, "Expect an integer: %s\n", cap->str);
            return FALSE;
        }

        g_string_free(cap, TRUE);
    }

    g_string_assign(buf, field);
    g_string_append(buf, ".base");
    base_text = g_hash_table_lookup(vartab, buf->str);
    if (base_text) {                    /* has .base */
        /* .base += val, val = 0 */
        char *end;
        long  base_val = strtol(base_text, &end, 0);
        char *new_text;

        if (end == base_text) {
            fprintf(stderr, "Expect an integer from %s: %s\n",
                    buf->str, base_text);
            return FALSE;
        }

        base_val += val;
        val = 0L;

        g_hash_table_insert(vartab,
                            g_strdup(field),
                            g_strdup_printf("%ld", val));
    } else {
        val = 0L;
    }

    g_string_free(buf, TRUE);

    val_text = g_strdup_printf("%ld %s", val, next ? next : "");
    g_hash_table_insert(vartab,
                        g_strdup(field),
                        val_text);

    while (next) {
        char *next_field;
        next = str_next_tok(next, &next_field);
        if (! field_refresh(next_field, index + 1))
            return FALSE;
    }
    return TRUE;
}

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

    { "stdout",    'c', 0, G_OPTION_ARG_NONE, &opt_stdout,
      "write to stdout instead of save", },

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

    if (opt_incrfield == NULL) {
        fprintf(stderr, "Field to increase isn't specified. \n");
        return 1;
    }

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
