/*******************************************************************************
 * Copyright or © or Copr. 2009 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2009 - 2010)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
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
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
#include "jobheader.h"

#ifndef PROTECTEDSHAREDMEMORY
#define PROTECTEDSHAREDMEMORY

/**
 A memory shared between processes and protected via mutexes. 
 The write method is protected against race condition.

 @author mpelcat
*/
class ProtectedSharedMemory {
	private:
		// Shared file created to share data
		HANDLE hMapFile;
		// Pointer to the shared data
		void* memoryPointer;
		// Mutex to protect writing method
		HANDLE hMutex;

		/**
		 Creating the mutex to protect writing method
		*/
		void createMutex(LPCWSTR name);
		
		/**
		 Connecting to the mutex created in another process
		*/
		void connectMutex(LPCWSTR name);

		/**
		 Creating the shared file and initializing pointer to shared memory

		 @param size: the size of the memory
		 @param name: the name of the memory used to connect it in another process
		 @return: 1 if it worked; 0 otherwise
		*/
		int createMem(int size, LPCWSTR name);

		/**
		 Connecting to the memory created in another process

		 @param size: the size of the memory
		 @param name: the name of the memory used to connect it
		 @return: 1 if it worked; 0 otherwise
		*/
		int connectMem(int size, LPCWSTR name);
	public:
		/**
		 Constructor 

		 @param create: 1 if memory and mutex need to be created
		 @param size: the size of the memory
		 @param id: the id from which is derived the name of the memory
		*/
		ProtectedSharedMemory(int create, int size, int id);

		/**
		 Destructor
		*/
		~ProtectedSharedMemory();

		/**
		 Reading data from memory
		 
		 @param buffer: returned buffer
		 @param offset: offset of the address where to start reading
		 @param size: size of the copied data
		 @return: 1 if it worked; 0 otherwise
		*/
		int read(void* buffer, int offset, int size);

		/**
		 Writing data to memory
		 
		 @param buffer: input buffer
		 @param offset: offset of the address where to start writing
		 @param size: size of the copied data
		 @return: 1 if it worked; 0 otherwise
		*/
		void write(void* buffer, int offset, int size);
	
};

#endif
