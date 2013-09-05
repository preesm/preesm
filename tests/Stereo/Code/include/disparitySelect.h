#ifndef DISPARITY_SELECT_H
#define DISPARITY_SELECT_H

void disparitySelect (int height , int width, int nbIterations, int *iter, unsigned char *disparity, float *aggregatedDisparity, float *bestCostFeed, unsigned char *currentResult,  int *nextIter, unsigned char *result, float *backBestCost);

#endif
