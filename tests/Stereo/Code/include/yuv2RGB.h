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

/**
* This function convert the input YUV image into its RGB equivalent.
*
*/
void yuv2rgb(int width, int height, unsigned char *y, unsigned char *u, unsigned char *v, unsigned char *rgb);

#endif
