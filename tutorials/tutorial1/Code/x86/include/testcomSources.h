/*
	============================================================================
	Name        : testComSources.h
	Author      : mpelcat
	Version     :
	Copyright   : Application specific function prototypes
	Description :
	============================================================================
*/

#ifndef TESTCOMSOURCE_H_
#define TESTCOMSOURCE_H_

void sensor_init(char* o1, char* o2, char* o3, int size);
void sensor2_init(char* o1, int size);
void parallel_init(char* i1, char* o1, int size);
void gen_int_init(char* i1,char* o1,char* o2, int size);
void copy_init(char* i1, char* o1, int size);
void actuator_init(char* i1,char* i2,char* i3, int size);
void circular_init(char* i1,char* o1, int size);
void circular6_init(char* i1,char* o1, int size);
void sensor(char* o1, char* o2, char* o3, int size);
void sensor2(char* o1, int size);
void parallel(char* i1, char* o1, int size);
void parallel2(char* i1, char* i2, char* o1, int size);
void gen_int(char* i1,char* i2,char* o1,char* o2, int size);
void copy(char* i1, char* o1, int size);
void circular(char* i1, char* i2, char* o1, char* o2, int size);
void circular6(char* i1, char* o1, int size);
void actuator(char* i1,char* i2,char* i3, int size);
#endif
