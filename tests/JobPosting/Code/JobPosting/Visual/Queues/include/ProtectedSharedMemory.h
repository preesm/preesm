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

		void createMutex();
		void connectMutex();
		int createMem();
		int connectMem();
	public:
		ProtectedSharedMemory(int create);
		~ProtectedSharedMemory();
		void read(void* buffer, int offset, int size);
		void write(void* buffer, int offset, int size);
	
};

#endif