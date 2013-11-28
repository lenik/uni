#include <bas/cli.h>

static GOptionEntry options[] = {
    { "force",     'f', 0, G_OPTION_ARG_NONE, &opt_force,
      "Force to overwrite existing files", },

    { "quiet",     'q', G_OPTION_FLAG_NO_ARG,
      G_OPTION_ARG_CALLBACK, parse_option,
      "Show less verbose info", },

    { "verbose",   'v', G_OPTION_FLAG_NO_ARG,
      G_OPTION_ARG_CALLBACK, parse_option,
      "Show more verbose info", },

    { "version",   '\0', G_OPTION_FLAG_NO_ARG,
      G_OPTION_ARG_CALLBACK, parse_option,
      "Show version info", },

    { NULL },
};

int main(int argc, char **argv) {
    program_title = "Show line numbers";
    program_help_args = "FILES";

    if (! parse_options(options, &argc, &argv)) {
        error("Illegal cmdline syntax.");
        return 1;
    }

    while (--argc > 0) {
        argv++;
        printf("arg: %s\n", *argv);
    }

    return 0;
}

gboolean parse_option(const char *opt, const char *val,
                      gpointer data, GError **err) {
    return _parse_option(opt, val, data, err);
}
