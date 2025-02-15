/*
	============================================================================
	Name        : medianCpt.h
	Author      : orenaud
	Version     : 1.1
	Copyright   : CECILL-C
	Description : Actor code to read a file from the hard drive
	============================================================================
*/

#ifndef MAD_CPT_H
#define MAD_CPT_H

#include "preesm.h"
#define K 1.4826 //refer to wikipedia

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
void MADCpt(int N_SAMPLES, int SIGMA,  IN double *raw_data_real_i, IN double *raw_data_im_i, OUT double *mad_R_o, OUT double *mad_I_o);
void sortList(double *data, int size, double *sorted_list);
double computeMedian(double *list, int length);
void deviationList(double *list, double median, int length, double *deviation_list);
double MIN(double *tab,int len);
#endif
