/*
 ============================================================================
 Name        : testcomSources.c
 Author      : mpelcat
 Version     :
 Copyright   : functions called by the communication tester project
 Description :
 ============================================================================
 */
#include "x86.h"

#define TESTVALUE 10

void sensor_init(char* o1, char* o2){

}

void parallel_init(char* i1, char* o1){

}

void gen_int_init(char* i1,char* o1,char* o2){

}

void copy_init(char* i1, char* o1){

}

void actuator_init(char* i1,char* i2,char* i3){

}

void sensor(char* o1, char* o2, char* o3){
	//printf("sensor");
	int i = 0;

	for(i=0;i<1000;i++){
		o3[i] = o2[i] = o1[i] = TESTVALUE;
	}
}

void parallel(char* i1, char* o1){
	//printf("parallel");
	int i = 0;

	for(i=0;i<1000;i++){
		o1[i] = i1[i];
	}
}

void gen_int(char* i1,char* i2,char* o1,char* o2){
	//printf("gen_int");
	int i = 0;

	for(i=0;i<1000;i++){
		o1[i] = i1[i];
		o2[i] = i2[i];
	}
}

void copy(char* i1, char* o1){
	//printf("copy");
	int i = 0;

	for(i=0;i<1000;i++){
		o1[i] = i1[i];
	}
}

void actuator(char* i1,char* i2,char* i3, int size){
	//printf("actuator");
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
	}

	if(bSuccess){
		printf("success\n");
	}
	else{
		printf("failure\n");
	}
}
