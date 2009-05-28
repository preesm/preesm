#include "jobheader.h"

#ifndef JOBQUEUE
#define JOBQUEUE

/**
 The job queue contains a list of job descriptors filled by the master and used by the slaves
*/
class JobQueue {
	private:
		HANDLE namedPipe;
	public:
		JobQueue();
		JobQueue(string type);
	
};

#endif