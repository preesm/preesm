// main.cpp: calls the Scheduler to dispatch the jobs corresponding to the application
//

#include "stdafx.h"

int buf1[712];
int buf2[712];

#include "testComSources.h"
#include "jobDefines.h"
/*
#define MAX_BUF 5
#define MAX_PRED 4
#define MAX_PARAM 4
#define JOB_NUMBER 2
*/

typedef struct job_descriptor_st{

	unsigned short id;
	unsigned int time;
	void (__cdecl *fct_pointer)(void* buffers[], int params[]);
	//void* buf_pointers[MAX_BUF];
	//int params[MAX_PARAM];
	unsigned short pred_ids[MAX_PRED];
} job_descriptor;

/*
void test1(job_descriptor desc){
}

void test2(job_descriptor desc){
}

job_descriptor jobs[JOB_NUMBER] = {
	// test1
	{1,1000,test1,{buf1},{}},
	// test1
	{2,1000,test2,{buf1},{1}}
};*/

#include "jobList.h"
#include "testcomSources.h"

int _tmain(int argc, _TCHAR* argv[])
{
	int i = 0;
	job_descriptor job;

	for(i = 0;i<JOB_NUMBER;i++){
		job_descriptor job = jobs[i];
		job.fct_pointer();
	}
}

