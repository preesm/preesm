/*
 ============================================================================
 Name        : displayRGB.c
 Author      : mpelcat & kdesnos
 Version     : 1.0
 Copyright   : CeCILL-C, IETR, INSA Rennes
 Description : Displaying RGB frames one next to another in a row
 ============================================================================
 */

#include "displayRGB.h"
#include <time.h>

void displayRGBInit(int id, int height, int width) {
}

void displayLum(int id, unsigned char *lum) {
	display3Components(id, lum, lum, lum, 1);
}

void displayRGB(int id, int height, int width, unsigned char *rgb){
	unsigned char *r = rgb;
    unsigned char *g = rgb+1;
    unsigned char *b = rgb+2;
    display3Components(id,r,g,b,3);
}

void display3Components(int id, unsigned char *r, unsigned char *g, unsigned char *b, int pixelStride){
}

