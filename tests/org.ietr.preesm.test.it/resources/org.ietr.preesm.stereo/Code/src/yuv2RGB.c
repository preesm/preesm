/*
	============================================================================
	Name        : yuv2RGB.c
	Author      : kdesnos
	Version     : 1.0
	Copyright   : CECILL-C, IETR, INSA Rennes
	Description : Transformation of an YUV image into a RGB image.
	============================================================================
*/

#include "yuv2RGB.h"

#define YUV2RGB_COEF_R  1.13983
#define YUV2RGB_COEF_G1 0.39465
#define YUV2RGB_COEF_G2 0.58060
#define YUV2RGB_COEF_B  2.03211

#define clamp(x) (x>255)? 255 : ((x<0)? 0 : x);

void yuv2rgb(int width, int height, unsigned char *y, unsigned char *u, unsigned char *v, unsigned char *rgb){
    int i,j;
	int size = height*width;

    for(i=0; i< height; i++){
        for(j=0; j < width; j++){
			int idx = i*width + j;
			int idxUV = i/2*width/2 + j/2;

			int c = y[idx];
			int d = u[idxUV];
			int e = v[idxUV];

			c = c -16;
			d = d -128;
			e = e -128;

            *(rgb + 3*idx+0) = (unsigned char) clamp((298*c+409*e+128)>>8);
            *(rgb + 3*idx+1) = (unsigned char) clamp((298*c-100*d-208*e+128)>>8);
            *(rgb + 3*idx+2) = (unsigned char) clamp((298*c+516*d+128)>>8);

		}
    }
}

