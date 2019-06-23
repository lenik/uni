#include "config.h"

#define RCS_ID      "$Id: - @VERSION@ @DATE@ @TIME@ - $"
#define DESCRIPTION "words"

#include <cprog.h>

#include "TLibrary.h"

static int opt_width = 64;
static int opt_height = 64;

static gchar *opt_font;
static gchar *opt_charmap;
static int opt_fontSize = 16;

static FT_UInt ft_platform_id;
static FT_UInt ft_encoding_id;
static FT_UInt ft_charmap;
static FT_UInt ft_faceIndex;
static FT_Bool ft_useKerning;

GOptionEntry options[] = {
    { "force",     'f', 0, G_OPTION_ARG_NONE, &opt_force,
      "Force to continue", },

    { "quiet",     'q', G_OPTION_FLAG_NO_ARG,
      G_OPTION_ARG_CALLBACK, (gpointer) set_verbose_arg,
      "Show less verbose info", },

    { "verbose",   'v', G_OPTION_FLAG_NO_ARG,
      G_OPTION_ARG_CALLBACK, (gpointer) set_verbose_arg,
      "Show more verbose info", },

    { "version",   '\0', G_OPTION_FLAG_NO_ARG,
      G_OPTION_ARG_CALLBACK, (gpointer) show_version,
      "Show version info", },

    { "width",     'w',  0, G_OPTION_ARG_INT, &opt_width,
      "Specify the canvas width", },

    { "height",    'h',  0, G_OPTION_ARG_INT, &opt_height,
      "Specify the canvas height", },

    { "font",      'f',  0, G_OPTION_ARG_STRING, &opt_font,
      "Specify the font file", },

    { "charmap",   'm',  0, G_OPTION_ARG_STRING, &opt_charmap,
      "Select the code map to use", },

    { "font-size", 's',  0, G_OPTION_ARG_INT, &opt_fontSize,
      "Specify the font size", },

    //    { G_OPTION_REMAINING, '\0', 0, G_OPTION_ARG_FILENAME_ARRAY, &opt_files,
    //     "TEXT", },

    { NULL },
};

TLibrary *lib;

int main(int argc, char **argv) {
    if (! boot(&argc, &argv, "TEXT"))
        return 1;

    lib = new TLibrary();
    if (! lib->init())
        return 1;

    bool generate(const char *);
    for (int i = 0; i < argc; i++)
        generate(argv[i]);

    delete lib;
    return 0;
}

int process_file(char *filename, FILE *file) {
    LOG1 printf("Processing file %s\n", filename);
    return 0;
}

bool generate(const char *str) {
    return true;
}
