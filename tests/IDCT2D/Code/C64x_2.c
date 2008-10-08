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

//Buffer allocation for C64x_2
#pragma DATA_SECTION(Trigger_0fireHierarchicalIdct2d_2_ReadBlockblock_In, ".my_sect")
#pragma DATA_ALIGN(Trigger_0fireHierarchicalIdct2d_2_ReadBlockblock_In, 8)
char[10] Trigger_0fireHierarchicalIdct2d_2_ReadBlockblock_In;

#pragma DATA_SECTION(Trigger_0fireHierarchicalIdct2d_2_ReadBlock_1block_In, ".my_sect")
#pragma DATA_ALIGN(Trigger_0fireHierarchicalIdct2d_2_ReadBlock_1block_In, 8)
char[10] Trigger_0fireHierarchicalIdct2d_2_ReadBlock_1block_In;

#pragma DATA_SECTION(Trigger_0fireHierarchicalIdct2d_1_ReadBlock_1block_In, ".my_sect")
#pragma DATA_ALIGN(Trigger_0fireHierarchicalIdct2d_1_ReadBlock_1block_In, 8)
char[10] Trigger_0fireHierarchicalIdct2d_1_ReadBlock_1block_In;

#pragma DATA_SECTION(HierarchicalIdct2d_2_ReadBlockblock_outHierarchicalIdct2d_2_idct1drow_in, ".my_sect")
#pragma DATA_ALIGN(HierarchicalIdct2d_2_ReadBlockblock_outHierarchicalIdct2d_2_idct1drow_in, 8)
char[10] HierarchicalIdct2d_2_ReadBlockblock_outHierarchicalIdct2d_2_idct1drow_in;

#pragma DATA_SECTION(HierarchicalIdct2d_2_idct1drow_outHierarchicalIdct2d_2_transposeblock_in, ".my_sect")
#pragma DATA_ALIGN(HierarchicalIdct2d_2_idct1drow_outHierarchicalIdct2d_2_transposeblock_in, 8)
char[10] HierarchicalIdct2d_2_idct1drow_outHierarchicalIdct2d_2_transposeblock_in;

#pragma DATA_SECTION(HierarchicalIdct2d_2_transposeblock_outHierarchicalIdct2d_2_ReadBlock_1block_trans, ".my_sect")
#pragma DATA_ALIGN(HierarchicalIdct2d_2_transposeblock_outHierarchicalIdct2d_2_ReadBlock_1block_trans, 8)
char[10] HierarchicalIdct2d_2_transposeblock_outHierarchicalIdct2d_2_ReadBlock_1block_trans;

#pragma DATA_SECTION(HierarchicalIdct2d_2_transposeblock_outHierarchicalIdct2d_2_clipdouble_block, ".my_sect")
#pragma DATA_ALIGN(HierarchicalIdct2d_2_transposeblock_outHierarchicalIdct2d_2_clipdouble_block, 8)
char[10] HierarchicalIdct2d_2_transposeblock_outHierarchicalIdct2d_2_clipdouble_block;

#pragma DATA_SECTION(HierarchicalIdct2d_2_ReadBlock_1block_outHierarchicalIdct2d_2_idct1d_1row_in, ".my_sect")
#pragma DATA_ALIGN(HierarchicalIdct2d_2_ReadBlock_1block_outHierarchicalIdct2d_2_idct1d_1row_in, 8)
char[10] HierarchicalIdct2d_2_ReadBlock_1block_outHierarchicalIdct2d_2_idct1d_1row_in;

#pragma DATA_SECTION(HierarchicalIdct2d_2_idct1d_1row_outHierarchicalIdct2d_2_transpose_1block_in, ".my_sect")
#pragma DATA_ALIGN(HierarchicalIdct2d_2_idct1d_1row_outHierarchicalIdct2d_2_transpose_1block_in, 8)
char[10] HierarchicalIdct2d_2_idct1d_1row_outHierarchicalIdct2d_2_transpose_1block_in;

#pragma DATA_SECTION(HierarchicalIdct2d_2_transpose_1block_outHierarchicalIdct2d_2_clipdouble_block, ".my_sect")
#pragma DATA_ALIGN(HierarchicalIdct2d_2_transpose_1block_outHierarchicalIdct2d_2_clipdouble_block, 8)
char[10] HierarchicalIdct2d_2_transpose_1block_outHierarchicalIdct2d_2_clipdouble_block;

