#pragma once

#include <memory>
#include <string>
#include <vector>

#include <opencv2/core/core.hpp>
#include <caffe/caffe.hpp>

class Classifier {
public:
    bool create(const char *prototxt);
    bool load(const char *statefile);

    std::vector<std::pair<int, float>>
        classify(const cv::Mat& img, int N = 10);

private:
    std::shared_ptr<caffe::Net<float>> net;

    caffe::Blob<float> *input;
    cv::Size inSize;
    int inWidth;
    int inHeight;
    int inChannels;

    caffe::Blob<float> *output;
    int outChannels;

    std::vector<std::string> labels;
};
