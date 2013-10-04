/*
 * application.c
 *
 *  Created on: 5 sept. 2013
 *      Author: dspii
 */
#include "application.h"

void decode( unsigned char* in,  unsigned char* out, int size, int start){
	int i;
        for(i=0; i<size; i++){
                //*(out+i) = (int)(*(in+i))-2*(i+start);
        		//*(out+i) = (int)(*(in+i))-127;
        		*(out+i) = (int)(*(in+i));
        }
}

