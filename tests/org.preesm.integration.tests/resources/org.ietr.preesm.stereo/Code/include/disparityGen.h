/*
	============================================================================
	Name        : disparityGen.h
	Author      : kdesnos
	Version     : 1.0
	Copyright   : CeCILL-C, IETR, INSA Rennes
	Description : Generation of an array of the disparities used in the 
	              computation of the depth map.
	============================================================================
*/

#ifndef DISPARITY_GEN_H
#define DISPARITY_GEN_H
#include "preesm.h"

/**
* Generation of an array of the disparities used in the computation 
* of the depth map.
*
* @param minDisparity
*        The minimum disparity used in the computations.
* @param maxDisparity
*        The maximum disparity + 1 used in the computations.
* @param disparities
*        The array containing all values from minDisparity to maxDisparities-1
*/
void disparityGen (int minDisparity, int maxDisparity,
				   OUT unsigned char *disparities);

#endif
