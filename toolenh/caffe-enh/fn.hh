#pragma once

#include <algorithm>
#include <vector>

template <typename T>
std::vector<std::pair<int, T>> topN(const std::vector<T>& vec, size_t N) {
    size_t size = vec.size();
    if (N > size)
        N = size;

    std::vector<std::pair<int, T>> pairs;
    for (size_t i = 0; i < vec.size(); ++i)
        pairs.push_back(std::make_pair(i, vec[i]));

    std::partial_sort(pairs.begin(), pairs.begin() + N, pairs.end(),
        [](auto a, auto b) { return a.second > b.second; }
        );

    return std::vector<std::pair<int, T>>(pairs.begin(), pairs.begin() + N);
}
