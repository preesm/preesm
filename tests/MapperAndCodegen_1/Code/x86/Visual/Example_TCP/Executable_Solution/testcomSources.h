#ifndef TESTCOMSOURCE_H_
#define TESTCOMSOURCE_H_

extern void sensor_init(char* o1, char* o2);
extern void parallel_init(char* i1, char* o1);
extern void gen_int_init(char* i1,char* o1,char* o2);
extern void copy_init(char* i1, char* o1);
extern void actuator_init(char* i1,char* i2,char* i3);
extern void sensor(char* o1, char* o2, char* o3);
extern void parallel(char* i1, char* o1);
extern void gen_int(char* i1,char* i2,char* o1,char* o2);
extern void copy(char* i1, char* o1);
extern void actuator(char* i1,char* i2,char* i3,int size);
#endif
