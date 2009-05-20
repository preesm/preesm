#include "jobheader.h"
#include "JobQueue.h"

#ifndef MASTER
#define MASTER

/**
 The master posts the jobs 
*/
class Master {
	private :
		job_descriptor jobs[JOB_NUMBER];
		JobQueue* jobQueue;

	public : 
		Master(job_descriptor input_jobs[]);
		~Master();
		void launch(void);
};

#endif