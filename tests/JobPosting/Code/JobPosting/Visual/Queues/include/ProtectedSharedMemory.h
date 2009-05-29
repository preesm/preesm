#include "jobheader.h"

#ifndef PROTECTEDSHAREDMEMORY
#define PROTECTEDSHAREDMEMORY

/**
 A memory shared between processes and protected via mutexes
*/
class ProtectedSharedMemory {
	private:
		HANDLE namedPipe;
		HANDLE hMapFile;
		void* memoryPointer;
		HANDLE hMutex; 

		void createMutex();
		void connectMutex();
		int createMem(int size));
		int connectMem();
	public:
		ProtectedSharedMemory(int size);
		~ProtectedSharedMemory();
		int read(void* buffer, int offset, int size);
		void write(void* buffer, int offset, int size);
	
};

#endif