/*
 * ClusterRotate.h
 *
 *  Created on: 04-Mar-2009
 *      Author: sbutler
 */

#ifndef CLUSTERROTATE_H_
#define CLUSTERROTATE_H_

#include <Eigen/Core>
#include <vector>

#include "Evrot.h"

class ClusterRotate {

public:
	ClusterRotate(int method, double stopCriteria );
	virtual ~ClusterRotate() { }

	std::vector<std::vector<int> > cluster(Eigen::MatrixXd& X);
	double getMaxQuality() { return mMaxQuality; };

protected:

	int mMethod;
	double mMaxQuality;
    double mStopCriteria;

};

#endif /* CLUSTERROTATE_H_ */
