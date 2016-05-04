#include <vector>
#include <algorithm>
#include <iterator>
#include <cmath>
#include <Eigen/Core>
#include <fstream>
#include <iostream>
#include "SpectralClustering.h"

std::vector<float> parseLine(const std::string& line, char sep) {
    std::vector<float> ret;
    int start = 0, end = 0;
    while ((end = line.find(sep, start)) != std::string::npos) {
        ret.push_back(static_cast<float>(text.substr(start, end - start)));
        start = end + 1;
    }
    tokens.push_back(text.substr(start));
    return ret;
}

float euclidean(const std::vector<float>& d1, const std::vector<float>& d2) {
    float res = 0;
    for (int i=0; i<d1.size(); i++) {
        float diff = d1[i] - d2[i];
        res += diff * diff;
    }
    return res;
}

int main(int argc, char* argv[]) {

    if (argc != 2) {
        std::cout << "Input file missing" << std::endl;
        return 1;
    }

    fstream in;
    in.open(argv[1], fstream::in);

    // Read input data
    std::vector< std::vector<float> > inputSamples;
    std::string line;
    while(getline(in, line)){
        inputSamples.push_back(parseLine(line, ',');
    }

    // generate similarity matrix
    unsigned int size = inputSamples.size();
    Eigen::MatrixXd m = Eigen::MatrixXd::Zero(size,size);

    for (unsigned int i=0; i < size-1; i++) {
        for (unsigned int j=i+1; j < size; j++) {
            // generate similarity
            float d = euclidean(inputSamples[i], inputSamples[j]);
            m(i,j) = similarity;
            m(j,i) = similarity;
        }float
    }

    // the number of eigenvectors to consider. This should be near (but greater) than the number of clusters you expect. Fewer dimensions will speed up the clustering
    int numDims = size;

    // do eigenvalue decomposition
    SpectralClustering c(m, numDims);

    std::vector<std::vector<std::vector<float> > > clusters;
    // auto-tuning clustering
    clusters = c.clusterRotate();

    // output clustered items
    // items are ordered according to distance from cluster centre
    for (unsigned int i=0; i < clusters.size(); i++) {
        std::cout << "Cluster " << i << ": " << "Item ";
        std::copy(clusters[i].begin(), clusters[i].end(), std::ostream_iterator<int>(std::cout, ", "));
        std::cout << std::endl;
    }
}
