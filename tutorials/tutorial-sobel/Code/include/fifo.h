/*
	============================================================================
	Name        : fifo.h
	Author      : kdesnos
	Version     :
	Copyright   :
	Description :
	============================================================================
*/

#ifndef FIFO_H
#define FIFO_H

void fifoInit(void* storageBuffer, int size, int nbTokens);

void fifoPush(void* storageBuffer, void * inputBuffer, int size, int fifoSize);

void fifoPop(void* storageBuffer, void * outputBuffer, int size, int fifoSize);

#endif
