/** 
* @file /Cluster_sub0_srv_0.c/h 
* @generated by CodegenScape
* @date Sun Jan 14 10:39:48 CET 2024
*/ 

#include "Cluster_sub0_srv_0.h" 

 void srv_0Init(){
 }
 void srv_0(int cfg_0,int cfg_1,double *in_0,double *in_1,double *out_0,double *out_1){ 

// buffer declaration 

 // body 
 for(int indexMAD_Computation = 0; indexMAD_Computation<29;indexMAD_Computation++){
MADCpt(cfg_0,cfg_1,in_0 + indexMAD_Computation*2048,in_1 + indexMAD_Computation*2048,out_0 + indexMAD_Computation*2048,out_1 + indexMAD_Computation*2048); 

 }

 // free buffer  
 }
