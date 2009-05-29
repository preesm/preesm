#include "Master.h"


Master::Master(job_descriptor input_jobs[])
{
	string type = "master";
	this->jobQueue = new JobQueue(type);

	
	for(int i=0;i<JOB_NUMBER;i++){
		job_descriptor job = input_jobs[i];
		jobs[i] = job;
	}
}


Master::~Master()
{
	//delete[] jobs;
	//delete jobQueue;
}

void Master::launch(){

	while(1){
		for(int i=0;i<JOB_NUMBER;i++){
			job_descriptor job = jobs[i];
			job.fct_pointer(&job);
		}
	}
}
