#include "config.h"

#define RCS_ID      "$Id: - @VERSION@ @DATE@ @TIME@ - $"
#define DESCRIPTION "Stupid Text Concatenater"

#include <stdbool.h>
#include <stdlib.h>
#include <cprog.h>

#include "File.h"
#include "FileSearcher.h"
#include "Frame.h"
#include "Options.h"

char *pkgdatadir = "@pkgdatadir@";
char opt_configdir[PATH_MAX];

FileSearcher *fileSearcher = NULL;
Frame rootFrame;
Frame *context = &rootFrame;

int main(int argc, char **argv) {
    if (! boot(&argc, &argv, "FILES"))
        return 1;

    if (pkgdatadir[0] == '@')
        pkgdatadir = "/usr/share/catme";

    char *HOME = getenv("HOME");
    if (! HOME) HOME = ".";
    strcpy(opt_configdir, HOME);
    strcat(opt_configdir, "/.config/catme");

    FileSearcher_addPathEnv("LIB");
    FileSearcher_dump();

    return process_files(opt_files);
}

int process_file(char *fileArg, FILE *file) {
    char *base = strrchr(fileArg, '/');
        if (base) base++;
        else base = fileArg;

    char *ext = strrchr(base, '.');
        if (ext) ext++;
        else ext = NULL;

    if (fileSearcher->pathList) {
        g_list_free(fileSearcher->pathList);
        fileSearcher->pathList = NULL;
    }

    if (opt_libpath) {
        char **p = opt_libpath;
        while (*p) {
            fileSearcher->pathList = g_list_append(fileSearcher->pathList,
                strdup(*p));
            p++;
        }
    }

    if (ext) {
        char pathdir[PATH_MAX];
        strcpy(pathdir, opt_configdir);
        strcat(pathdir, "/path/");
        strcat(pathdir, ext);
        loadPathFiles(pathdir);

        strcpy(pathdir, pkgdatadir);
        strcat(pathdir, "/path/");
        strcat(pathdir, ext);
        loadPathFiles(pathdir);

        SrcLang lang = SrcLang_parse(ext);
        if (lang) {
            char var[100];
            strcpy(var, SrcLang_toString(lang));
            strcat(var, "LIB");
            strupper(var);
            char *langLIB = getenv(var);
            if (langLIB) {
                char *tok = strtok(langLIB, ":");
                do {
                    pathv = g_list_append(pathv, strdup(tok));
                } while (tok = strtok(NULL, ":"));
            }
        }
    }

    return 0;
}

int process(char *file, ...) {
    LOG2 printf("Process %s", file);

    Frame *frame = Frame_new(context, file);

    LOG2 printf("Delimiter: start %s(%d), stop %s(%d), sl-start %s(%d)\n",
        startSeq, nStartSeq, stopSeq, nStopSeq, slStartSeq, nSlStartSeq);

    frame->echo = 0;
    frame->copy = 1;

    GHashTable *vars = g_hashtable_new();
    int argi;
    for (argi = 0; argi < narg; argi++) {
        char *assign = strchr(arg, ":=");
        char *k;
        if (assign == NULL)
            k = argi;
        else {
            k = strndup(arg, 0, assign - arg);
            arg = assign + 2;
        }
        g_hashtable_set(vars, k, arg);
    }

    File *f = fopen(file, "rt");
    if (f == NULL) {
        fprintf(stderr, "Can't open %s", file);
        perror("");
        return 1;
    }

    int lineNo = 0;
    g_string *buf;
    const char *p;
    while (FileStream_readLine(f, buf, true)) {
        lineNo++;
        p = buf->str;

        /* save a copy of the line */
        p = Chars_trimLeft(p);
        *Chars_trimRight(p) = 0;

        char *line = strndup(p, len);
        p = line;

        /* catme cmds must be within the delimitors */
        if ((strncmp(p, context->startSeq, n) == 0)
            && strcmp(s + (n - context->nStopSeq), context->stopSeq) == 0) {
            s = substr(s, nStartSeq, n - context->nStopSeq); /* remove startSeq and stopSeq */
            s = trimLeft(s);

            if (*s == '\\') {
                err = parse_cmd(s);
                if (err) {
                    fprintf(stderr, "    at %s:%d\n", file, line);
                    break;
                } else {
                    continue;
                }
            }
        }

        if (frame->copy) {
            char *exp = VarExpr_expand(line, vars);
            puts(exp);
        }
    } /* while fgets */
    fclose(f);
    context = context->parent;
    return err;
}
