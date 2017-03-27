#include "Master.h"


/**
 Constructor

 @param nbSlaves: number of connected slaves
 @param input_jobs: list of the input job descriptors
*/
Master::Master(int nbSlaves, job_descriptor input_jobs[])
{
	for(int i=0;i<JOB_NUMBER;i++){
		job_descriptor job = input_jobs[i];
		jobs[i] = job;
	}

	for(int i=0;i<nbSlaves;i++){
		jobQueues[i] = new JobQueue(1,i);
	}

	this->nbSlaves = nbSlaves;
}

/**
 Destructor
*/
Master::~Master()
{
	//delete[] jobs;
	//delete jobQueue;
}

/**
 Launches the master. Runs until ESC is pressed
*/
void Master::launch(){

	int isReady = 0;
	// Local history tables
	int slaveJobRecordSizes[MAX_NB_SLAVES]; // a table giving for each slave the total number of jobs that have been executed by it for this iteration
	unsigned short slaveJobRecord[MAX_NB_SLAVES][MAX_NB_JOBS]; // For each slave, gives the ordered list of all jobs that have been executed by it for this iteration
	unsigned short previousMappingOfJobs[MAX_NB_JOBS]; // For each job, gives the last mapping choice
	unsigned short nbPendingJobs[MAX_NB_SLAVES]; // a table giving for each slave the current number of pending jobs

	// Loop exectuted once per loop iteration
	while(1){

		// Resets the history
		for(int i=0;i<nbSlaves;i++){
			slaveJobRecordSizes[i] = 0;
		}

		// Loop on the jobs
		for(int i=0;i<JOB_NUMBER;i++){
			job_descriptor job = jobs[i];
			isReady = 0;

			// While the job is not ready to be executed, loops
			while(!isReady){
				// High value to get the minimum
				int minPendingJobs = 1000000;
				int mostAvailableSlaveId = 0;

				// Loop on the slaves looking for how many jobs are pending on each one
				for(int j=0;j<nbSlaves;j++){
					nbPendingJobs[j] = jobQueues[j]->getJobCount();

					if(minPendingJobs>nbPendingJobs[j]){
						minPendingJobs = nbPendingJobs[j];
						mostAvailableSlaveId = j;
					}
				}

				// Testing if predecessors are ready without accessing shared memory (necessitates the local history tables)
				isReady = testPredecessorsCompletion(&job, nbPendingJobs, slaveJobRecordSizes, slaveJobRecord, previousMappingOfJobs);

				// If the job is ready, posts it to the slave with the minimal number of pending tasks
				if(isReady){
					(jobQueues[mostAvailableSlaveId])->pushJob(&job);
					slaveJobRecord[mostAvailableSlaveId][slaveJobRecordSizes[mostAvailableSlaveId]] = job.id;
					slaveJobRecordSizes[mostAvailableSlaveId]++;
					previousMappingOfJobs[job.id] = mostAvailableSlaveId;
				}
				else if(GetAsyncKeyState(VK_ESCAPE) != 0){
					// Stops if ESC is pressed
					return;
				}
			}
		}
	}
}

/**
 Tests if all the predecessors of a job have been executed

 @param job: the input job tested
 @param nbPendingJobs: a table giving for each slave the current number of pending jobs
 @param slaveJobRecordSizes: a table giving for each slave the total number of jobs that have been executed by it for this iteration
 @param slaveJobRecord: For each slave, gives the ordered list of all jobs that have been executed by it for this iteration
 @param previousMappingOfJobs: For each job, gives the last mapping choice
 @return: 1 if all the predecessors of the job have been executed; 0 otherwise
*/
int Master::testPredecessorsCompletion(job_descriptor* job, unsigned short nbPendingJobs[], int slaveJobRecordSizes[], unsigned short slaveJobRecord[][MAX_NB_JOBS], 
									   unsigned short previousMappingOfJobs[]){

	// For each predecessor job, testing if it is not waiting to be executed by a slave
	for(int j=0;j<job->nb_preds;j++){
		int currentPredecessorId = job->pred_ids[j];
		// Getting the slave to which the job has been mapped
		int currentSlaveId = previousMappingOfJobs[currentPredecessorId];

		// Looking in the pending jobs of the slave if we find the predecessor. If it is so, the job is not ready to be processed.
		for(int k=0;k<nbPendingJobs[currentSlaveId];k++){
			int remainingJobId = slaveJobRecord[currentSlaveId][slaveJobRecordSizes[currentSlaveId]-1-k];
			if(remainingJobId == currentPredecessorId){
				return 0;
				break;
			}
		}
	}

	return 1;
}