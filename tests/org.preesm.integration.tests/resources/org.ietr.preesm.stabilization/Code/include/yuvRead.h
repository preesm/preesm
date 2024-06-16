/*
	============================================================================
	Name        : readYUV.h
	Author      : kdesnos & mpelcat
	Version     : 1.1
	Copyright   : CECILL-C
	Description : Read a file from the hard drive
	============================================================================
*/

#include "preesm.h"
#include "applicationParameters.h"

#ifndef READ_YUV_H
#define READ_YUV_H

/**
* Initialize the readYUV actor.
* Open the YUV file at the given PATH_VIDEO and check its size.
*
* @param width
*        The width of the opened YUV file
* @param height
*        The heigth of the opened YUV file
*/
void initReadYUV(int width, int height);

/**
* Read a new frame from the YUV file.
*
* @param width
*        The width of the opened YUV file
* @param height
*        The heigth of the opened YUV file
* param y
*       Destination of the Y component read from the file
* param u
*       Destination of the U component read from the file
* param v
*       Destination of the V component read from the file
*/
void readYUV(int width, int height, OUT unsigned char *y, OUT unsigned char *u, OUT unsigned char *v);

/**
* Close the read file.
*/
void endYUVRead();

#endif
