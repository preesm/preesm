/*
	============================================================================
	Name        : readYUV.h
	Author      : kdesnos & mpelcat
	Version     : 1.0
	Copyright   : CECILL-C
	Description : Actor code to read a file from the hard drive
	============================================================================
*/

#ifndef READ_YUV_H
#define READ_YUV_H

#define SUCCESS 0
#define FAILURE -1

/**
* Initialize the readYUV actor.
* Open the YUV file at the given PATH and check its size.
*
* @param fileName
*        The path of the opened YUV file
* @param xSize
*        The width of the opened YUV file
* @param ySize
*        The heigth of the opened YUV file
* @param nbFrames
*        The number of frames to read
*/
int initReadYUV(char* filePath, int xSize, int ySize, int nbFrames);

/**
* Read a new frame from the YUV file.
*
* @param xSize
*        The width of the opened YUV file
* @param ySize
*        The heigth of the opened YUV file
* param y
*       Destination of the Y component read from the file
* param u
*       Destination of the U component read from the file
* param v
*       Destination of the V component read from the file
*/
void readYUV(int xSize, int ySize, unsigned char *y, unsigned char *u, unsigned char *v);

void closeYuvFile();

#endif
