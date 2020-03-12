#include "CatMe.h"
#include "Options.h"

#define OPTION_FN(fn_name) \
    fn_name(const gchar *option_name, \
            const gchar *value, \
            gpointer data, \
            GError **error)

Options cmdOptions = {
    NULL, /* char *libPath; */
    false, /* gboolean opt_echo; */
};

gboolean OPTION_FN(set_format_c) {
    Frame_setSrcLang(context, SrcLang_C);
    return TRUE;
}

gboolean OPTION_FN(set_format_sql) {
    Frame_setSrcLang(context, SrcLang_SQL);
    return TRUE;
}

gboolean OPTION_FN(set_format_unix) {
    Frame_setSrcLang(context, SrcLang_UNIX);
    return TRUE;
}

gboolean OPTION_FN(set_format_xml) {
    Frame_setSrcLang(context, SrcLang_XML);
    return TRUE;
}

gboolean OPTION_FN(set_verbose_arg) {
    char *p = option_name;
    while (*p == '-') p++;
    if (*p == 'q') {                  /* q, quiet */
        opt_verbose--;
    } else if (*p == 'v') {           /* v, verbose */
        opt_verbose++;
    }
    return TRUE;
}

void OPTION_FN(show_version) {
    printf("catme 2.0\n"
           "written by Lenik, (at) 99jsj.com\n");
    exit(0);
}

static GOptionEntry options[] = {
    { "lib", 'L', 0, G_OPTION_ARG_FILENAME_ARRAY, &options.path,
      "Add to the library search path", },

    { "echo", 'e', 0, G_OPTION_ARG_NONE, &options.echo,
      "Preserve catme commands in output", },

    { "format-c", 0, G_OPTION_FLAG_NO_ARG, G_OPTION_ARG_CALLBACK, set_format_c,
      "Use /* ... */ delimitors", },

    { "format-unix", 0, G_OPTION_FLAG_NO_ARG, G_OPTION_ARG_CALLBACK, set_format_unix,
      "Use # ... delimitors", },

    { "format-sql", 0, G_OPTION_FLAG_NO_ARG, G_OPTION_ARG_CALLBACK, set_format_sql,
      "Use -- ... delimitors", },

    { "format-xml", 0, G_OPTION_FLAG_NO_ARG, G_OPTION_ARG_CALLBACK, set_format_xml,
      "Use <!-- ... --> delimitors", },

    { "quiet", 'q', G_OPTION_FLAG_NO_ARG, G_OPTION_ARG_CALLBACK, set_verbose_arg,
      "Show less verbose info", },

    { "verbose", 'v', G_OPTION_FLAG_NO_ARG, G_OPTION_ARG_CALLBACK, set_verbose_arg,
      "Show more verbose info", },

    { "version", 0, G_OPTION_FLAG_NO_ARG, G_OPTION_ARG_CALLBACK, show_version,
      "Show version info", },

    { G_OPTION_REMAINING, 0, 0, G_OPTION_ARG_FILENAME_ARRAY, &options.files,
      "FILES", },

    { NULL },
};
