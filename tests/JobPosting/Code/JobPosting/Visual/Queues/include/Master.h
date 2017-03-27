#include "jobheader.h"
#include "JobQueue.h"

#ifndef MASTER
#define MASTER

#define MAX_NB_SLAVES 10
#define MAX_NB_JOBS 100

/**
 The master posts the jobs to the slaves that execute them. It creates as many job queues as there are slaves
 and dispatches the jobs to these slaves

 @author mpelcat
*/
class Master {

	private :
		// Table of the input jobs
		job_descriptor jobs[JOB_NUMBER];
		// One queue for each connected slave
		(JobQueue*) jobQueues[MAX_NB_SLAVES];
		// Number of slaves
		int nbSlaves;

		/**
		 Tests if all the predecessors of a job have been executed

		 @param job: the input job tested
		 @param nbPendingJobs: a table giving for each slave the current number of pending jobs
		 @param slaveJobRecordSizes: a table giving for each slave the total number of jobs that have been executed by it for this iteration
		 @param slaveJobRecord: For each slave, gives the ordered list of all jobs that have been executed by it for this iteration
		 @param previousMappingOfJobs: For each job, gives the last mapping choice
		 @return: 1 if all the predecessors of the job have been executed; 0 otherwise
		*/
		int testPredecessorsCompletion(job_descriptor* job, unsigned short nbPendingJobs[], int slaveJobRecordSizes[], unsigned short slaveJobRecord[][MAX_NB_JOBS], 
									   unsigned short previousMappingOfJobs[]);
	public : 
		/**
		 Constructor

		 @param nbSlaves: number of connected slaves
		 @param input_jobs: list of the input job descriptors
		*/
		Master(int nbSlaves, job_descriptor input_jobs[]);

		/**
		 Destructor
		*/
		~Master();

		/**
		 Launches the master. Runs until ESC is pressed
		*/
		void launch(void);
};

#endif