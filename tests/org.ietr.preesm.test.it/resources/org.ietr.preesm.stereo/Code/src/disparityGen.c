/*
	============================================================================
	Name        : disparityGen.c
	Author      : kdesnos
	Version     : 1.0
	Copyright   : CeCILL-C, IETR, INSA Rennes
	Description : Generation of an array of the disparities used in the 
	              computation of the depth map.
	============================================================================
*/

#include "disparityGen.h"
#include <stdio.h>

void disparityGen (int minDisparity, int maxDisparity,
				   unsigned char *disparities){
	int disp;
	for(disp=minDisparity; disp<maxDisparity; disp++){
		disparities[disp-minDisparity] = disp;
	}
}
