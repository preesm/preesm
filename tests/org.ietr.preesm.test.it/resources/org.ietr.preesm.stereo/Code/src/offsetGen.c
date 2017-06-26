/*
	============================================================================
	Name        : offsetGen.h
	Author      : kdesnos
	Version     : 1.0
	Copyright   : CeCILL-C, IETR, INSA Rennes
	Description : Generation of an array of the offsets used in the 
	              computation of the depth map.
	============================================================================
*/

#include "offsetGen.h"
#include <math.h>


void offsetGen (int nbIterations, int *offsets){
	int i;
	int sum = 0;
	for(i=0; i< nbIterations; i++){
		offsets[i] = 2*sum + 1;
		sum += offsets[i];
		offsets[i] %= 32;  
	}
}
