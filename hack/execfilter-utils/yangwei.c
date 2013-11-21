#include <assert.h>
#include <errno.h>
#include <stdio.h>
#include <string.h>

#include "ywcrypt.h"

#define BLOCK_SIZE 1024
#define LINEMAX 4095

static int process(int encrypt, FILE *in, FILE *out, int bs) {
    int str = encrypt && bs < 0;
    char *buf;
    int cb;

    if (str)
        bs = LINEMAX + 1;
    if (bs == 0)
        bs = BLOCK_SIZE;

    buf = (char *) malloc(bs + 1);

    while (! feof(in)) {
        if (str)
            if (fgets(buf, bs, in) == NULL)
                return 0;
            else
                cb = strlen(buf);
        else
            cb = fread(buf, 1, bs, in);
        buf[cb] = 0;

        if (ferror(in))
            return 1;

        if (encrypt) {
            xor_encrypt(buf, cb);
            garble_encrypt(buf, cb);
        } else {
            garble_decrypt(buf, cb);
            fprintf(stderr, "Gd=%s\n", buf);
            xor_decrypt(buf, cb);
            fprintf(stderr, "Xd=%s\n", buf);
        }

        fwrite(buf, 1, cb, out);
        if (ferror(out))
            return 2;
    }

    return 0;
}

char *program;
char opt_mode;

int main(int argc, char **argv) {
    int eoo = 0;
    char *arg;
    size_t len;
    int files = 0;

    opt_mode = '?';
    program = *argv++;
    argc--;

    while (argc-- > 0) {
        arg = *argv++;

        if (*arg == '-' && !eoo) {
            switch (arg[1]) {
            case 'h':
                fprintf(stderr, "syntax: %s -e/-d filename\n", program);
                return 1;
            case 'e':
                opt_mode='e';
                continue;
            case 'd':
                opt_mode='d';
                continue;
            case '-':
                eoo = 1;
                continue;
            }
            fprintf(stderr, "Bad option: %s\n", arg);
            return 1;
        }

        if (opt_mode == '?') {
            fprintf(stderr, "Either encrypt(-e) or decrypt(-d) must be specified.\n");
            return 1;
        }


        FILE *f = fopen(arg, "rb");
        if (f == NULL) {
            fprintf(stderr, "Failed to open file %s", arg);
            perror("");
            return 2;
        }

        fseek(f, 0L, SEEK_END);
        len = ftell(f);
        fseek(f, 0L, SEEK_SET);

        if (process(opt_mode == 'e', f, stdout, len) != 0) {
            perror("Process error");
            return 3;
        }

        fclose(f);
        files++;
    } /* for args */

    if (files == 0 && opt_mode != '?') {
        fprintf(stderr, ""
                "Warning: yangwei encryption doesn't support streaming, the result of the\n"
                "line-based encryption can differ from the block-based encryption, and you won't\n"
                "be able to decrypt the secret bin without line lengths.\n");
        process(opt_mode == 'e', stdin, stdout, -1);
    }

    return 0;
}