#pragma DATA_SECTION(HierarchicalIdct2d_1_ReadBlock_1block_outHierarchicalIdct2d_1_idct1d_1row_in, ".my_sect")
#pragma DATA_ALIGN(HierarchicalIdct2d_1_ReadBlock_1block_outHierarchicalIdct2d_1_idct1d_1row_in, 8)
char[10] HierarchicalIdct2d_1_ReadBlock_1block_outHierarchicalIdct2d_1_idct1d_1row_in;

#pragma DATA_SECTION(HierarchicalIdct2d_1_idct1d_1row_outHierarchicalIdct2d_1_transpose_1block_in, ".my_sect")
#pragma DATA_ALIGN(HierarchicalIdct2d_1_idct1d_1row_outHierarchicalIdct2d_1_transpose_1block_in, 8)
char[10] HierarchicalIdct2d_1_idct1d_1row_outHierarchicalIdct2d_1_transpose_1block_in;

#pragma DATA_SECTION(HierarchicalIdct2d_1_transpose_1block_outHierarchicalIdct2d_1_clipdouble_block, ".my_sect")
#pragma DATA_ALIGN(HierarchicalIdct2d_1_transpose_1block_outHierarchicalIdct2d_1_clipdouble_block, 8)
char[10] HierarchicalIdct2d_1_transpose_1block_outHierarchicalIdct2d_1_clipdouble_block;

#pragma DATA_SECTION(sem, ".my_sect")
#pragma DATA_ALIGN(sem, 8)
semaphore[10] sem;


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

//Variables allocation for computationThread
int i;


//beginningCode

{
	init_Trigger_0(Trigger_0fireHierarchicalIdct2d_1_ReadBlock_1block_In,Trigger_0fireHierarchicalIdct2d_2_ReadBlock_1block_In,Trigger_0fireHierarchicalIdct2d_2_ReadBlockblock_In);
	init_HierarchicalIdct2d_2_ReadBlock(HierarchicalIdct2d_2_ReadBlockblock_outHierarchicalIdct2d_2_idct1drow_in,Trigger_0fireHierarchicalIdct2d_2_ReadBlockblock_In);
	init_HierarchicalIdct2d_2_idct1d(HierarchicalIdct2d_2_ReadBlockblock_outHierarchicalIdct2d_2_idct1drow_in,HierarchicalIdct2d_2_idct1drow_outHierarchicalIdct2d_2_transposeblock_in);
	init_HierarchicalIdct2d_2_transpose(HierarchicalIdct2d_2_idct1drow_outHierarchicalIdct2d_2_transposeblock_in,HierarchicalIdct2d_2_transposeblock_outHierarchicalIdct2d_2_ReadBlock_1block_trans,HierarchicalIdct2d_2_transposeblock_outHierarchicalIdct2d_2_clipdouble_block);
	init_HierarchicalIdct2d_2_ReadBlock_1(HierarchicalIdct2d_2_ReadBlock_1block_outHierarchicalIdct2d_2_idct1d_1row_in,HierarchicalIdct2d_2_transposeblock_outHierarchicalIdct2d_2_ReadBlock_1block_trans,Trigger_0fireHierarchicalIdct2d_2_ReadBlock_1block_In);
	init_HierarchicalIdct2d_2_idct1d_1(HierarchicalIdct2d_2_ReadBlock_1block_outHierarchicalIdct2d_2_idct1d_1row_in,HierarchicalIdct2d_2_idct1d_1row_outHierarchicalIdct2d_2_transpose_1block_in);
	init_HierarchicalIdct2d_2_transpose_1(HierarchicalIdct2d_2_idct1d_1row_outHierarchicalIdct2d_2_transpose_1block_in,HierarchicalIdct2d_2_transpose_1block_outHierarchicalIdct2d_2_clipdouble_block);
	init_HierarchicalIdct2d_2_clip(HierarchicalIdct2d_2_transpose_1block_outHierarchicalIdct2d_2_clipdouble_block,HierarchicalIdct2d_2_transposeblock_outHierarchicalIdct2d_2_clipdouble_block);
	init_HierarchicalIdct2d_1_ReadBlock_1(HierarchicalIdct2d_1_ReadBlock_1block_outHierarchicalIdct2d_1_idct1d_1row_in,Trigger_0fireHierarchicalIdct2d_1_ReadBlock_1block_In);
	init_HierarchicalIdct2d_1_idct1d_1(HierarchicalIdct2d_1_ReadBlock_1block_outHierarchicalIdct2d_1_idct1d_1row_in,HierarchicalIdct2d_1_idct1d_1row_outHierarchicalIdct2d_1_transpose_1block_in);
	init_HierarchicalIdct2d_1_transpose_1(HierarchicalIdct2d_1_idct1d_1row_outHierarchicalIdct2d_1_transpose_1block_in,HierarchicalIdct2d_1_transpose_1block_outHierarchicalIdct2d_1_clipdouble_block);
	init_HierarchicalIdct2d_1_clip(HierarchicalIdct2d_1_transpose_1block_outHierarchicalIdct2d_1_clipdouble_block);
	semaphorePost(sem[0], empty);
	semaphorePost(sem[2], empty);
	semaphorePost(sem[4], empty);
}


