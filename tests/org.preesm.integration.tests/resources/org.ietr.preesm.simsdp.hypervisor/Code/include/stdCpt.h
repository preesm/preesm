/*
	============================================================================
	Name        : medianCpt.h
	Author      : orenaud
	Version     : 1.1
	Copyright   : CECILL-C
	Description : Actor code to read a file from the hard drive
	============================================================================
*/

#ifndef STD_CPT_H
#define STD_CPT_H

#include "preesm.h"
//#define K 1.4826 //refer to wikipedia

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
void STDCpt(int N_SAMPLES, int SIGMA,  IN double *raw_data_real_i, IN double *raw_data_im_i, OUT double *std_R_o, OUT double *std_I_o);
double computeAverage( double *list, int size);
double computeVariance( double *list, int size);
void stdDeviationList(double *list, double average, int length, double *deviation_list);
#endif
