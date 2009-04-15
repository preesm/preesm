// main.cpp: calls the Scheduler to dispatch the jobs corresponding to the application
//

#include "stdafx.h"

// Code prototypes
#include "testComSources.h"

// Buffer declarations
#include "jobBuffers.h"
// useful macro defines
#include "jobDefines.h"

typedef struct job_descriptor_st{

	unsigned short id;
	unsigned int time;
	void (*fct_pointer)(void* buffers[], int params[]);
	unsigned short pred_ids[MAX_PRED];
	void* buf_pointers[MAX_BUF];
	int params[MAX_PARAM];
} job_descriptor;

// table with the job descriptors
#include "jobList.h"

int _tmain(int argc, _TCHAR* argv[])
{
	int i = 0;

	while(1){
		for(i = 0;i<JOB_NUMBER;i++){
			job_descriptor job = jobs[i];
			job.fct_pointer(job.buf_pointers,job.params);
		}
	}
}

