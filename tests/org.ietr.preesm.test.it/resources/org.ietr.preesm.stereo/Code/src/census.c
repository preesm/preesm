/*
	============================================================================
	Name        : census.c
	Author      : kdesnos
	Author      : JZHAHG
	Version     : 1.0
	Copyright   : CeCILL-C, IETR, INSA Rennes
	Description : Computation of the census corresponding to an input gray image
	============================================================================
*/

#include "census.h"
#include <string.h>


void census(int height, int width, float *gray, unsigned char *cen){
    int i,j;
    int k,l;

    // Scan the pixels of the grey image
    // except the 1 pixel-wide band around the image.
    for(j=1; j<height-1; j++){
		// 1st and last pixels of the line are 0
		cen[j*width] = 0;
		cen[(j+1)*width-1] = 0;
        for(i=1; i<width-1; i++){
            unsigned char signature = 0x00;
            int bit = 7;
            // For each pixel, compute its census signature with
            // a 3x3 pixels window around it.
            for(l = -1; l <= 1; l++){
                for(k=-1 ; k<=1; k++){
                    // In the 8 bit signature, a bit is set
                    // to 1 if the compared pixel is inferior to the current.
					// Ex:
					// |20|21|13|
					// |15|18|19|  => 0x35 (00110101)
					// |13|18|16|
                    if(k!=0 || l!=0){
                        if(gray[j*width+i] > gray[(j+l)*width+(i+k)]){
                            signature |= 1 << bit;
                        }
                        bit--;
                    }
                }
            }
            cen[j*width+i] = signature;
        }
    }

	// Fill the 1st and last lines with 0
	memset(cen,0,width*sizeof(char));
	memset(cen+(height-1)*width,0,width*sizeof(char));
}
