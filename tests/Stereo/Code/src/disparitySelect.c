
#include "disparitySelect.h"
#include <string.h>

#define min(x,y) (((x)<(y))?(x):(y))
#define max(x,y) (((x)<(y))?(y):(x))

void disparitySelect (int height, int width, int nbDisparities,
                      int *iter, unsigned char *disparity, float *aggregatedDisparity,
                      float *bestCostFeed, unsigned char *currentResult,
                      int *nextIter, unsigned char *result, float *backBestCost)
{
    int i,j;

    // Special processng for the first iteration
    if(*iter == 0)
    {
        // Copy the input aggregated disparity in the feedback
        memcpy(backBestCost,aggregatedDisparity,height*width*sizeof(float));
        // Fill the result map with minDisparity
        memset(result, 0, height*width*sizeof(char));
    }
    else
    {
        // For all other iterations
        // Scan the pixels of the aggregated disparity
        for(j=0; j<height; j++)
        {
            for(i=0; i<width; i++)
            {
				// If the cost of the aggregated disparity is lower, keep the new
				// disparity as the best, else, keep te current.
                result[j*width+i] =
					(aggregatedDisparity[j*width+i]<bestCostFeed[j*width+i])?
						4*(*disparity) : currentResult[j*width+i];
				
                backBestCost[j*width+i] = min(aggregatedDisparity[j*width+i],bestCostFeed[j*width+i]);
			
            }
        }
    }
	*nextIter = (*iter + 1)%nbDisparities;
}
