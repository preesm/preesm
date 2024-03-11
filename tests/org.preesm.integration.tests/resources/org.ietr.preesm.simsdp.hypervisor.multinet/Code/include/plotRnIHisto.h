/*
	============================================================================
	Name        : plotRnISeries.h
	Author      : orenaud
	Version     : 1.1
	Copyright   : CECILL-C
	Description : Actor code to read a file from the hard drive
	============================================================================
*/

#ifndef PLOTRNIHISTO_H
#define PLOTRNIHISTO_H

#include "preesm.h"
#define REAL_HEADER_PATH PROJECT_ROOT_PATH "/dat/real.data"
#define IM_HEADER_PATH PROJECT_ROOT_PATH "/dat/im.data"
#define REAL_ROOT "/dat/real.data"
//#define IM_ROOT "/dat/im.data"


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
void PlotRnIHisto( int SIZE, int DISPLAY, IN double *raw_data_real_i, IN double *raw_data_im_i);
void storeData(double *data,int size, const char *__restrict path);
void plotHistogram(const char* path, const char* label);

#endif
