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

void fifoPush(void* storageBuffer, void * inputBuffer, int size);

void fifoPull(void* storageBuffer, void * outputBuffer, int size);

#endif
