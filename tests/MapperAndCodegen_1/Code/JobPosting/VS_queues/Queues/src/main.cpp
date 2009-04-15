// main.cpp: calls the Scheduler to dispatch the jobs corresponding to the application
//

#include "jobHeader.h"

// Code prototypes
#include "testComSources.h"

// Buffer declarations
#include "jobBuffers.h"
// table with the job descriptors
#include "jobList.h"

int _tmain(int argc, _TCHAR* argv[])
{
	int i = 0;

	while(1){
		for(i = 0;i<JOB_NUMBER;i++){
			job_descriptor job = jobs[i];
			job.fct_pointer(&job);
		}
	}
}

