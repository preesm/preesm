// main.cpp: calls the Scheduler to dispatch the jobs corresponding to the application
//

#include "stdafx.h"

int buf1[712];
int buf2[712];

#include "testComSources.h"
#include "jobDefines.h"
/*
#define MAX_PRED 4
#define JOB_NUMBER 2
*/

typedef struct job_descriptor_st{

	unsigned short id;
	unsigned int time;
	void (*fct_pointer)(void* buffers[], int params[]);
	unsigned short pred_ids[MAX_PRED];
	void* buf_pointers[MAX_BUF];
	int params[MAX_PARAM];
} job_descriptor;

/*
void test1(void* buffers[], int params[]){
}

void test2(void* buffers[], int params[]){
}

job_descriptor jobs[JOB_NUMBER] = {
	{0,10000,sensor,{},{},{}}
};*/

#include "jobList.h"
#include "testcomSources.h"

int _tmain(int argc, _TCHAR* argv[])
{
	int i = 0;

	for(i = 0;i<JOB_NUMBER;i++){
		job_descriptor job = jobs[i];
		job.fct_pointer(job.buf_pointers,job.params);
	}
}

