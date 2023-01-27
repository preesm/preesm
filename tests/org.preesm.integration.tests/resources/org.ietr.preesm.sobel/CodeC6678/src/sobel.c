/*
	============================================================================
	Name        : sobel.c
	Author      : kdesnos
	Version     : 1.1
	Copyright   : CECILL-C
	Description :
	============================================================================
*/

#include <string.h>


#include "sobel.h"
#include <xdc/runtime/System.h>

void sobel(int width, int height, unsigned char *input, unsigned char *output){
    int i,j;
    // Apply the filter
    for(j=1; j<height-1; j++){
        for(i=1; i<width-1; i++){
            int gx = -input[(j-1)*width + i-1] -2*input[  j*width + i-1] -input[(j+1)*width + i-1]
                     +input[(j-1)*width + i+1] +2*input[  j*width + i+1] +input[(j+1)*width + i+1];
            int gy = -input[(j-1)*width + i-1] -2*input[(j-1)*width + i] -input[(j-1)*width + i+1]
                     +input[(j+1)*width + i-1] +2*input[(j+1)*width + i] +input[(j+1)*width + i+1];

            output[j*width + i] = (abs(gx) + abs(gy))/8;
        }
    }

    // Fill the left and right sides
    for(j=0; j<height ; j++){
        output[j*width] = 0;
        output[(j+1)*width-1] = 0;
    }
}
