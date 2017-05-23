/*
	============================================================================
	Name        : rgb2Gray.h
	Author      : kdesnos
	Version     : 1.0
	Copyright   : CECILL-C, IETR, INSA Rennes
	Description : Transformation of an RGB image into a gray-level image.
	============================================================================
*/

#ifndef RGB_2_GRAY_H
#define RGB_2_GRAY_H

#include "preesm.h"

/**
* This function convert the input RGB image into its gray equivalent.
* 
* @param size
*        Total number of pixel of the input image
* @param rgb
*        3 components of the input image
* @param gray
*        Output buffer for the gray image.
*/
void rgb2Gray(int size, IN unsigned char *rgb, OUT float *gray);

#endif
