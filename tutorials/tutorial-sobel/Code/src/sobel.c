/*
	============================================================================
	Name        : sobel.c
	Author      : kdesnos
	Version     : 1.0
	Copyright   : CECILL-C
	Description :
	============================================================================
*/

#include <string.h>
#include <stdlib.h>

#include "sobel.h"

void sobel(int xSize, int ySize, unsigned char *input, unsigned char *output){
    int i,j;

    // Apply the filter
    for(j=1; j<ySize-1; j++){
        for(i=1; i<xSize-1; i++){
            int gx = -input[(j-1)*xSize + i-1] -2*input[  j*xSize + i-1] -input[(j+1)*xSize + i-1]
                     +input[(j-1)*xSize + i+1] +2*input[  j*xSize + i+1] +input[(j+1)*xSize + i+1];
            int gy = -input[(j-1)*xSize + i-1] -2*input[(j-1)*xSize + i] -input[(j-1)*xSize + i+1]
                     +input[(j+1)*xSize + i-1] +2*input[(j+1)*xSize + i] +input[(j+1)*xSize + i+1];

            output[j*xSize + i] = (abs(gx) + abs(gy))/8;
        }
    }

    // Fill the left and right sides
    for(j=0; j<ySize ; j++){
        output[j*xSize] = 0;
        output[(j+1)*xSize-1] = 0;
    }
}


