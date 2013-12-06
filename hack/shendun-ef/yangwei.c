#include <assert.h>
#include <errno.h>
#include <stdio.h>
#include <string.h>

#define LOG_IDENT "@PACKAGE_NAME@"
#include <bas/cli.h>
#include <bas/file.h>

#include "ywcrypt.h"

static GOptionEntry options[] = {
    OPTION('e', "encrypt", "Encrypt file data"),
    OPTION('d', "decrypt", "Decrypt file data"),
    OPTION('q', "quiet", "Show less verbose info"),
    OPTION('v', "verbose", "Show more verbose info"),
    OPTION(0, "version", "Show version info"),
    { NULL },
};

char opt_mode;

int process(int encrypt, const char *path, FILE *in, FILE *out);

gboolean parse_option(const char *_opt, const char *val,
                      gpointer data, GError **err) {
    const char *opt = _opt;

    bool shortopt = opt++[1] != '-';
    while (*opt == '-')
        opt++;

    switch (*opt) {
    case 'e':
        if (shortopt || streq(opt, "encode")) {
            opt_mode = 'e';
            return true;
        }
        break;
    case 'd':
        if (shortopt || streq(opt, "decode")) {
            opt_mode = 'd';
            return true;
        }
        break;
    }

    return _parse_option(_opt, val, data, err);
}

int main(int argc, char **argv) {
    program_title = "Yangwei Encrypt Utility";
    program_help_args = "FILES";

    opt_mode = '?';

    if (! parse_options(options, &argc, &argv))
        return 1;

    if (opt_mode == '?') {
        log_err("Either encrypt(-e) or decrypt(-d) must be specified.");
        return 1;
    }

    argc--;
    argv++;

    if (argc > 0)
        while (argc-- > 0) {
            char *path = *argv++;

            FILE *f = fopen(path, "rb");
            if (f == NULL) {
                log_perr("Failed to open file %s", path);
                return 2;
            }

            if (process(opt_mode == 'e', path, f, stdout) != 0) {
                log_perr("Process error");
                return 3;
            }

            fclose(f);
        } /* for args */
    else
        process(opt_mode == 'e', "<stdin>", stdin, stdout);

    return 0;
}

int process(int encrypt, const char *path, FILE *in, FILE *out) {
    char *data;
    size_t size;

    data = _load_file(in, path, &size, 0, 1);
    if (data == NULL)
        return 1;

    if (encrypt) {
        yw_encrypt(data, size);
    } else {
        yw_decrypt(data, size);
    }

    fwrite(data, 1, size, out);
    if (ferror(out))
        return 2;

    return 0;
}
