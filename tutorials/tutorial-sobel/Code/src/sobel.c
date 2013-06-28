/*
	============================================================================
	Name        : sobel.c
	Author      : kdesnos
	Version     :
	Copyright   :
	Description :
	============================================================================
*/

#include <string.h>
#include <stdlib.h>

#include "sobel.h"

void sobel(int xSize, int ySize, unsigned char *input, unsigned char *output){
    int i,j;

    // Copy input to output buffer before applying the filter
    //memcpy(output, input, xSize*ySize*sizeof(unsigned char));

    // Apply the filter
    for(j=0; j<ySize; j++){
        for(i=0; i<xSize; i++){
            int gx = -input[(j-1)*xSize + i-1] -2*input[  j*xSize + i-1] -input[(j+1)*xSize + i-1]
                     +input[(j-1)*xSize + i+1] +2*input[  j*xSize + i+1] +input[(j+1)*xSize + i+1];
            int gy = -input[(j-1)*xSize + i-1] -2*input[(j-1)*xSize + i] -input[(j-1)*xSize + i+1]
                     +input[(j+1)*xSize + i-1] +2*input[(j+1)*xSize + i] +input[(j+1)*xSize + i+1];

            output[j*xSize + i] = (abs(gx) + abs(gy))/8;
        }
    }
}


