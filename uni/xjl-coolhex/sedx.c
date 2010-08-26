#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <glib.h>

#define LOG1 if (verbose >= 1)
#define LOG2 if (verbose >= 2)

#define MAX_WORD 8
#define MAX_LOOK MAX_WORD

typedef int val_t;

void version();
void help();

void errlog(const char *fmt, ...) {
    va_list args;
    va_start(args, fmt);
    vfprintf(stderr, fmt, args);
    va_end(args);
    fflush(stderr);
}

typedef GSList GStack;
GStack *stack_push(GStack *stack, gpointer data) {
    return g_slist_prepend(stack, data);
}

GStack *stack_pop(GStack *stack, gpointer* pdata) {
    GStack * next = stack->next;
    *pdata = stack->data;
    g_slist_free_1(stack);
    return next;
}

guint vartab_hash(gconstpointer key) {
    unsigned *pindex = (unsigned *)key;
    unsigned index = *pindex;
    guint hash = ((index ^ 0xcdcdcdcd) * 0x3457c) ^ index;
    return hash;
}

gboolean vartab_equals(gconstpointer a,
                       gconstpointer b) {
    unsigned *pindexa = (unsigned *) a;
    unsigned *pindexb = (unsigned *) b;
    return *pindexa == *pindexb;
}

GStack *    instack = NULL;             /* (pointer) ftell() */
GStack *    pcstack = NULL;             /* (pointer) pc */
GStack *    valstack = NULL;            /* (pointer) val_t */
GHashTable *vartab = NULL;              /* arg_val -> (pointer) val_t */

GString *   program = NULL;             /* program */
char *      pc;                         /* program pointer */
gboolean    arg_present = FALSE;
int         arg_val;

const char *filename = "-";
FILE *      in = NULL;
gboolean    in_truncate = FALSE;
char        lookbuf[MAX_LOOK + 1];      /* lookahead buffer of in */
int         look_start = 0;
int         look_end = 0;               /* at max sizeof(lookbuf) - 1 is used */
gboolean    underflow = FALSE;

gboolean    val_looked = FALSE;         /* val_look is valid? */
val_t       val_look = 0;               /* look-ahead val */
int         val_size = 1;               /* sizeof(val) in bytes */
gboolean    val_le = TRUE;              /* little-endian by default */

int read();                             /* return EOF if end */
gboolean lookahead(int size);           /* read more chars to fulfill size */
void invalidate();                      /* invalidate the look-buf and look-val */
int error_underflow();                  /* always return -1 */
val_t lookval();                        /* val = look_xx(val_size) */
val_t readval();                        /* val = read_xx(val_size) */
val_t forcelook(val_t val);             /* modify lookahead. */

/* persist look_val to lookbuf, and lookbuf to entire-file-cache if possible. */
void persistlook();

typedef void (*OUTFUNC)(val_t);

void _out_copy_s(val_t);
void _out_copy_r(val_t);

#ifndef LITTLE_ENDIAN
#define LITTLE_ENDIAN
#endif

#ifdef LITTLE_ENDIAN
#  define out_copy_le _out_copy_s
#  define out_copy_be _out_copy_r
#else
#  define out_copy_le _out_copy_r
#  define out_copy_be _out_copy_s
#endif

#define MAX_RADIX 36
const char *ctab_lc = "0123456789abcdefghijklmnopqrstuvwxyz";
const char *ctab_uc = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

int out_width = 0;
int out_fill = ' ';
int out_radix = 10;
const char *out_ctab;
void out_numeric(val_t);

OUTFUNC output = out_copy_le;

gboolean    stop_eof = FALSE;         /* continue when reached EOF */
int         verbose  = 0;

