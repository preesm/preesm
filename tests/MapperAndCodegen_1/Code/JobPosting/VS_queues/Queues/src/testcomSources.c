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

#define TESTVALUE 10

void sensor(void* buffers[], int params[]){

	char* o1 = (char*)buffers[0];
	char* o2 = (char*)buffers[1];
	char* o3 = (char*)buffers[2];
	int i = 0;

	for(i=0;i<1000;i++){
		o3[i] = o2[i] = o1[i] = TESTVALUE;
		//o4[i] = 12;
	}
}

void sensor2(void* buffers[], int params[]){

	char* o1 = (char*)buffers[0];

	int i = 0;

	for(i=0;i<1000;i++){
		o1[i] = TESTVALUE;
		//o4[i] = 12;
	}
}

void parallel(void* buffers[], int params[]){

	char* i1 = (char*)buffers[0];
	char* o1 = (char*)buffers[1];

	int i = 0;

	for(i=0;i<1000;i++){
		o1[i] = i1[i];
	}
}

void gen_int(void* buffers[], int params[]){

	char* i1 = (char*)buffers[0];
	char* i2 = (char*)buffers[1];
	char* o1 = (char*)buffers[2];
	char* o2 = (char*)buffers[3];

	int i = 0;

	for(i=0;i<1000;i++){
		o1[i] = i1[i];
		o2[i] = i2[i];
	}
}

void copy(void* buffers[], int params[]){
	
	char* i1 = (char*)buffers[0];
	char* o1 = (char*)buffers[1];
	int size = params[0];
	int divFactor = params[1];

	int i = 0;

	for(i=0;i<1000;i++){
		o1[i] = i1[i];
	}
}

void circular(void* buffers[], int params[]){

	char* i1 = (char*)buffers[0];
	char* i2 = (char*)buffers[1];
	char* o1 = (char*)buffers[2];
	char* o2 = (char*)buffers[3];
	int i = 0;

	for(i=0;i<1000;i++){
		o1[i] = i1[i]-1;
		o2[i] = i2[i]-1;
	}
}
void circular6(void* buffers[], int params[]){
	
	char* i1 = (char*)buffers[0];
	char* o1 = (char*)buffers[1];

	int i = 0;

	for(i=0;i<1000;i++){
		o1[i] = i1[i]-11;
	}
}

void actuator(void* buffers[], int params[]){

	char* i1 = (char*)buffers[0];
	char* i2 = (char*)buffers[1];
	char* i3 = (char*)buffers[2];
	int size = params[0];

	int i = 0;
	int bSuccess = 1;

	for(i=0;i<size;i++){
		if(i1[i] != TESTVALUE){
			bSuccess = 0;
			break;
		}
		if(i2[i] != TESTVALUE){
			bSuccess = 0;
			break;
		}
		if(i3[i] != TESTVALUE){
			bSuccess = 0;
			break;
		}
		/*if(i4[i] != 1){
			bSuccess = 0;
			break;
		}*/
	}

	if(bSuccess){
		printf("success\n");
	}
	else{
		printf("failure\n");
	}
}


