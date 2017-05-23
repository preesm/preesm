/*
 ============================================================================
 Name        : costConstruction.c
 Author      : kdesnos
 Author      : JZHAHG
 Version     : 1.0
 Copyright   : CeCILL-C, IETR, INSA Rennes
 Description : Computation of the costs associated to the pixels of the
 stereo pair for a given disparity
 ============================================================================
 */

#include "costConstruction.h"
#include <math.h>

#define min(x,y) (((x)<(y))?(x):(y))

unsigned char hammingCost(unsigned char *a, unsigned char *b) {
	int i;
	unsigned char res = 0;

	// Bitwise exclusive or to identify the differences
	// between the two signatures
	unsigned char diffBit = *a ^ *b;

	// Count the 1 in the diffBit word
	for (i = 0; i < 8; i++) {
		res += ((diffBit & (1 << i)) ? 1 : 0);
	}

	return res;
}

void costConstruction (int height, int width, float truncValue,
                       unsigned char *disparity,
                       float *grayL, float *grayR,
                       unsigned char *cenL, unsigned char *cenR,
                       float *disparityError, unsigned char * back)
{
    int i,j;

    // For each disparity, scan the pixels of the left image
    for(j=0; j<height; j++)
    {
        for(i=0; i<width; i++)
        {
            unsigned char censusCost;
            int leftPxlIdx = j*width + i;
            int rightPxlIdx = j*width + (((i-*disparity)>0)?i-*disparity:0);

            // Get the cost from the census signatures
            censusCost = hammingCost(cenL+leftPxlIdx, cenR+rightPxlIdx);

            // Combination method 3 -- weight addition
            disparityError[leftPxlIdx] =  min(fabs((float)(grayL[leftPxlIdx]-grayR[rightPxlIdx])),truncValue) + censusCost/5.0;
        }
    }
}
