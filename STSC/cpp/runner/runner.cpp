#include <iostream>
#include <vector>
#include <algorithm>
#include <iterator>
#include <cmath>
#include <fstream>
#include <map>
#include <Eigen/Core>
#include "SpectralClustering.h"

/*
 * Read the input data. The format of the dataset must be
 * feature_1,....,feature_n
 * feature_1,....,feature_n
 *
 * @filePath: the path of the dataset
 *
 * @ret a vector of multi dimensional points
 */

std::vector<std::vector<double> > readData(char* filePath) {
    std::fstream aInput;
    aInput.open(filePath, std::fstream::in);

    std::vector<std::vector<double> > aDataVect;
    std::string aLine;
    while(std::getline(aInput, aLine)) {
        std::vector<double> aPoint;
        int nStart = 0, nEnd = 0;
        while ((nEnd = aLine.find(',', nStart)) != std::string::npos) {
            aPoint.push_back(std::stod(aLine.substr(nStart, nEnd - nStart)));
            nStart = nEnd + 1;
        }
        aPoint.push_back(std::stod(aLine.substr(nStart, aLine.length() - nStart)));
        aDataVect.push_back(aPoint);
    }
    aInput.close();
    return aDataVect;
}

/*
 * calculate the Euclidean Distance between two points
 *
 * @rD1: First point
 * @rD2: Second point
 *
 * @ret the Euclidean Distance
 */

double euclideanDistance(std::vector<double>& rD1, std::vector<double>& rD2) {
    double dRes=0;
    for (int i=0; i<rD1.size(); i++) {
        double dDiff = rD1[i] - rD2[i];
        dRes += dDiff * dDiff;
    }
    return std::sqrt(dRes);
}

/*
 * Compute the sigma parameter. The sigma is the tuning parameter and
 * it is defined as the distance between the point and its seventh
 * nearest neighboor
 *
 * @rDataset: the whole dataset
 * @nPIndex: the index of the point to analyze
 *
 * @ret the distance of nPIndex's 7th neighboor.
 */

double sigma(std::vector<std::vector<double>> rDataset, int nPIndex, int K) {
    std::vector<double> aDistances;
    for (int i=0; i<rDataset.size(); i++) {
        if (i != nPIndex) { //Don't compare the point with itself
            aDistances.push_back(euclideanDistance(rDataset[nPIndex], rDataset[i]));
        }
    }

    std::sort(aDistances.begin(), aDistances.end());

    return aDistances[K];
}

int main(int argc, char* argv[]) {

    if (argc != 5) {
        std::cout << "Usage: ./runner [Dataset] [# Exp. Clusters] [K Value] [Similarity Cut]" << std::endl;
        return -1;
    }

    /*
    * Neighboor to consider for the tuning parameter.
    * starting from 0
    */

    int K = std::stoi(argv[3]);
    int simCut = std::stod(argv[4]);
    std::vector<std::vector<double> > aInput = readData(argv[1]);

    // generate similarity matrix
    unsigned int size = aInput.size();
    Eigen::MatrixXd m = Eigen::MatrixXd::Zero(size,size);

    // calculate sigmas
    std::vector<double> aSigmas;
    for (int i=0; i<size; i++) {
        aSigmas.push_back(sigma(aInput, i, K));
    }

    for (unsigned int i=0; i < size; i++) {
        for (unsigned int j=i+1; j < size; j++) {
            // generate similarity
            double d = euclideanDistance(aInput[i], aInput[j]);
            double similarity = 0;
            if (d<simCut) {
                similarity = exp(-(d*d) / (aSigmas[i] * aSigmas[j]));
            }
            m(i,j) = similarity;
            m(j,i) = similarity;
        }
    }

    // the number of eigenvectors to consider. This should be near (but greater) than the number of clusters you expect. Fewer dimensions will speed up the clustering
    int numDims = std::stoi(argv[2]);

    // do eigenvalue decomposition
    SpectralClustering c(m, numDims);

    std::vector<std::vector<int> > clusters;
    // auto-tuning clustering
    clusters = c.clusterRotate();

    // output clustered items
    // items are ordered according to distance from cluster centre


    for (unsigned int i=0; i < clusters.size(); i++) {
        for (int j = 0; j < clusters[i].size(); j++) {
           for (int k=0; k<aInput[clusters[i][j]].size(); k++) {
               std::cout << aInput[clusters[i][j]][k] << ",";
           }
           std::cout << i << std::endl;
        }
    }

    return 0;
}
