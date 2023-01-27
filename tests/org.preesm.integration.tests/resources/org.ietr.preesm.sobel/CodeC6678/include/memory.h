/*
	============================================================================
	Name        : memory.h
	Author      : kdesnos
	Version     : 1.1
	Copyright   : CECILL-C
	Description : Dynamic memory allocation primitives for C6678.
	              These functions replace the regular malloc() and free()
				  functions when code is printed with dynamic allocation and
				  mergeable broadcast and roundbuffers is activated.
	============================================================================
*/

#ifndef MEMORY_H
#define MEMORY_H

#include <xdc/std.h>
#include <ti/ipc/HeapMemMP.h>
#include <ti/ipc/GateMP.h>

#include <cache.h>
#include <semaphore6678.h>

// Remember to activate SharedRegion 1 in the .cfg file to activate dynamic memory allocation.
//#define DYNAMIC_MEM_ALLOC

#ifdef DYNAMIC_MEM_ALLOC
#ifdef L2
#warn "Using L2 cache with dynamic memory allocation may lead to data corruption for a unknown reason. Contact Preesm developer for more information."
#endif


/**
* Allocation function used when a single pointer is allocated several times.
* This method ensures that only its first call actually performs the
* allocation.
*
* @param sharedHeap
* 		Heap on which the data should be allocated
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
* @param mutexID
* 		Unique character string used to identify the semaphore associated to
* 		this memory.
* @param align
* 		The desired alignment of the allocated memory.
*
* @return a pointer to the allocated memory.
*
*/
void* multiple_malloc(HeapMemMP_Handle sharedheap, void** pointer, int size, int nbFree, sem_t *mutex, const char* mutexID, int align);

/**
* Allocation function used when a single pointer is allocated once and freed
* multiple times.
*
* @param sharedHeap
* 		Heap where the memory is allocated
* @param pointer
*		Adress of the pointer to allocate.
* @param size
*       The amount of memory to allocate in byte. The function will allocate
*       more memory than this value in order to store a semaphore used to
*       ensure that the number of free is correct.
* @param nbFree
*       Number of call to the merged_free function for this buffer. Only the
*       last call will actually free the memory.
* @param mutexID
* 		Unique character string used to identify the semaphore associated to
* 		this memory.
* @param align
* 		Alignment for the allocation.
*
* @return a pointer to the allocated memory
*
*/
void* merged_malloc(HeapMemMP_Handle sharedheap, void** pointer, int size, int nbFree, const char* mutexID, int align);

/**
* Free function used when a single pointer is allocated once or several times
* but must be freed only once.
*
* @param sharedHeap
* 		Heap where the memory is allocated
*
* @param pointer
*		Adress of the pointer to free.
*
* @param size
*       The amount of memory to allocate in byte. The function will allocate
*       more memory than this value in order to store a semaphore used to
*       ensure that the number of free is correct.
*
* @return a pointer to the allocated memory or 0 if the memory was freed.
*
*/
void*  merged_free(HeapMemMP_Handle sharedheap, void* pointer, int size);

#endif // DYNAMIC_MEM_ALLOC
#endif
