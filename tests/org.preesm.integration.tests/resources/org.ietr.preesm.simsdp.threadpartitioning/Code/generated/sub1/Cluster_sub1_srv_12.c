/** 
* @file /Cluster_sub1_srv_12.c/h 
* @generated by CodegenScape
* @date Mon Nov 06 09:06:47 CET 2023
*/ 

#include "Cluster_sub1_srv_12.h" 

 void srv_12Init(){
 }
 void srv_12(int cfg_0,int cfg_1,double in_0,double out_0,double out_1){ 

// buffer declaration 

 // body 
 for(int index = 0; index <66;index++){
Brd_MAD_I(cfg_0,cfg_1,in,out_0,out_1);
 }

 // free buffer  
 }