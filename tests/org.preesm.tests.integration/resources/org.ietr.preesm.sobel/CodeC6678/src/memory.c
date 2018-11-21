/*
 * memory.c
 *
 *  Created on: 18 nov. 2013
 *      Author: kdesnos
 */

#include <cache.h>
#include <memory.h>
#include <ti/ipc/GateMP.h>

#ifdef DYNAMIC_MEM_ALLOC

inline int gapForAlignment(int size, unsigned long alignmentSize, int desiredAlignment) {
	// End of data
	unsigned long end = alignmentSize + size;
	int gap =
			(end % desiredAlignment != 0) ?
					desiredAlignment - end % desiredAlignment : 0;
	return gap;
}

void* multiple_malloc(HeapMemMP_Handle sharedHeap, void** pointer, int size, int nbFree, sem_t *mutex, const char* mutexID, int align){

	sem_wait(mutex);
	cache_inv(pointer,1);
	if(*pointer == 0){
		*pointer =  (void*) merged_malloc(sharedHeap, pointer, size, nbFree, mutexID, align);
		cache_wb(pointer,1);
	}
	sem_post(mutex);

	return *pointer;
}

void* merged_malloc(HeapMemMP_Handle sharedHeap, void** pointer, int size, int nbFree, const char* mutexID, int align){

	// Allocate a buffer whose size is:
	// - The given data size
	// - A gap to align the sem_t structure in memory
	// - The size of a semaphore
	int gap = gapForAlignment(size, align, CACHE_LINE_SIZE);
	*pointer = HeapMemMP_alloc(sharedHeap, size+sizeof(sem_t)+ gap, align);

	// Initialize the semaphore to nbFree - 1
	sem_init(((sem_t*)((char*)(*pointer)+size + gap)), 0, nbFree-1, mutexID);

	return *pointer;
}

void*  merged_free(HeapMemMP_Handle sharedHeap, void* pointer, int size){
	char* ptr = (char*)pointer;
	int gap = gapForAlignment(size, (unsigned long)pointer, CACHE_LINE_SIZE);

	if(sem_trywait(((sem_t*)(ptr+size+gap))) == -1){
		// If the semaphore value was 0
		// The current free is the last so we do
		// the free
		sem_destroy(((sem_t*)(ptr+size+gap)));
		HeapMemMP_free(sharedHeap, pointer, size+gap+sizeof(sem_t));
		return 0;
	}

	return pointer;
}
#endif