//loopCode


for(;;){
	semaphorePend(sem[0], empty);
	semaphorePend(sem[2], empty);
	semaphorePend(sem[4], empty);
	Trigger_0(Trigger_0fireHierarchicalIdct2d_1_ReadBlock_1block_In,Trigger_0fireHierarchicalIdct2d_2_ReadBlock_1block_In,Trigger_0fireHierarchicalIdct2d_2_ReadBlockblock_In);
	semaphorePost(sem[5], full);
	semaphorePost(sem[3], full);
	semaphorePost(sem[1], full);
	HierarchicalIdct2d_2_ReadBlock(HierarchicalIdct2d_2_ReadBlockblock_outHierarchicalIdct2d_2_idct1drow_in,Trigger_0fireHierarchicalIdct2d_2_ReadBlockblock_In);
	for(i = 0; i < 8 ; i += 1){
		HierarchicalIdct2d_2_idct1d(HierarchicalIdct2d_2_ReadBlockblock_outHierarchicalIdct2d_2_idct1drow_in[i] ,HierarchicalIdct2d_2_idct1drow_outHierarchicalIdct2d_2_transposeblock_in[i] );
	}
	HierarchicalIdct2d_2_transpose(HierarchicalIdct2d_2_idct1drow_outHierarchicalIdct2d_2_transposeblock_in,HierarchicalIdct2d_2_transposeblock_outHierarchicalIdct2d_2_ReadBlock_1block_trans,HierarchicalIdct2d_2_transposeblock_outHierarchicalIdct2d_2_clipdouble_block);
	HierarchicalIdct2d_2_ReadBlock_1(HierarchicalIdct2d_2_ReadBlock_1block_outHierarchicalIdct2d_2_idct1d_1row_in,HierarchicalIdct2d_2_transposeblock_outHierarchicalIdct2d_2_ReadBlock_1block_trans,Trigger_0fireHierarchicalIdct2d_2_ReadBlock_1block_In);
	for(i = 0; i < 8 ; i += 1){
		HierarchicalIdct2d_2_idct1d_1(HierarchicalIdct2d_2_ReadBlock_1block_outHierarchicalIdct2d_2_idct1d_1row_in[i] ,HierarchicalIdct2d_2_idct1d_1row_outHierarchicalIdct2d_2_transpose_1block_in[i] );
	}
	HierarchicalIdct2d_2_transpose_1(HierarchicalIdct2d_2_idct1d_1row_outHierarchicalIdct2d_2_transpose_1block_in,HierarchicalIdct2d_2_transpose_1block_outHierarchicalIdct2d_2_clipdouble_block);
	HierarchicalIdct2d_2_clip(HierarchicalIdct2d_2_transpose_1block_outHierarchicalIdct2d_2_clipdouble_block,HierarchicalIdct2d_2_transposeblock_outHierarchicalIdct2d_2_clipdouble_block);
	semaphorePend(sem[6], full);
	HierarchicalIdct2d_1_ReadBlock_1(HierarchicalIdct2d_1_ReadBlock_1block_outHierarchicalIdct2d_1_idct1d_1row_in,Trigger_0fireHierarchicalIdct2d_1_ReadBlock_1block_In);
	semaphorePost(sem[7], empty);
	for(i = 0; i < 8 ; i += 1){
		HierarchicalIdct2d_1_idct1d_1(HierarchicalIdct2d_1_ReadBlock_1block_outHierarchicalIdct2d_1_idct1d_1row_in[i] ,HierarchicalIdct2d_1_idct1d_1row_outHierarchicalIdct2d_1_transpose_1block_in[i] );
	}
	HierarchicalIdct2d_1_transpose_1(HierarchicalIdct2d_1_idct1d_1row_outHierarchicalIdct2d_1_transpose_1block_in,HierarchicalIdct2d_1_transpose_1block_outHierarchicalIdct2d_1_clipdouble_block);
	semaphorePend(sem[8], full);
	HierarchicalIdct2d_1_clip(HierarchicalIdct2d_1_transpose_1block_outHierarchicalIdct2d_1_clipdouble_block);
	semaphorePost(sem[9], empty);
}



