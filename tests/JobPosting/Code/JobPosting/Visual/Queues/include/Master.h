/*******************************************************************************
 * Copyright or © or Copr. %%LOWERDATE%% - %%UPPERDATE%% IETR/INSA:
 *
 * %%AUTHORS%%
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