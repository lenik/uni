#include "classifier.hh"

#include <bas/log.h>

#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>

#include "fn.hh"

using namespace std;

bool Classifier::create(const char *prototxt) {
    net.reset(new caffe::Net<float>(prototxt, caffe::TEST));
    return true;
}

bool Classifier::load(const char *statefile) {
    net->CopyTrainedLayersFrom(statefile);

    if (net->num_inputs() != 1) {
        log_err("Network have too many inputs: %d", net->num_inputs());
        return false;
    }

    if (net->num_outputs() != 1) {
        log_err("Network have too many outputs: %d", net->num_outputs());
        return false;
    }

    input = net->input_blobs()[0];
    inChannels = input->channels();
    if (inChannels != 1 && inChannels != 3) {
        log_err("Unsupported number of channels: %d", inChannels);
        return false;
    }

    inSize = cv::Size(input->width(), input->height());
    inWidth = inSize.width;
    inHeight = inSize.height;

    output = net->output_blobs()[0];
    outChannels = output->channels();

    return true;
}

vector<pair<int, float>> Classifier::classify(const cv::Mat& img, int N) {
    input->Reshape(1, inChannels, inHeight, inWidth);

    /* forard dim change to all layers. */
    net->Reshape();

    /* Wrap input channels in Mat[] */
    vector<cv::Mat> channelv;
    float *data = input->mutable_cpu_data();
    for (int i = 0; i < inChannels; i++) {
        cv::Mat ch(inHeight, inWidth, CV_32FC1, data);
        channelv.push_back(ch);
        data += inWidth * inHeight;
    }

    /* send img to input channels */
    cv::Mat sample;
    switch (inChannels) {
    case 1:
        switch (img.channels()) {
        case 3: cv::cvtColor(img, sample, cv::COLOR_BGR2GRAY); break;
        case 4: cv::cvtColor(img, sample, cv::COLOR_BGRA2GRAY); break;
        }
        break;
    case 3:
        switch (img.channels()) {
        case 4: cv::cvtColor(img, sample, cv::COLOR_BGRA2BGR); break;
        case 1: cv::cvtColor(img, sample, cv::COLOR_GRAY2BGR); break;
        }
        break;
    }
    if (sample.empty())
        sample = img;

    if (sample.size() != inSize) {
        cv::Mat resized;
        cv::resize(sample, resized, inSize);
        sample = resized;
    }

    cv::Mat _float;
    if (inChannels == 3)
        sample.convertTo(_float, CV_32FC3);
    else
        sample.convertTo(_float, CV_32FC1);

    cv::split(_float, channelv);

    float *_data = reinterpret_cast<float *>(channelv[0].data);
    if (_data != input->cpu_data()) {
        log_err("Wrapping of the input layer doesn't work.");
    }

    net->Forward();

    const float *p = output->cpu_data();

    vector<float> outvec(p, p + outChannels);
    return topN(outvec, N);
}