//endCode

{
	close_Trigger_0(Trigger_0fireHierarchicalIdct2d_1_ReadBlock_1block_In,Trigger_0fireHierarchicalIdct2d_2_ReadBlock_1block_In,Trigger_0fireHierarchicalIdct2d_2_ReadBlockblock_In);
	close_HierarchicalIdct2d_2_ReadBlock(HierarchicalIdct2d_2_ReadBlockblock_outHierarchicalIdct2d_2_idct1drow_in,Trigger_0fireHierarchicalIdct2d_2_ReadBlockblock_In);
	close_HierarchicalIdct2d_2_idct1d(HierarchicalIdct2d_2_ReadBlockblock_outHierarchicalIdct2d_2_idct1drow_in,HierarchicalIdct2d_2_idct1drow_outHierarchicalIdct2d_2_transposeblock_in);
	close_HierarchicalIdct2d_2_transpose(HierarchicalIdct2d_2_idct1drow_outHierarchicalIdct2d_2_transposeblock_in,HierarchicalIdct2d_2_transposeblock_outHierarchicalIdct2d_2_ReadBlock_1block_trans,HierarchicalIdct2d_2_transposeblock_outHierarchicalIdct2d_2_clipdouble_block);
	close_HierarchicalIdct2d_2_ReadBlock_1(HierarchicalIdct2d_2_ReadBlock_1block_outHierarchicalIdct2d_2_idct1d_1row_in,HierarchicalIdct2d_2_transposeblock_outHierarchicalIdct2d_2_ReadBlock_1block_trans,Trigger_0fireHierarchicalIdct2d_2_ReadBlock_1block_In);
	close_HierarchicalIdct2d_2_idct1d_1(HierarchicalIdct2d_2_ReadBlock_1block_outHierarchicalIdct2d_2_idct1d_1row_in,HierarchicalIdct2d_2_idct1d_1row_outHierarchicalIdct2d_2_transpose_1block_in);
	close_HierarchicalIdct2d_2_transpose_1(HierarchicalIdct2d_2_idct1d_1row_outHierarchicalIdct2d_2_transpose_1block_in,HierarchicalIdct2d_2_transpose_1block_outHierarchicalIdct2d_2_clipdouble_block);
	close_HierarchicalIdct2d_2_clip(HierarchicalIdct2d_2_transpose_1block_outHierarchicalIdct2d_2_clipdouble_block,HierarchicalIdct2d_2_transposeblock_outHierarchicalIdct2d_2_clipdouble_block);
	close_HierarchicalIdct2d_1_ReadBlock_1(HierarchicalIdct2d_1_ReadBlock_1block_outHierarchicalIdct2d_1_idct1d_1row_in,Trigger_0fireHierarchicalIdct2d_1_ReadBlock_1block_In);
	close_HierarchicalIdct2d_1_idct1d_1(HierarchicalIdct2d_1_ReadBlock_1block_outHierarchicalIdct2d_1_idct1d_1row_in,HierarchicalIdct2d_1_idct1d_1row_outHierarchicalIdct2d_1_transpose_1block_in);
	close_HierarchicalIdct2d_1_transpose_1(HierarchicalIdct2d_1_idct1d_1row_outHierarchicalIdct2d_1_transpose_1block_in,HierarchicalIdct2d_1_transpose_1block_outHierarchicalIdct2d_1_clipdouble_block);
	close_HierarchicalIdct2d_1_clip(HierarchicalIdct2d_1_transpose_1block_outHierarchicalIdct2d_1_clipdouble_block);
}

}//end thread: computationThread

//Thread: communicationThread
void communicationThread()
{


//beginningCode

{
	semaphorePost(sem[7], empty);
	semaphorePost(sem[9], empty);
}


//loopCode


for(;;){
	semaphorePend(sem[1], full);
	send(C64x_1,);
	semaphorePost(sem[0], empty);
	semaphorePend(sem[3], full);
	send(C64x_1,);
	semaphorePost(sem[2], empty);
	semaphorePend(sem[5], full);
	send(C64x_1,);
	semaphorePost(sem[4], empty);
	semaphorePend(sem[7], empty);
	receive(C64x_1,);
	semaphorePost(sem[6], full);
	semaphorePend(sem[9], empty);
	receive(C64x_1,);
	semaphorePost(sem[8], full);
}



//endCode

{
}

}//end thread: communicationThread
