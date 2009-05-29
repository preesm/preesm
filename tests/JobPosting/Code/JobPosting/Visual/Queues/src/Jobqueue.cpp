#include "jobQueue.h"

JobQueue::JobQueue()
{
}

JobQueue::JobQueue(string type)
{

	if(type == "master"){
		memory = new ProtectedSharedMemory(1);
	}
	else{
		memory = new ProtectedSharedMemory(0);
	}
}

JobQueue::~JobQueue()
{
	delete memory;
}
