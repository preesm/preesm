
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
