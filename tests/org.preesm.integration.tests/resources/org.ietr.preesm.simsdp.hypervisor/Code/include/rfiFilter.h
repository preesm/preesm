/*
	============================================================================
	Name        : plotRnISeries.h
	Author      : orenaud
	Version     : 1.1
	Copyright   : CECILL-C
	Description : Actor code to read a file from the hard drive
	============================================================================
*/

#ifndef RFI_FILTER_H
#define RFI_FILTER_H

#include "preesm.h"


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
void RFIFilter(int MODE, int N_SAMPLES,IN double *mad_R_i, IN double *mad_I_i, IN double *std_R_i, IN double *std_I_i, IN double *raw_data_real_i, IN double *raw_data_im_i, OUT double *filtered_real_data_o, OUT double *filtered_im_data_o);
void madFilter(double *raw_data, int length, double threshold, double *filtered_data);
void stdFilter(double *raw_data, int length, double threshold, double *filtered_data);
#endif
