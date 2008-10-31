#include <stdio.h>
#include <stdlib.h>
#include <std.h>
#include <tsk.h>
#include <log.h>
#define uchar unsigned char
#define ushort unsigned short
//#define uint unsigned int
#define ulong unsigned long
#define prec_synchro int
#define stream uchar
extern LOG_Obj trace;

#include "..\..\lib_RACH\common.h"

//Buffer allocation for C64x_1
#pragma DATA_SECTION(n4o1n8i2, ".my_sect")
#pragma DATA_ALIGN(n4o1n8i2, 8)
char[10] n4o1n8i2;

#pragma DATA_SECTION(n3o1n8i1, ".my_sect")
#pragma DATA_ALIGN(n3o1n8i1, 8)
char[10] n3o1n8i1;

#pragma DATA_SECTION(sem, ".my_sect")
#pragma DATA_ALIGN(sem, 8)
semaphore[8] sem;


// Main function
/* External Variables */
extern far int L2RAM;   /* Generated within BIOS configuration */
/* Handles for dynamically created tasks */
TSK_Handle computationThread_handle;
TSK_Attrs computationThread_attrib;
void computationThread(void);

TSK_Handle communicationThread_handle;
TSK_Attrs communicationThread_attrib;
void communicationThread(void);

void main(void)
{
/* Initialize attributes for Task computationThread */
memcpy(&computationThread_attrib,(void*)(&TSK_ATTRS),sizeof(TSK_Attrs));
computationThread_attrib.priority = 1;
computationThread_attrib.stack = NULL;
computationThread_attrib.stacksize = 0x9000;
computationThread_attrib.stackseg =L2RAM;
computationThread_attrib.environ = NULL;
computationThread_attrib.name = "MainTask";
computationThread_attrib.exitflag = TRUE;
computationThread_attrib.initstackflag = TRUE;
/* Create a task to do the work */
if( (computationThread_handle = TSK_create((Fxn)computationThread, &computationThread_attrib, 1)) == NULL)
{
/* Failure in Creating Task */
    LOG_printf(&trace,"TSK_create() error... computationThread");
    while(1);
}
/* Initialize attributes for Task communicationThread */
memcpy(&communicationThread_attrib,(void*)(&TSK_ATTRS),sizeof(TSK_Attrs));
communicationThread_attrib.priority = 1;
communicationThread_attrib.stack = NULL;
communicationThread_attrib.stacksize = 0x9000;
communicationThread_attrib.stackseg =L2RAM;
communicationThread_attrib.environ = NULL;
communicationThread_attrib.name = "MainTask";
communicationThread_attrib.exitflag = TRUE;
communicationThread_attrib.initstackflag = TRUE;
/* Create a task to do the work */
if( (communicationThread_handle = TSK_create((Fxn)communicationThread, &communicationThread_attrib, 1)) == NULL)
{
/* Failure in Creating Task */
    LOG_printf(&trace,"TSK_create() error... communicationThread");
    while(1);
}
}

//Thread: computationThread
void computationThread()
{


//beginningCode

{
	init_n4(n4o1n8i2);
	init_n3(n3o1n8i1);
	init_n8(n3o1n8i1,n4o1n8i2);
	init_n5();
	semaphorePost(sem[4], empty);
}


//loopCode


for(;;){
	semaphorePend(sem[0], full);
	n4(n4o1n8i2);
	semaphorePost(sem[1], empty);
	semaphorePend(sem[2], full);
	n3(n3o1n8i1);
	semaphorePost(sem[3], empty);
	semaphorePend(sem[4], empty);
	n8(n3o1n8i1,n4o1n8i2);
	semaphorePost(sem[5], full);
	semaphorePend(sem[6], full);
	n5();
	semaphorePost(sem[7], empty);
}



//endCode

{
	close_n4(n4o1n8i2);
	close_n3(n3o1n8i1);
	close_n8(n3o1n8i1,n4o1n8i2);
	close_n5();
}

}//end thread: computationThread

//Thread: communicationThread
void communicationThread()
{


//beginningCode

{
	semaphorePost(sem[7], empty);
	semaphorePost(sem[1], empty);
	semaphorePost(sem[3], empty);
}


//loopCode


for(;;){
	semaphorePend(sem[7], empty);
	receive(C64x_2,);
	semaphorePost(sem[6], full);
	semaphorePend(sem[1], empty);
	receive(C64x_2,);
	semaphorePost(sem[0], full);
	semaphorePend(sem[3], empty);
	receive(C64x_2,);
	semaphorePost(sem[2], full);
	semaphorePend(sem[5], full);
	send(C64x_2,);
	semaphorePost(sem[4], empty);
}



//endCode

{
}

}//end thread: communicationThread
