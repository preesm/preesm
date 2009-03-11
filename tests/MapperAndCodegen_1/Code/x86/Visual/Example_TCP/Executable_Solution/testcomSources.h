#ifndef TESTCOMSOURCE_H_
#define TESTCOMSOURCE_H_

void sensor_init(char* o1, char* o2);
void parallel_init(char* i1, char* o1);
void gen_int_init(char* i1,char* o1,char* o2);
void copy_init(char* i1, char* o1);
void actuator_init(char* i1,char* i2,char* i3);
void sensor(char* o1, char* o2, char* o3);
void parallel(char* i1, char* o1);
void gen_int(char* i1,char* i2,char* o1,char* o2);
void copy(char* i1, char* o1);
void actuator(char* i1,char* i2,char* i3,int size);

#endif
