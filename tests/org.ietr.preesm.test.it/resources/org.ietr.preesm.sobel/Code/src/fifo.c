/*
	============================================================================
	Name        : fifo.c
	Author      : kdesnos
	Version     :
	Copyright   : CECILL-C
	Description :
	============================================================================
*/
#include <string.h>
#include "../include/fifo.h"




void fifoInit(void* headBuffer, int headSize, void* bodyBuffer, int bodySize) {
    memset(headBuffer,0,headSize);
    if(bodyBuffer != NULL) {
        memset(bodyBuffer,0,bodySize);
    }
}


void fifoPush(void * inputBuffer, void* headBuffer, int headSize, void* bodyBuffer, int bodySize){
    if(bodyBuffer != NULL){
        memcpy(headBuffer,bodyBuffer,headSize);
        memcpy(bodyBuffer, (char *)bodyBuffer+headSize, bodySize-headSize);
        memcpy((char *)bodyBuffer+bodySize-headSize,  inputBuffer, headSize);
    } else {
        memcpy(headBuffer,inputBuffer,headSize);
    }
    //memcpy(headBuffer, headBuffer+outputSize, fifoSize-outputSize);
    //memset(headBuffer+fifoSize-outputSize, 0, outputSize); // Useless
}

void fifoPop(void * outputBuffer, void* headBuffer, int headSize, void* bodyBuffer, int bodySize){
    memcpy(outputBuffer, headBuffer, headSize);
}
