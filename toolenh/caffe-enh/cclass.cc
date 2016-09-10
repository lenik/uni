#include "config.h"

#define RCS_ID      "$Id: - @VERSION@ @DATE@ @TIME@ - $"
#define DESCRIPTION "Caffe Classifier"

#include <cprog.h>
#include <bas/log.h>

#include "classifier.hh"
#include "fn.hh"

#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>

using namespace std;

static char *opt_proto;
static char *opt_state;
static int opt_limit = 10;

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

    { "proto",     'p', 0,
      G_OPTION_ARG_FILENAME, &opt_proto,
      "Specify the network .prototxt file", },

    { "state",     's', 0,
      G_OPTION_ARG_FILENAME, &opt_state,
      "Specify the trained state .caffemodel file", },

    { "limit",     'l', 0,
      G_OPTION_ARG_INT, &opt_limit,
      "Limit the max number of results", },

    { G_OPTION_REMAINING, '\0', 0, G_OPTION_ARG_FILENAME_ARRAY, &opt_files,
      "FILES", },

    { NULL },
};

Classifier classifier;

int main(int argc, char **argv) {
    ::google::InitGoogleLogging(argv[0]);
    FLAGS_logtostderr = 1;
    FLAGS_minloglevel = 1;

    if (! boot(&argc, &argv, "FILES"))
        return 1;

    if (! opt_proto) {
        log_err("The network .prototxt file isn't specified.");
        return 2;
    }

    if (! opt_state) {
        log_err("The trained state .caffemodel file isn't specified.");
        return 2;
    }

    if (! classifier.create(opt_proto)) {
        log_err("Can't create the network.");
        return 3;
    }

    if (! classifier.load(opt_state)) {
        log_err("Can't load the trained data.");
        return 4;
    }

    return process_files(opt_files);
}

int process_file(char *filename, FILE *file) {
    LOG1 printf("Processing file %s\n", filename);

    cv::Mat img = cv::imread(filename, -1);
    if (img.empty()) {
        log_err("Failed to read image %s.", filename);
        return 1;
    }

    for (auto pair : classifier.classify(img, opt_limit)) {
        int channel = pair.first;
        float accuracy = pair.second;
        // cout << "channel " << channel << ": " << accuracy << endl;
        printf("%d:%.4f\n", channel, accuracy);
    }

    return 0;
}
