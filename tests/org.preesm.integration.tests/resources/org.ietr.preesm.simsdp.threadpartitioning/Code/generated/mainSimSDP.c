#include <stdio.h> 
#include <mpi.h> 
#include "stdlib.h" 
#include "preesm_gen.h" 
//#define MPI_LOOP_SIZE 0 

int MPIStopNode = 0;
int initNode0 = 1;
int initNode1 = 1;
int initNode2 = 1;
char nodeset[3] = {"Paravance","Paranoia","Abacus",}; 

int main(int argc, char **argv) { 
// Initialize the MPI environment 
MPI_Init(NULL, NULL); 
//Allocate buffers in distributed memory 
double *out_0;
MPI_Alloc_mem(409600 * sizeof(double), MPI_INFO_NULL, &out_0); 
double *out_1;
MPI_Alloc_mem(409600 * sizeof(double), MPI_INFO_NULL, &out_1); 
double *out_2;
MPI_Alloc_mem(409600 * sizeof(double), MPI_INFO_NULL, &out_2); 
double *out_3;
MPI_Alloc_mem(231424 * sizeof(double), MPI_INFO_NULL, &out_3); 
double *out_4;
MPI_Alloc_mem(178176 * sizeof(double), MPI_INFO_NULL, &out_4); 
double *out_5;
MPI_Alloc_mem(178176 * sizeof(double), MPI_INFO_NULL, &out_5); 
double *out_6;
MPI_Alloc_mem(409600 * sizeof(double), MPI_INFO_NULL, &out_6); 
double *out_7;
MPI_Alloc_mem(409600 * sizeof(double), MPI_INFO_NULL, &out_7); 
double *out_8;
MPI_Alloc_mem(409600 * sizeof(double), MPI_INFO_NULL, &out_8); 
double *out_9;
MPI_Alloc_mem(231424 * sizeof(double), MPI_INFO_NULL, &out_9); 
double *out_0;
MPI_Alloc_mem(409600 * sizeof(double), MPI_INFO_NULL, &out_0); 
double *out_1;
MPI_Alloc_mem(409600 * sizeof(double), MPI_INFO_NULL, &out_1); 
double *out_2;
MPI_Alloc_mem(409600 * sizeof(double), MPI_INFO_NULL, &out_2); 
double *out_3;
MPI_Alloc_mem(409600 * sizeof(double), MPI_INFO_NULL, &out_3); 
// Get the name of the processor 
char processor_name[MPI_MAX_PROCESSOR_NAME]; 
int name_len; 
int rank = 0; 
MPI_Get_processor_name(processor_name, &name_len); 
for(int index = 0;index < nodeset;index++){  
if(processor_name==nodeset[index]){ 
rank = index; 
} 
 } 
for(int index=0; index< MPI_LOOP_SIZE;index++) { 
if (world_rank ==0){ 
sub_0(N_BLOCKS,N_SAMPLES,SIGMA,HEADER_SIZE,SIZE,DISPLAY,out_0,out_1,out_2,out_3,out_4,out_5,out_6,out_7,out_8,out_9MPI_Ssend(out_0,409600,double,1, MPI_COMM_WORLD);
MPI_Ssend(out_1,409600,double,1, MPI_COMM_WORLD);
MPI_Ssend(out_2,409600,double,1, MPI_COMM_WORLD);
MPI_Ssend(out_3,231424,double,1, MPI_COMM_WORLD);
MPI_Ssend(out_4,178176,double,1, MPI_COMM_WORLD);
MPI_Ssend(out_5,178176,double,1, MPI_COMM_WORLD);
MPI_Ssend(out_6,409600,double,1, MPI_COMM_WORLD);
MPI_Ssend(out_7,409600,double,1, MPI_COMM_WORLD);
MPI_Ssend(out_8,409600,double,1, MPI_COMM_WORLD);
MPI_Ssend(out_9,231424,double,1, MPI_COMM_WORLD);
initNode0 = 0;
 }
if (world_rank ==1){ 
MPI_Recv(sub_0,178176,double,0, MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
MPI_Recv(sub_0,409600,double,0, MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
MPI_Recv(sub_0,409600,double,0, MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
MPI_Recv(sub_0,409600,double,0, MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
MPI_Recv(sub_0,409600,double,0, MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
MPI_Recv(sub_0,231424,double,0, MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
MPI_Recv(sub_0,231424,double,0, MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
MPI_Recv(sub_0,178176,double,0, MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
sub_1(N_SAMPLES,N_BLOCKS,SIGMA,SIZE,SAMPLE_RATE,DISPLAY,sub_0,sub_0,sub_0,sub_0,sub_0,sub_0,sub_0,sub_0,out_0,out_1,out_2,out_3MPI_Ssend(out_0,409600,double,2, MPI_COMM_WORLD);
MPI_Ssend(out_1,409600,double,2, MPI_COMM_WORLD);
MPI_Ssend(out_2,409600,double,2, MPI_COMM_WORLD);
MPI_Ssend(out_3,409600,double,2, MPI_COMM_WORLD);
initNode1 = 0;
 }
if (world_rank ==2){ 
MPI_Recv(sub_1,409600,double,1, MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
MPI_Recv(sub_1,409600,double,1, MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
MPI_Recv(sub_1,409600,double,1, MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
MPI_Recv(sub_1,409600,double,1, MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
MPI_Recv(sub_0,409600,double,1, MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
MPI_Recv(sub_0,409600,double,1, MPI_ANY_TAG, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
sub_2(SIZE,N_BLOCKS,N_SAMPLES,HEADER_SIZE,MODE,DISPLAY,SAMPLE_RATE,sub_1,sub_1,sub_1,sub_1,sub_0,sub_0initNode2 = 0;
 }
}
MPI_Free_mem(out_0MPI_Free_mem(out_1MPI_Free_mem(out_2MPI_Free_mem(out_3MPI_Free_mem(out_4MPI_Free_mem(out_5MPI_Free_mem(out_6MPI_Free_mem(out_7MPI_Free_mem(out_8MPI_Free_mem(out_9MPI_Free_mem(out_0MPI_Free_mem(out_1MPI_Free_mem(out_2MPI_Free_mem(out_3MPI_Finalize();