int main(int argc, char **argv) {

    gboolean fsect = FALSE;             /* 0=options, 1=files */

    int c;                              /* code */
    val_t val;
    gboolean stop = FALSE;              /* stop the program? */

    --argc; ++argv;

    while (argc && !fsect) {
        char *arg = *argv;
        char a;

        if (*arg == '-') {
            argc--;
            argv++;

            arg++;
            while (a = *arg++) {
                switch (a) {
                case '-':               /* --? */
                    if (*arg == 0) {    /* -- */
                        fsect = TRUE;
                        continue;       /* this will jump to while (argc).  */
                    }
                    if (! strcmp("help", arg)) {
                        help();
                        return 0;
                    }
                    else if (! strcmp("version", arg)) {
                        version();
                        return 0;
                    }
                    else {
                        fprintf(stderr, "Invalid option: %s\n", arg);
                        return 1;
                    }
                    break;
                case 'e':               /* -e PROGRAM */
                    if (program == NULL)
                        program = g_string_sized_new(1024);
                    if (*arg)
                        program = g_string_append(program, arg);
                    else if (argc) {
                        argc--;
                        program = g_string_append(program, *argv++);
                    } else {
                        fprintf(stderr, "PROGRAM is expected. \n");
                        return 1;
                    }
                    break;
                case 'q':
                    verbose--;
                    break;
                case 'v':
                    verbose++;
                    break;
                case 'h':
                    help();
                    return 0;
                default:
                    fprintf(stderr, "Invalid option: %c\n", a);
                    return 1;
                } /* switch opt */
            } /* foreach opt-char */
        } // if '-'
        else                            /* files... */
            break;
    } // while argc

    if (argc) {
        filename = argv[0];
        if (argc > 1) {
            fprintf(stderr, "Unexpected argument: %s\n", argv[1]);
            return 1;
        }
    }

    if (program == NULL) {
        fprintf(stderr, "Program isn't specified. \n");
        return 1;
    }
    pc = program->str;
    LOG1 errlog("program: %s\n", pc);

    if (! strcmp(filename, "-"))
        in = stdin;
    else {
        in = fopen(filename, "r+b");    /* + for random access */
        if (in == NULL) {
            fprintf(stderr, "Can't open %s: ", filename);
            perror("");
            return 1;
        }
    }
    LOG1 errlog("filename: %s\n", filename);

    vartab = g_hash_table_new(vartab_hash, vartab_equals);

    while ((c = *pc++) && !stop) {

        if (c >= '0' && c <= '9') {     /* numeric argument */
            int off = 0;
            int radix = 10;
            int digit = c - '0';
            int num = digit;
            if (digit == 0)
                radix = 8;
            while (c = *pc++) {
                off++;
                if (c >= '0' && c <= '9')
                    digit = c - '0';
                else {
                    if (off == 1 && (c == 'x' || c == 'X') && radix == 8) {
                        radix = 16;
                        continue;       /* 0x... */
                    }
                    if (c >= 'a' && c <= 'z')
                        digit = c - 'a' + 10;
                    else if (c >= 'A' & c <= 'Z')
                        digit = c - 'A' + 10;
                    else
                        break;          /* invalid digit, reject. */
                }
                if (digit >= radix)
                    break;              /* invalid digit, reject. */
                num = num * radix + digit;
            }
            arg_val = num;
            arg_present = TRUE;
        } /* [0-9]+ */
        else if (c == '\'') {
            c = *pc++;
            if (! c) {
                fprintf(stderr, "Expect '<char>. \n");
                return 1;
            }
            arg_val = c;
            arg_present = TRUE;

            c = *pc++;
        } /* '. */

        if (c == 0)                     /* program end. */
            break;

        LOG2 {
            errlog("PC: %c\n", c);
            if (arg_present)
                errlog("  with-arg: %d\n", arg_val);
        }

        // assert(c < '0' && c > '9');
        switch (c) {
        case ' ':                       /* comments */
        case '\t':
        case '\n':
            continue;

        case '[':
            if (arg_present) {
                /* condition variable to use... */
            }
            pcstack = stack_push(pcstack, pc);
            break;
        case ']':
            if (pcstack == NULL) {
                errlog("Unmatched []\n");
                stop = TRUE;
                break;
            }
            {
                // char *saved_pc = pcstack->data;
            }
            break;

        case '<':                       /* in -= # */
            if (! arg_present)
                arg_val = 1;
            /* XXX - fseek doesn't work well... */
            if (fseek(in, SEEK_CUR, -arg_val * val_size) != 0) {
                perror("Seek failed: ");
                stop = 1;
            }
            invalidate();
            break;

        case '>':                       /* in += # */
            if (! arg_present)
                arg_val = 1;
            if (arg_val != 0) {
                arg_val *= val_size;
                while (arg_val--)
                    read();
            }
            val_looked = FALSE;
            break;

        case '.':                       /* out(val = *in++) */
            if (! arg_present)
                arg_val = 1;
            while (arg_val--) {
                val = readval();
                if (underflow) {
                    stop = stop_eof;
                    break;
                }
                output(val);
            }
            break;

        case ':':                       /* out(val = *in) */
            if (arg_present)
                val = arg_val;
            else {
                val = lookval();
                if (underflow) {
                    stop = stop_eof;
                    break;
                }
            }
            output(val);
            break;

        case ',':                       /* out(val = #) */
            val = lookval();
            if (arg_present)
                val = forcelook(arg_val);
            /* ignore underflow */
            break;

        case ';':                       /* out(val = #), in++ */
            val = readval();
            if (arg_present)
                val = arg_val;
            if (underflow) {
                stop = stop_eof;
                break;
            }
            output(val);
            break;

        case '?':                       /* check if (val == #) */
            /* Not implmented. */
            break;
        case '=':
        case '!':                       /* assert(val == #) */
            if (! arg_present)
                arg_val = 0;
            val = lookval();
            LOG2 errlog("check %d against %d\n", arg_val, val);
            if (val != arg_val)
                stop = TRUE;
            break;

        case '$':                       /* Store */
            val = lookval();
            if (underflow) {
                stop = stop_eof;
                break;
            }
            if (arg_present) {          /* var[#] = val */
                g_hash_table_insert(vartab, &arg_val, (gpointer) &val);
            } else {                    /* push(val) */
                valstack = stack_push(valstack, (gpointer) val);
            }
            break;

        case '@':                       /* Restore */
            val =  lookval();           /* ignore underflow error. */
            if (arg_present) {          /* val = var[#] */
                if (! g_hash_table_lookup_extended(vartab,
                                                   &arg_val, /* lookup_key */
                                                   NULL,     /* orig_key */
                                                   (gpointer *) &val /* value */
                                                   )) {
                    /* +option for default 0? */
                    LOG2 errlog("Warning: variable isn't set: %d\n", arg_val);
                    stop = TRUE;
                    break;
                }
            } else {                    /* val = pop */
                if (valstack == NULL) {
                    errlog("Error: stack underflow. \n");
                    stop = TRUE;
                    break;
                }
                valstack = stack_pop(valstack, (gpointer *) &val);
            }
            /* Update the val_look cache, directly */
            val_look = val;
            val_looked = TRUE;          /* always set the validity */
            break;

        case '+':
        case '-':
        case '*':
        case '/':
        case '%':
        case '&':
        case '|':
        case '^':
            errlog("stack algorithms is not implemented. \n");
            stop = TRUE;
            break;

        case 'e':                       /* little-endian */
            val_le = TRUE;
            if (output == out_copy_be)
                output = out_copy_le;
            break;
        case 'E':                       /* big-endian */
            val_le = FALSE;
            if (output == out_copy_le)
                output = out_copy_be;
            break;

        case 's':                       /* 1s 2s 4s */
            if (arg_val > MAX_WORD) {
                errlog("Invalid word size: %d\n", arg_val);
                stop = TRUE;
            } else {
                val_size = arg_val;
                val_looked = FALSE;     /* invalidate the look-ahead val */
            }
            break;

        case 'c':                       /* copy */
            output = val_le ? out_copy_le : out_copy_be;
            break;

        case 'n':                       /* numeric */
        case 'N':
            out_ctab = c == 'n' ? ctab_lc : ctab_uc;

            if (arg_present)
                out_width = arg_val;
            else
                out_width = 0;          /* variable-width */
            output = out_numeric;
            break;

        case 'f':
            if (! arg_present)
                arg_val = ' ';
            out_fill = arg_val;
            break;

        case 'x':
            if (arg_present) {
                if (arg_val < 2) {
                    errlog("Bad radix value: %d\n", arg_val);
                    stop = TRUE;
                    break;
                }
                if (arg_val > MAX_RADIX) {
                    errlog("Radix is too big: %d\n", arg_val);
                    stop = TRUE;
                    break;
                }
                out_radix = arg_val;
            } else
                out_radix = 10;
            break;

        case '"':                       /* verbatim output */
            {
                char q = c;
                gboolean escaped = FALSE;
                while (c = *pc) {
                    pc++;
                    if (escaped) {
                        switch (c) {
                        case 'n': c = '\n'; break;
                        case 't': c = '\t'; break;
                        case '0': c = '\0'; break;
                        }
                        escaped = FALSE;
                    } else {
                        if (c == '\\') {
                            escaped = TRUE;
                            continue;
                        }
                        if (c == q)
                            break;
                    }
                    putchar(c);
                }
            }
            break;

        case '#':                       /* truncate in */
            in_truncate = TRUE;
            break;
        default:
            errlog("Invalid code: %c\n", c);
            return 2;
        }

        arg_present = FALSE;

    } /* while pc  */

    if (! in_truncate) {
        int d;
        while (look_start != look_end) {
            d = lookbuf[look_start++];
            look_start %= sizeof(lookbuf);
            putchar(d);
        }
        while ((d = fgetc(in)) != EOF) {
            putchar(d);
        }
    }

    fclose(in);
    return 0;
}

