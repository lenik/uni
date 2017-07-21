
void set_delim(const char *del, const char *der) {
    Frame *frame = Stack_peek();
    SET_STRDUP(frame->del, del);
    SET_STRDUP(frame->der, der);
}

void set_format_c() {
    set_delim("/*", "*/");
}

void set_format_unix() {
    set_delim("#", "");
}

void set_format_sql() {
    set_delim("--", "");
}

void set_format_xml() {
    set_delim("<!--", "-->");
}

static GOptionEntry options[] = {
    { "lib", 'L', 0, G_OPTION_ARG_FILENAME_ARRAY, &opt_libpath,
      "Add to the library search path", },
    
    { "echo", 'e', 0, G_OPTION_ARG_NONE, &opt_echo,
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

    { G_OPTION_REMAINING, 0, 0, G_OPTION_ARG_FILENAME_ARRAY, &opt_files,
      "FILES", },

    { NULL },
};
