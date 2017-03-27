#include "jobQueue.h"

#define MAX_JOB_NUMBER 20

/**
 Constructor

 @param create: 1 if we need to create the shared mem. 0 if we connect to it.
 @param id: the id of the queue
*/
JobQueue::JobQueue(int create, int id)
{
	memory = new ProtectedSharedMemory(create,MAX_JOB_NUMBER*sizeof(job_descriptor) + sizeof(queue_properties),id);

	if(create){
		queue_properties props = {0,0,0};
		writeProperties(props);
	}
}

/**
 Destructor
*/
JobQueue::~JobQueue()
{
	delete memory;
}

/**
 Pushes a job in the queue

 @param job: the input job being pushed
 @return: 1 if it worked; 0 otherwise
*/
int JobQueue::pushJob(job_descriptor* job)
{
	queue_properties props = readProperties();

	if(props.jobCount < MAX_JOB_NUMBER){
		int charIndex = props.headIndex*sizeof(job_descriptor) + sizeof(queue_properties);
		memory->write((void*)job,charIndex,sizeof(job_descriptor));
		props.headIndex = props.headIndex + 1;
		props.jobCount = props.jobCount + 1;
		writeProperties(props);
		return 1;
	}

	return 0;
}

/**
 Pops a job from the queue

 @param job: the output job being popped
 @return: 1 if it worked; 0 otherwise
*/
int JobQueue::popJob(job_descriptor* job)
{
	queue_properties props = readProperties();

	if(props.jobCount > 0){
		int charIndex = props.tailIndex*sizeof(job_descriptor) + sizeof(queue_properties);
		memory->read((void*)job,charIndex,sizeof(job_descriptor));
		props.tailIndex = props.tailIndex + 1;
		props.jobCount = props.jobCount - 1;
		writeProperties(props);
		return 1;
	}

	return 0;
}

/**
 Getting the number of jobs in the queue

 @return: the number of jobs in the queue
*/
int JobQueue::getJobCount()
{
	queue_properties props = readProperties();
	return props.jobCount;
}

/**
 Reads the properties of the queue from the shared memory

 @param props: properties
*/
queue_properties JobQueue::readProperties()
{
	queue_properties props;
	memory->read((void*)&props,0,sizeof(queue_properties));
	return props;
}

/**
 Writes the properties of the queue in the shared memory

 @param props: properties
*/
void JobQueue::writeProperties(queue_properties props)
{
	memory->write((void*)&props,0,sizeof(queue_properties));
}