void version() {
    printf("sedx 0.1 by Lenik\n");
}

void help() {
    version();

    printf(
           "Syntax: \n"
           "    sedx OPTIONS [ FILE | '-' ]\n"
           "\n"
           "Options: \n"
           "    -e PROGRAM     Evaluate the program\n"
           "    -q, --quiet    Show less verbose info\n"
           "    -v, --verbose  Show more verbose info\n"
           "    -h, --help     Show this help page\n"
           "        --version  Show version\n"
           );
}

int read() {
    int c;
    if (look_start != look_end) {
        c = lookbuf[look_start++];
        look_start %= sizeof(lookbuf);
    } else {
        c = fgetc(in);
    }
    return c;
}

/**
 * @param size must be less then sizeof(lookbuf).
 */
gboolean lookahead(int size) {
    int c;
    int nlook = look_end - look_start;
    if (nlook < 0)
        nlook += sizeof(lookbuf);

    while (nlook < size) {
        c = fgetc(in);
        if (c == EOF)
            return FALSE;
        lookbuf[look_end++] = c;
        look_end %= sizeof(lookbuf);
        nlook++;
    }
    return TRUE;
}

void invalidate() {
    look_start = look_end = 0;
    val_looked = FALSE;
}

int error_underflow() {
    underflow = TRUE;
    errlog("Error: input underflow. \n");
    return -1;
}

