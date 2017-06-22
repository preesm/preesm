/*
	============================================================================
	Name        : fifo.h
	Author      : kdesnos
	Version     : 1.0
	Copyright   : CECILL-C
	Description : FIFO primitive for Preesm Codegen.
                  Currently, primitives were tested only for x86 with shared_mem.
	============================================================================
*/

#ifndef FIFO_H
#define FIFO_H

/**
* Initialize a FIFO by filling its memory with 0.
*
* @param headBuffer
*        pointer to the memory space containing the first element of the fifo.
* @param headSize
*        Size of the first element of the fifo (>0)
* @param bodyBuffer
*        pointer to the memory space containing all but the first element of 
*        the fifo.
* @param bodySize
*        Size of the body of the fifo (>=0)
*/
void fifoInit(void* headBuffer, int headSize, void* bodyBuffer, int bodySize);

/**
* Push a new element in the FIFO from an input buffer.
*
* @param inputBuffer
*        pointer to the data pushed in the fifo.
* @param headBuffer
*        pointer to the memory space containing the first element of the fifo.
* @param headSize
*        Size of the pushed data and of the first element of the fifo (>0)
* @param bodyBuffer
*        pointer to the memory space containing all but the first element of 
*        the fifo.
* @param bodySize
*        Size of the body of the fifo (>=0)
*/
void fifoPush(void * inputBuffer, void* headBuffer, int headSize, void* bodyBuffer, int bodySize);

/**
* Pop the head element from the FIFO to an output buffer.
*
* @param outputBuffer
*        pointer to the destination of the popped data.
* @param headBuffer
*        pointer to the memory space containing the first element of the fifo.
* @param headSize
*        Size of the popped data and of the first element of the fifo (>0)
* @param bodyBuffer
*        pointer to the memory space containing all but the first element of 
*        the fifo.
* @param bodySize
*        Size of the body of the fifo (>=0)
*/
void fifoPop(void * outputBuffer, void* headBuffer, int headSize, void* bodyBuffer, int bodySize);

#endif
