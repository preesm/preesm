/** 
* @file /Cluster_sub0_srv_0.c/h 
* @generated by CodegenScape
* @date Thu Nov 02 17:45:05 CET 2023
*/ 

#include "Cluster_sub0_srv_0.h" 

 void srv_0Init(){
 }
 void srv_0(int cfg_0,int cfg_1,double in_0,double in_1,double out_0,double out_1){ 

// buffer declaration 

 // body 
 for(int index = 0; index <29;index++){
MAD_Computation(cfg_0,cfg_1,raw_data_real_i,raw_data_im_i,mad_R_o,mad_I_o);
 }

 // free buffer  
 }