val_t lookval() {
    val_t    val = 0;
    int      size = val_size;
    int      look_off;

    if (val_looked)
        return val_look;

    if (! lookahead(size))
        return error_underflow();

    if (val_le) {
        look_off = (look_start + size) % sizeof(lookbuf);
        while (size--) {
            val <<= 8;
            val += lookbuf[--look_off];
            if (look_off < 0)
                look_off += sizeof(lookbuf);
        }
    } else {
        look_off = look_start;
        while (size--) {
            val <<= 8;
            val += lookbuf[look_off++];
            look_off %= sizeof(lookbuf);
        }
    }

    val_looked = TRUE;
    return val_look = val;
}

val_t readval() {
    val_t val = 0;
    int size = val_size;
    int c;

    /* Optimized. */
    if (val_looked) {
        val_looked = FALSE;
        look_start += val_size;
        look_start %= sizeof(lookbuf);
        return val_look;
    }

    if (val_le) {                       /* OPT... */
        unsigned char buf[MAX_WORD];    /* [size] */
        unsigned char *pbuf = buf;
        int n = size;
        while (n--) {
            c = read();
            if (c == EOF)
                return error_underflow();
            *pbuf++ = (unsigned char) c;
        }
        while (size--) {
            val <<= 8;
            val += *--pbuf;
        }
    } else {
        while (size--) {
            c = read();
            if (c == EOF)
                return error_underflow();
            val <<= 8;
            val += c;
        }
    }

    return val;
}

val_t forcelook(val_t val) {
    val_look = val;
    val_looked = TRUE;
    return val;
}

void persistlook() {
    /* not implemented, yet */
}



void _out_copy_s(val_t val) {
    char *p = (char *) &val;
    int n = val_size;
    while (n--)
        putchar(*p++);
}

void _out_copy_r(val_t val) {
    char *p = (char *) &val + val_size;
    int n = val_size;
    while (n--)
        putchar(*--p);
}

void out_numeric(val_t val) {
    char buf[32];
    char *p = buf;
    int width;
    int i;

    if (val == 0) {
        *p++ = '0';
    } else
        while (val) {
            int digit = val % out_radix;
            *p++ = out_ctab[digit];
            val /= out_radix;
        }

    width = (p - buf);                  /* width / sizeof(char) ? */
    for (i = width; i < out_width; i++)
        putchar(out_fill);

    p = buf + width;
    while (width--)
        putchar(*--p);
}
