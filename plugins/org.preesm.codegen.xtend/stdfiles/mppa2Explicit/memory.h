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
