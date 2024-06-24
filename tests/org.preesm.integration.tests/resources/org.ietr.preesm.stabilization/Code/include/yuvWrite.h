/**
* Write YUV file
*
* @file yuvWrite.h
* @author kdesnos
* @date 2016.09.06
* @version 1.0
* @copyright CECILL-C
*/

#include "preesm.h"
#include "applicationParameters.h"

#ifndef YUV_WRITE_H
#define YUV_WRITE_H

/**
* Initialize the yuvWrite actor.
* Open the YUV file at the given PATH_WRITE.
*
*/
void initYUVWrite();

/**
* Write a new frame to the YUV file.
*
* @param width
*        The width of the written YUV file
* @param height
*        The heigth of the written YUV file
* param y
*       Y component written in the file
* param u
*       U component written in the file
* param v
*       V component written in the file
*/
void yuvWrite(const int width, const int height,
			  IN const unsigned char * const y, 
			  IN const unsigned char * const u, 
			  IN const unsigned char * const v);

/**
* Close the written file.
*/
void endYUVWrite();

#endif
