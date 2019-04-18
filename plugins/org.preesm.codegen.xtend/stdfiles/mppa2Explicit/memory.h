/*
	============================================================================
	Name        : memory.h
	Author      : kdesnos
	Version     : 1.0
	Copyright   : CECILL-C
	Description : Dynamic memory allocation primitives.
	              These functions replace the regular malloc() and free()
				  functions when code is printed with dynamic allocation and
				  mergeable broadcast and roundbuffers is activated.
	============================================================================
*/

#ifndef MEMORY_H
#define MEMORY_H

/**
* Allocation function used when a single pointer is allocated several times.
* This method ensures that only its first call actually performs the
* allocation.
*
* @param pointer
*		Adress of the pointer to allocate. The pointer value must be null (=0)
*       if this is the first call to this function. Otherwise, a previous call
*       to this function will have allocated the memory and the pointer value
*       will not be null.
* @param size
*       The amount of memory to allocate in byte. The function will allocate
*       more memory than this value in order to store a semaphore used to
*       ensure that the number of free is correct.
* @param nbFree
*       Number of call to the merged_free function for this buffer. Only the
*       last call will actually free the memory.
* @param mutex
*       Semaphore (used as a mutex) used to ensure that no concurrent
*       calls to multiple_malloc() are effectively running.
*
* @return a pointer to the allocated memory.
*
*/
void* multiple_malloc(void** pointer, int size, int nbFree, sem_t* mutex);

/**
* Allocation function used when a single pointer is allocated once and freed
* multiple times.
*
* @param size
*       The amount of memory to allocate in byte. The function will allocate
*       more memory than this value in order to store a semaphore used to
*       ensure that the number of free is correct.
* @param nbFree
*       Number of call to the merged_free function for this buffer. Only the
*       last call will actually free the memory.
*
* @return a pointer to the allocated memory
*
*/
void* merged_malloc(int size, int nbFree);

/**
* Free function used when a single pointer is allocated once or several times
* but must be freed only once.
*
* @param pointer
*       The amount of memory to allocate in byte. The function will allocate
*       more memory than this value in order to store a semaphore used to
*       ensure that the number of free is correct.
*
* @return a pointer to the allocated memory or 0 if the memory was freed.
*
*/
void*  merged_free(void* pointer, int size);

#endif
