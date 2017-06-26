/*
 ============================================================================
 Name        : computeWeights.c
 Author      : kdesnos
 Author      : JZHAHG
 Version     : 1.0
 Copyright   : CeCILL-C, IETR, INSA Rennes
 Description : Computation of the weights associated to the pixel of an rgb
 image.
 ============================================================================
 */

#include "utils.h"
#include <math.h>
#include "computeWeights.h"

#define R_gamaC (float) 1.0/16.0 /*(1.0)/16.0 */
#define min(x,y) (((x)<(y))?(x):(y))
#define max(x,y) (((x)<(y))?(y):(x))

void computeWeights(int height, int width, int horOrVert, int *offset,
		unsigned char *rgbL, float *weights) {
	int i, j;

	// hOffset of vOffset depending on the computed weights
	int hOffset = (horOrVert == 0) ? LOAD_INT(offset) : 0;
	int vOffset = (horOrVert == 1) ? LOAD_INT(offset) : 0;

	// Distance coeff
	float distanceCoeff = -(float) (LOAD_INT(offset)) / 36.0;
	//distanceCoeff *= -1;

	// Scan the pixels of the rgb image
	for (j = 0; j < height; j++) {
		for (i = 0; i < width; i++) {
			float r0, g0, b0, r, g, b;
			float weightM, weightP, weightO;

			 // Compute the weights
            r0 = rgbL[3*(j*width+i)];
            g0 = rgbL[3*(j*width+i)+1];
            b0 = rgbL[3*(j*width+i)+2];

            r  = rgbL[3*(max(j-vOffset,0)*width+max(i-hOffset,0))];
            g  = rgbL[3*(max(j-vOffset,0)*width+max(i-hOffset,0))+1];
            b  = rgbL[3*(max(j-vOffset,0)*width+max(i-hOffset,0))+2];
            weightM = sqrtf((r0-r)*(r0-r)+(g0-g)*(g0-g)+(b0-b)*(b0-b))* R_gamaC;

            r  = rgbL[3*(min(j+vOffset,height-1)*width+min(i+hOffset,width-1))];
            g  = rgbL[3*(min(j+vOffset,height-1)*width+min(i+hOffset,width-1))+1];
            b  = rgbL[3*(min(j+vOffset,height-1)*width+min(i+hOffset,width-1))+2];
            weightP = sqrtf((r0-r)*(r0-r)+(g0-g)*(g0-g)+(b0-b)*(b0-b))* R_gamaC;

			weightM = exp(distanceCoeff - weightM);
			weightP = exp(distanceCoeff - weightP);

			weightO = 1 / (weightM + weightP + 1);
			weightM = weightM * weightO;
			weightP = weightP * weightO;

			// Store the three weights one after the other in the
			// output buffer;
			STORE_FLOAT(&weights[3*(j*width+i)+0], &weightO);
			STORE_FLOAT(&weights[3*(j*width+i)+1], &weightM);
			STORE_FLOAT(&weights[3*(j*width+i)+2], &weightP);
		}
	}
}
