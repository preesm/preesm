/*
============================================================================
Name        : morph.h
Author      : kdesnos
Version     : 1.0
Copyright   : CeCILL-C, IETR, INSA Rennes
Description : 
============================================================================
*/

#include "morph.h"
#define min(x,y) (((x)<(y))?(x):(y))
#define max(x,y) (((x)<(y))?(y):(x))

void dilation (int height , int width,
			   const int window,
			   unsigned char *input,
			   unsigned char *output)
{	
	int i,j;
	int k,l;
	// Process pixels one by one
	for(j=window; j< height-window; j++){
		for(i=0;i<width;i++){
			int res = -1;
			// output pixel is the median of a 3x3 window
			// Get the 9 pixels
			for(l=-window;l<=window;l++){
				int y = j+l;
				for(k=-window+abs(l);k<=window-abs(l);k++){
					int x = min(max(i+k,0),width-1);
					res = max(input[y*width+x],res);
				}
			}

			output[(j-window)*width+i] = res;
		}
	}
}

void erosion (int height , int width, 
			  const int window,
			  unsigned char *input,
			  unsigned char *output)
{	
	int i,j;
	int k,l;
	// Process pixels one by one
	for(j=window; j< height-window; j++){
		for(i=0;i<width;i++){
			int res = 256;
			// output pixel is the median of a 3x3 window
			// Get the 9 pixels
			for(l=-window;l<=window;l++){
				int y = j+l;
				for(k=-window+abs(l);k<=window-abs(l);k++){
					int x = min(max(i+k,0),width-1);
					res = min(input[y*width+x],res);
				}
			}

			output[(j-window)*width+i] = res;
		}
	}
}