/*
	============================================================================
	Name        : memory.c
	Author      : kdesnos
	Version     : 1.0
	Copyright   : CECILL-C
	Description : Dynamic memory allocation primitives.
	              These functions replace the regular malloc() and free()
				  functions when code is printed with dynamic allocation and
				  mergeable broadcast and roundbuffers is activated.
	============================================================================
*/
#include <stdlib.h>
#include <pthread.h>
#include <semaphore.h>
#include <memory.h>

void * merged_malloc(int size, int nbFree){
	char* pointer;

	// Allocate a buffer whose size is:
	// - The given data size
	// - The size of a semaphore
	pointer = (char*) malloc(size+sizeof(sem_t));

	// Initialize the semaphore to nbFree - 1
	sem_init(((sem_t*)(pointer+size)), 0, nbFree-1);

	return (void*) pointer;
}

void* multiple_malloc(void **pointer, int size, int nbFree, sem_t* mutex)
{

	sem_wait(mutex);
	if(*pointer == 0){
		*pointer =  (void*) merged_malloc(size,nbFree);
	}
	sem_post(mutex);

	return *pointer;
}

void*  merged_free(void* pointer, int size){
	char* ptr = (char*)pointer;
	if(sem_trywait(((sem_t*)(ptr+size))) == -1){
		// If the semaphore value was 0
		// The current free is the last so we do
		// the free
		sem_destroy(((sem_t*)(ptr+size)));
		free(pointer);
		return 0;
	}

	return pointer;
}
