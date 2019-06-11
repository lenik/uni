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

    Frame *frame = Stack_enter();
    
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

    char *dir = file;
    char *base = strrchr(file, '/');
    if (base == NULL) {
        dir = strdup(".");
        base = file;
    } else {
        dir = strndup(file, base - file);
        base++;
    }
    frame->dir = dir;

    char *name = base;
    char *ext = strrchr(base, '.');
    if (ext == NULL) {
        name = strdup(base);
    } else {
        name = strndup(base, ext - name);
        ext = strdup(ext + 1);
    }
    // NEED: free(name);
    frame->ext = ext;

    char fqn[PATH_MAX];
    if (streq(dir, ".")) {
        strcpy(fqn, name);
    } else {
        strcpy(fqn, dir);
        strcat(fqn, "/");
        strcat(fqn, name);
        replace(fqn, "/", ".");
    }

    char *delim = opt_delim;
    if (delim == NULL) {
        SrcLang lang = SrcLang_parse(ext);
        Frame_initExt(frame, ext);
        LOG2 printf("Auto determined the delimiters from file extension.\n");
    }
    
    char *startSeq = delim[0];
    char *stopSeq = delim[1];
    LOG2 printf("Delimiter: %s(%d), %s(%d)\n",
        startSeq, nStartSeq, stopSeq, nStopSeq);
    LOG2 printf("Inline-Delimiter: %s(%d), %s(%d)\n",
        startSeq1, nStartSeq1, stopSeq1, nStopSeq1);
    
    frame->echo = 0;
    frame->copy = 1;

    File *f = fopen(file, "rt");
    if (f == NULL) {
        fprintf(stderr, "Can't open %s", file);
        perror("");
        return 1;
    }

    int line = 0;
    char line[4000];
    while (fgets(line, sizeof(line), f)) {
        line++;

        /* save a copy of the line */
        char *s = strdup(line);
        s = trimLeft(s);
        s = chomp(s);
        int n = strlen(s);

        /* catme cmds must be within the delimitors */
        if ((strncmp(s, startSeq, n) == 0)
            && strcmp(s + (n - nStopSeq), stopSeq) == 0) {
            s = substr($s, $nStartSeq, $n - $nStopSeq); /* remove startSeq and stopSeq */
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
    Stack_leave();
    return err;
}
