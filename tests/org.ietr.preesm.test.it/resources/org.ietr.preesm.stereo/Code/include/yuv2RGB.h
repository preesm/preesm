/*
	============================================================================
	Name        : yuv2RGB.h
	Author      : kdesnos
	Version     : 1.0
	Copyright   : CECILL-C, IETR, INSA Rennes
	Description : Transformation of an YUV image into an RGB image.
	============================================================================
*/

#ifndef YUV_2_RGB_H
#define YUV_2_RGB_H
#include "preesm.h"

/**
* This function convert the input YUV image into its RGB equivalent.
*
*/
void yuv2rgb(int width, int height, IN unsigned char *y, IN unsigned char *u, IN unsigned char *v, OUT unsigned char *rgb);

#endif
