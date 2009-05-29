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