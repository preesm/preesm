
#include "disparityGen.h"
#include <stdio.h>

void disparityGen (int minDisparity, int maxDisparity, unsigned char *disparities){
	int disp;
	for(disp=minDisparity; disp<maxDisparity; disp++){
		disparities[disp-minDisparity] = disp;
	}
}
