
#include <math.h>
#include "computeWeights.h"

#define R_gamaC (float) 1.0/16.0 /*(1.0)/16.0 */
#define min(x,y) (((x)<(y))?(x):(y))
#define max(x,y) (((x)<(y))?(y):(x))

void computeWeights (int height , int width, int horOrVert,int *offset, unsigned char *rgbL, float *weights)
{
    int i, j;

    // Beginning of the g anb b components
    const int idxG = 2*height*width;
    const int idxB = height*width;

    // hOffset of vOffset depending on the computed weights
    int hOffset = (horOrVert == 0)? *offset : 0;
    int vOffset = (horOrVert == 1)? *offset : 0;

    // Distance coeff
    float distanceCoeff = -(float)(*offset)/36.0;
	//distanceCoeff *= -1;

    // Scan the pixels of the rgb image
    for(j=0; j<height; j++)
    {
        for(i=0; i<width; i++)
        {
            float r0, g0, b0, r, g, b;
            float weightM, weightP, weightO;

            // Compute the weights
            r0 = rgbL[j*width+i];
            g0 = rgbL[idxG+j*width+i];
            b0 = rgbL[idxB+j*width+i];

            r  = rgbL[max(j-vOffset,0)*width+max(i-hOffset,0)];
            g  = rgbL[idxG+max(j-vOffset,0)*width+max(i-hOffset,0)];
            b  = rgbL[idxB+max(j-vOffset,0)*width+max(i-hOffset,0)];
            weightM = sqrtf((r0-r)*(r0-r)+(g0-g)*(g0-g)+(b0-b)*(b0-b))* R_gamaC;

            r  = rgbL[min(j+vOffset,height-1)*width+min(i+hOffset,width-1)];
            g  = rgbL[idxG+min(j+vOffset,height-1)*width+min(i+hOffset,width-1)];
            b  = rgbL[idxB+min(j+vOffset,height-1)*width+min(i+hOffset,width-1)];
            weightP = sqrtf((r0-r)*(r0-r)+(g0-g)*(g0-g)+(b0-b)*(b0-b))* R_gamaC;

            weightM = exp(distanceCoeff-weightM);
            weightP = exp(distanceCoeff-weightP);

            weightO = 1/(weightM+weightP+1);
            weightM = weightM*weightO;
            weightP = weightP*weightO;

            // Store the three weights one after the other in the
            // output buffer;
            weights[3*(j*width+i)+0] = weightO;
            weights[3*(j*width+i)+1] = weightM;
            weights[3*(j*width+i)+2] = weightP;
        }
    }
}
