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
	displayRGB(id, lum, lum, lum);
}

void displayRGB(int id, unsigned char *r, unsigned char *g, unsigned char *b) {

}

