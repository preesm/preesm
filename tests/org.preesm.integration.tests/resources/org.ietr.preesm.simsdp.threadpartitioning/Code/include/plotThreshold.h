/*
	============================================================================
	Name        : plotRnISeries.h
	Author      : orenaud
	Version     : 1.1
	Copyright   : CECILL-C
	Description : Actor code to read a file from the hard drive
	============================================================================
*/

#ifndef PLOT_THRESHOLD_H
#define PLOT_THRESHOLD_H

#include "preesm.h"

#define REAL_THRESHOLD_PATH PROJECT_ROOT_PATH "/dat/real_threshold.data"
#define IM_THRESHOLD_PATH PROJECT_ROOT_PATH "/dat/im_threshold.data"
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
void PlotThreshold(int SAMPLE_RATE,int SIZE, int DISPLAY, IN double *raw_data_real_i, IN double *raw_data_im_i, IN double *mad_R_i, IN double *mad_I_i, IN double *std_R_i, IN double *std_I_i);
void plotThreshold(double* x, double* MADthreshold, double* STDthreshold, double* raw_data , int size, const char* label,const char* path);
#endif
