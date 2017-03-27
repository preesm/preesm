// main.cpp: calls the Scheduler to dispatch the jobs corresponding to the application
//
#ifndef JOB_HEADER
#define JOB_HEADER

#include "stdafx.h"
#include <iostream>
#include <string>
#include <windows.h>
using namespace std;

// useful macro defines
#include "jobDefines.h"

typedef struct job_descriptor_st{

	unsigned short id;
	unsigned int time;
	void (*fct_pointer)(job_descriptor_st* job);
	unsigned short nb_preds;
	unsigned short pred_ids[MAX_PRED];
	void* buffers[MAX_BUF];
	int params[MAX_PARAM];
} job_descriptor;

#endif