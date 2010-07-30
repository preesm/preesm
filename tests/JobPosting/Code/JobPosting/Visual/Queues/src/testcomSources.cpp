/*
 ============================================================================
 Name        : testcomSources.c
 Author      : mpelcat
 Version     :
 Copyright   : functions called by the communication tester project
 Description :
 ============================================================================
 */
#include "testcomSources.h"
#include <stdio.h>

#define TESTVALUE 10

void sensor(job_descriptor* job){

	char* o1 = (char*)job->buffers[0];
	char* o2 = (char*)job->buffers[1];
	char* o3 = (char*)job->buffers[2];
	int i = 0;

	for(i=0;i<1000;i++){
		o3[i] = o2[i] = o1[i] = i%128;
	}
}

void sensor2(job_descriptor* job){

	char* o1 = (char*)job->buffers[0];

	int i = 0;

	for(i=0;i<1000;i++){
		o1[i] = i%128;
	}
}

void parallel(job_descriptor* job){

	char* i1 = (char*)job->buffers[0];
	char* o1 = (char*)job->buffers[1];

	int i = 0;

	for(i=0;i<1000;i++){
		o1[i] = i1[i];
	}
}

void gen_int(job_descriptor* job){

	char* i1 = (char*)job->buffers[0];
	char* i2 = (char*)job->buffers[1];
	char* o1 = (char*)job->buffers[2];
	char* o2 = (char*)job->buffers[3];

	int i = 0;

	for(i=0;i<1000;i++){
		o1[i] = i1[i];
		o2[i] = i2[i];
	}
}

void copy(job_descriptor* job){
	
	char* i1 = (char*)job->buffers[0];
	char* o1 = (char*)job->buffers[1];
	int size = job->params[0];

	int i = 0;

	for(i=0;i<1000;i++){
		o1[i] = i1[i];
	}
}

void actuator(job_descriptor* job){

	char* i1 = (char*)job->buffers[0];
	char* i2 = (char*)job->buffers[1];
	char* i3 = (char*)job->buffers[2];
	int size = job->params[0];

	int i = 0;
	int bSuccess = 1;

	for(i=0;i<size;i++){
		if(i1[i] != i%128){
			bSuccess = 0;
			break;
		}
		if(i2[i] != i%128){
			bSuccess = 0;
			break;
		}
		if(i3[i] != i%128){
			bSuccess = 0;
			break;
		}
	}

	if(bSuccess){
		printf("success\n");
	}
	else{
		printf("failure\n");
	}
}


