/*
	============================================================================
	Name        : fifo.c
	Author      : kdesnos
	Version     :
	Copyright   :
	Description :
	============================================================================
*/
#include <string.h>
#include "../include/fifo.h"

void fifoInit(void* storageBuffer, int size, int nbTokens){
    memset(storageBuffer,0,size*nbTokens);
}


void fifoPush(void* storageBuffer, void * inputBuffer, int inputSize, int fifoSize){
    memcpy(storageBuffer+(fifoSize-inputSize),inputBuffer,inputSize);
}

void fifoPop(void* storageBuffer, void * outputBuffer, int outputSize, int fifoSize){
    memcpy(outputBuffer, storageBuffer, outputSize);
    memcpy(storageBuffer, storageBuffer+outputSize, fifoSize-outputSize);
    memset(storageBuffer+fifoSize-outputSize, 0, outputSize); // Useless
}
