#include <stdargs.h>
#include <stdbool.h>

GHashTable *imported;

int parse_cmd(char *s) {
    // for (@_) {
    char *cmd;
    Frame *frame = Stack_peek();
    // while ($s =~ s/^\\([A-Za-z\-_]+)\b\s*//) {
    while (cmd = shiftCmd(s)) {
        if (streq(cmd, "dnl")
            || streq(cmd, "comment"))
            return 0;
        else if (streq(cmd, "echo"))
            frame->echo = true;
        else if (streq(cmd, "skip"))
            frame->copy = false;
        else if (streq(cmd, "noskip"))
            frame->copy = true;

        int err;
        // my @args = split(/\s+/, $s);
        if (streq(cmd, "import"))
            err = fn_import();
        if (streq(cmd, "include"))
            err = fn_include();
        if (streq(cmd, "mixin"))
            err = fn_mixin();
        if (streq(cmd, "sinclude"))
            err = fn_sinclude();
        else {
            fprintf(stderr, "Illegal command: %s\n", cmd);
            return 1;
        }
        int err = callback(va_args);
        if (err)
            return err;
    }
    return 0;
}

int _fn_import(bool once, char *qName, ...) {
    if (once && g_hashtable_get(qName)) {
        LOG2 printf("already imported: %s", qName);
        return 0;
    }
    g_hashtable_set(imported, qName, 1);

    char *href = Frame_qName2Href(context, qName);
    GList *hits = FileSearcher_search(fileSearcher, href);
    int nhit = g_list_size(hits);
    if (nhit == 0) {
        fprintf(stderr, "Import failed, not found: %s\n", href);
        FileSearcher_dump(fileSearcher);
    }

    char *dst = (char *) hits->data;
    return process(dst, va_args);
}

int fn_import(char *qName, ...) {
    return _fn_import(true, qName, va_args);
}

int fn_mixin(char *qName, ...) {
    return _fn_import(false, qName, va_args);
}

int _fn_include(bool silent, char *href, ...) {
    Frame *frame = Stack_peek();
    char *workdir = frame->dir;
    GList *hits = search(href, workdir, vpath);
    int nhit = g_list_size(hits);
    if (nhit == 0)
        if (silent)
            return 0;
        else {
            fprintf(stderr, "Include failed, not found: %s\n", href);
            return 1;
        }

    char *dst = (char *) hits->data;
    return process(dst, va_args);
}

int fn_include(char *href, ...) {
    return _fn_include(0, va_args);
}

int fn_sinclude(char *href, ...) {
    return _fn_include(1, va_args);
}
