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

#include "utils.h"
#include "offsetGen.h"
#include <math.h>

void offsetGen(int nbIterations, int *offsets) {
	int i;
	int sum = 0;
	for (i = 0; i < nbIterations; i++) {
		int modulo;
		int value = 2 * sum + 1;
		STORE_INT(&offsets[i], &value);
		sum += LOAD_INT(&offsets[i]);
		modulo = LOAD_INT(&offsets[i]) % 32;
		STORE_INT(&offsets[i], &modulo);
	}
}
