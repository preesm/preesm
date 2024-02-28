/** 
* @file Cluster_mainSimSDP.c 
* @generated by CodegenSimSDP
* @date Wed Feb 28 15:20:35 CET 2024
*/ 

#include <stdio.h> 
#include <mpi.h> 
#include "stdlib.h" 
#include "sub0/preesm_gen0.h" 
#include "sub0/sub0.h" 
#include "sub1/preesm_gen1.h" 
#include "sub1/sub1.h" 
#include "sub2/preesm_gen2.h" 
#include "sub2/sub2.h" 
#define MPI_LOOP_SIZE 100 

int MPIStopNode = 0;
int initNode0 = 1;
int initNode1 = 1;
int initNode2 = 1;
int main(int argc, char **argv) { 
 int label = 100;
 MPI_Status status;
 // Initialize the MPI environment 
 MPI_Init(&argc, &argv); 
//Allocate buffers in distributed memory 
double *sub0_out_0__sub1_in_2=(double*)malloc(401408 * sizeof(double));
double *sub0_out_1__sub1_in_3=(double*)malloc(401408 * sizeof(double));
double *sub0_out_2__sub1_in_1=(double*)malloc(8192 * sizeof(double));
double *sub0_out_3__sub2_in_7=(double*)malloc(409600 * sizeof(double));
double *sub0_out_4__sub2_in_5=(double*)malloc(409600 * sizeof(double));
double *sub0_out_5__sub2_in_1=(double*)malloc(409600 * sizeof(double));
double *sub0_out_6__sub2_in_6=(double*)malloc(409600 * sizeof(double));
double *sub0_out_7__sub2_in_4=(double*)malloc(409600 * sizeof(double));
double *sub0_out_8__sub2_in_2=(double*)malloc(409600 * sizeof(double));
double *sub0_out_9__sub1_in_0=(double*)malloc(8192 * sizeof(double));
double *sub1_out_0__sub2_in_0=(double*)malloc(409600 * sizeof(double));
double *sub1_out_1__sub2_in_3=(double*)malloc(409600 * sizeof(double));
int mpi_rank;
MPI_Comm_rank(MPI_COMM_WORLD,&mpi_rank); 

#ifdef MPI_LOOP_SIZE // Case of a finite loop 
for(int index=0; index< MPI_LOOP_SIZE;index++) { 
#else // Default case of an infinite loop 
while(!MPIStopNode) { 
#endif 
if (rank ==0){ 
sub0(sub0_out_0__sub1_in_2,sub0_out_1__sub1_in_3,sub0_out_2__sub1_in_1,sub0_out_3__sub2_in_7,sub0_out_4__sub2_in_5,sub0_out_5__sub2_in_1,sub0_out_6__sub2_in_6,sub0_out_7__sub2_in_4,sub0_out_8__sub2_in_2,sub0_out_9__sub1_in_0);
int dest =1;
MPI_Send(sub0_out_0__sub1_in_2,401408,MPI_DOUBLE,find_rank_by_processor_name(nodeset[1]) ,label, MPI_COMM_WORLD);
MPI_Send(sub0_out_1__sub1_in_3,401408,MPI_DOUBLE,find_rank_by_processor_name(nodeset[1]) ,label, MPI_COMM_WORLD);
MPI_Send(sub0_out_2__sub1_in_1,8192,MPI_DOUBLE,find_rank_by_processor_name(nodeset[1]) ,label, MPI_COMM_WORLD);
MPI_Send(sub0_out_3__sub2_in_7,409600,MPI_DOUBLE,find_rank_by_processor_name(nodeset[2]) ,label, MPI_COMM_WORLD);
MPI_Send(sub0_out_4__sub2_in_5,409600,MPI_DOUBLE,find_rank_by_processor_name(nodeset[2]) ,label, MPI_COMM_WORLD);
MPI_Send(sub0_out_5__sub2_in_1,409600,MPI_DOUBLE,find_rank_by_processor_name(nodeset[2]) ,label, MPI_COMM_WORLD);
MPI_Send(sub0_out_6__sub2_in_6,409600,MPI_DOUBLE,find_rank_by_processor_name(nodeset[2]) ,label, MPI_COMM_WORLD);
MPI_Send(sub0_out_7__sub2_in_4,409600,MPI_DOUBLE,find_rank_by_processor_name(nodeset[2]) ,label, MPI_COMM_WORLD);
MPI_Send(sub0_out_8__sub2_in_2,409600,MPI_DOUBLE,find_rank_by_processor_name(nodeset[2]) ,label, MPI_COMM_WORLD);
MPI_Send(sub0_out_9__sub1_in_0,8192,MPI_DOUBLE,find_rank_by_processor_name(nodeset[1]) ,label, MPI_COMM_WORLD);
initNode0 = 0;
 }
if (rank ==1){ 
int src = 0;
MPI_Recv(sub0_out_0__sub1_in_2,401408,MPI_DOUBLE,find_rank_by_processor_name(nodeset[0]), label, MPI_COMM_WORLD, &status);
MPI_Recv(sub0_out_1__sub1_in_3,401408,MPI_DOUBLE,find_rank_by_processor_name(nodeset[0]), label, MPI_COMM_WORLD, &status);
MPI_Recv(sub0_out_2__sub1_in_1,8192,MPI_DOUBLE,find_rank_by_processor_name(nodeset[0]), label, MPI_COMM_WORLD, &status);
MPI_Recv(sub0_out_9__sub1_in_0,8192,MPI_DOUBLE,find_rank_by_processor_name(nodeset[0]), label, MPI_COMM_WORLD, &status);
sub1(sub0_out_9__sub1_in_0,sub0_out_2__sub1_in_1,sub0_out_0__sub1_in_2,sub0_out_1__sub1_in_3,sub1_out_0__sub2_in_0,sub1_out_1__sub2_in_3);
int dest =2;
MPI_Send(sub1_out_0__sub2_in_0,409600,MPI_DOUBLE,find_rank_by_processor_name(nodeset[2]) ,label, MPI_COMM_WORLD);
MPI_Send(sub1_out_1__sub2_in_3,409600,MPI_DOUBLE,find_rank_by_processor_name(nodeset[2]) ,label, MPI_COMM_WORLD);
initNode1 = 0;
 }
if (rank ==2){ 
int src = 1;
MPI_Recv(sub1_out_0__sub2_in_0,409600,MPI_DOUBLE,find_rank_by_processor_name(nodeset[1]), label, MPI_COMM_WORLD, &status);
MPI_Recv(sub1_out_1__sub2_in_3,409600,MPI_DOUBLE,find_rank_by_processor_name(nodeset[1]), label, MPI_COMM_WORLD, &status);
MPI_Recv(sub0_out_3__sub2_in_7,409600,MPI_DOUBLE,find_rank_by_processor_name(nodeset[0]), label, MPI_COMM_WORLD, &status);
MPI_Recv(sub0_out_4__sub2_in_5,409600,MPI_DOUBLE,find_rank_by_processor_name(nodeset[0]), label, MPI_COMM_WORLD, &status);
MPI_Recv(sub0_out_5__sub2_in_1,409600,MPI_DOUBLE,find_rank_by_processor_name(nodeset[0]), label, MPI_COMM_WORLD, &status);
MPI_Recv(sub0_out_6__sub2_in_6,409600,MPI_DOUBLE,find_rank_by_processor_name(nodeset[0]), label, MPI_COMM_WORLD, &status);
MPI_Recv(sub0_out_7__sub2_in_4,409600,MPI_DOUBLE,find_rank_by_processor_name(nodeset[0]), label, MPI_COMM_WORLD, &status);
MPI_Recv(sub0_out_8__sub2_in_2,409600,MPI_DOUBLE,find_rank_by_processor_name(nodeset[0]), label, MPI_COMM_WORLD, &status);
sub2(sub1_out_0__sub2_in_0,sub0_out_5__sub2_in_1,sub0_out_8__sub2_in_2,sub1_out_1__sub2_in_3,sub0_out_7__sub2_in_4,sub0_out_4__sub2_in_5,sub0_out_6__sub2_in_6,sub0_out_3__sub2_in_7);
initNode2 = 0;
 }
}
free(sub0_out_0__sub1_in_2);
free(sub0_out_1__sub1_in_3);
free(sub0_out_2__sub1_in_1);
free(sub0_out_3__sub2_in_7);
free(sub0_out_4__sub2_in_5);
free(sub0_out_5__sub2_in_1);
free(sub0_out_6__sub2_in_6);
free(sub0_out_7__sub2_in_4);
free(sub0_out_8__sub2_in_2);
free(sub0_out_9__sub1_in_0);
free(sub1_out_0__sub2_in_0);
free(sub1_out_1__sub2_in_3);
 MPI_Finalize();
  return 0;
 }