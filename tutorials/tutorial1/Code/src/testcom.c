/*
	============================================================================
	Name        : testComSources.c
	Author      : mpelcat
	Version     : 1.0
	Copyright   : CECILL-C
	Description : Dummy functions called by the communication tester project
	============================================================================
 */

#include "x86.h"

int sensorIncrement = 0;
int actuatorIncrement = 0;

void sensor_init(char* o1, char* o2, char* o3, int size){

}

void sensor2_init(char* o1, int size){
}

void parallel_init(char* i1, char* o1, int size){

}

void gen_int_init(char* i1,char* o1,char* o2, int size){

}

void copy_init(char* i1, char* o1, int size){

}

void actuator_init(char* i1,char* i2,char* i3, int size){

}

void circular_init(char* i1,char* o1, int size){

}
void circular6_init(char* i1,char* o1, int size){

}

void sensor(char* o1, char* o2, char* o3, int size){
	//printf("sensor");
	// Generating dummy data
	int i = 0;

	for(i=0;i<size;i++){
		o3[i] = o2[i] = o1[i] = (i + sensorIncrement) % 128;
	}

	sensorIncrement++;
}

void sensor2(char* o1, int size){
	//printf("sensor");
	// Generating dummy data
	int i = 0;

	for(i=0;i<size;i++){
		o1[i] = -i%128;
	}
}

void parallel(char* i1, char* o1, int size){
	//printf("parallel");
	int i = 0;

	for(i=0;i<size;i++){
		o1[i] = i1[i];
	}
}

void parallel2(char* i1, char* i2, char* o1, int size){
	//printf("parallel");
	int i = 0;

	for(i=0;i<size;i++){
		o1[i] = i1[i];
	}
}

void gen_int(char* i1,char* i2,char* o1,char* o2, int size){
	//printf("gen_int");
	int i = 0;

	for(i=0;i<size;i++){
		o1[i] = i1[i];
		o2[i] = i2[i];
	}
}

void copy(char* i1, char* o1, int size){
	//printf("copy");
	int i = 0;

	for(i=0;i<size;i++){
		o1[i] = i1[i];
	}
}

void circular(char* i1, char* i2, char* o1, char* o2, int size){
	//printf("circular");
	int i = 0;

	for(i=0;i<size;i++){
		o1[i] = i1[i];
		o2[i] = i2[i];
	}
}
void circular6(char* i1, char* o1, int size){
	//printf("circular6");
	int i = 0;

	for(i=0;i<size;i++){
		o1[i] = i1[i];
	}
}

void actuator(char* i1,char* i2,char* i3, int size){
	//printf("actuator");
	// Testing dummy data
	int i = 0;
	int bSuccess = 1;

	for(i=0;i<size;i++){
		if(i1[i] != (char)((i + actuatorIncrement) %128)){
			bSuccess = 0;
			break;
		}
		if(i2[i] != (char)((i + actuatorIncrement) %128)){
			bSuccess = 0;
			break;
		}
		if(i3[i] != (char)(-i%128)){
			bSuccess = 0;
			break;
		}
	}

	actuatorIncrement++;

	if(bSuccess){
		printf("Actuator received the right data\n");
	}
	else{
		printf("Failure\n");
		exit(-1);
	}
}


