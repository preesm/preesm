/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Daniel Madroñal <daniel.madronal@upm.es> (2019)
 * Julien Hascoet <jhascoet@kalray.eu> (2017)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
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
