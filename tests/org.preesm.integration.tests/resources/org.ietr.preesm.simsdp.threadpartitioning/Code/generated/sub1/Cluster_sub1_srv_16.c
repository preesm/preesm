/** 
* @file /Cluster_sub1_srv_16.c/h 
* @generated by CodegenScape
* @date Thu Nov 02 18:04:19 CET 2023
*/ 

#include "Cluster_sub1_srv_16.h" 

 void srv_16Init(){
 }
 void srv_16(int cfg_0,int cfg_1,double in_0,double out_0,double out_1){ 

// buffer declaration 

 // body 
 for(int index = 0; index <66;index++){
Brd_MAD_R(cfg_0,cfg_1,in,out_0,out_2);
 }

 // free buffer  
 }
