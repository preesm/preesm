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

void fifoInit(void* headBuffer, int headSize, void* bodyBuffer, int bodySize);

void fifoPush(void * inputBuffer, void* headBuffer, int headSize, void* bodyBuffer, int bodySize);

void fifoPop(void * outputBuffer, void* headBuffer, int headSize, void* bodyBuffer, int bodySize);

#endif
