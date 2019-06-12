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

int process_file(char *filename, FILE *file) {
    char *ext = strrchr(filename, '.');
    if (ext) ext++;

    if (pathv) {
        g_list_free(pathv);
        pathv = NULL;
    }
    if (opt_libpath) {
        char **p = opt_libpath;
        while (*p)
            pathv = g_list_append(pathv, *p++);
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
    Buffer buf;
    char *p;
    while (p = Buffer_fgets(&linebuf)) {
        lineNo++;

        /* save a copy of the line */
        while (*p && isspace(*p)) p++; // trim-left
        
        int len = strlen(p);
        while (len && isspace(p[len - 1])) len--; // chomp and trim-right

        char *line = strndup(p, len);
        p = line;
        
        /* catme cmds must be within the delimitors */
        if ((strncmp(p, context->startSeq, n) == 0)
            && strcmp(s + (n - context->nStopSeq), context->stopSeq) == 0) {
            s = substr(s, $nStartSeq, n - context->nStopSeq); /* remove startSeq and stopSeq */
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
