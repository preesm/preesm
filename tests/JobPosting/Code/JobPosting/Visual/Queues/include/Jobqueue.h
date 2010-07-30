#include "jobheader.h"
#include "ProtectedSharedMemory.h"

#ifndef JOBQUEUE
#define JOBQUEUE

/**
 Structure representing the FIFO parameters of the job queue
*/
typedef struct queue_properties_st{
	unsigned short headIndex; // Index of the head from where the jobs are popped
	unsigned short tailIndex; // Index of the tail to where the jobs are pushed
	unsigned short jobCount; // Number of jobs currently in the queue
} queue_properties;

/**
 The job queue contains a list of job descriptors filled by the master 
 and retrieved and processed by the slaves

 @author mpelcat
*/
class JobQueue {
	private:
		// Shared memory
		ProtectedSharedMemory* memory;
		
		/**
		 Reads the properties of the queue from the shared memory

		 @param props: properties
		*/
		queue_properties readProperties();

		/**
		 Writes the properties of the queue in the shared memory

		 @param props: properties
		*/
		void writeProperties(queue_properties props);
	public:

		/**
		 Constructor

		 @param create: 1 if we need to create the shared mem. 0 if we connect to it.
		 @param id: the id of the queue
		*/
		JobQueue(int create, int id);

		/**
		 Destructor
		*/
		~JobQueue();
		
		/**
		 Pushes a job in the queue

		 @param job: the input job being pushed
		 @return: 1 if it worked; 0 otherwise
		*/
		int pushJob(job_descriptor* job);
		
		/**
		 Pops a job from the queue

		 @param job: the output job being popped
		 @return: 1 if it worked; 0 otherwise
		*/
		int popJob(job_descriptor* job);

		/**
		 Getting the number of jobs in the queue

		 @return: the number of jobs in the queue
		*/
		int getJobCount();
};

#endif
