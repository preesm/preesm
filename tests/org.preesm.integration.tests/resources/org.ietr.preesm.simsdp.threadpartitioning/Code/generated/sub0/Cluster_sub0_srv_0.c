/**
* @file /Cluster_sub0_srv_0.c/h
* @generated by CodegenScape
* @date Mon Oct 07 15:30:16 CEST 2024
*/

#include "Cluster_sub0_srv_0.h"

static int cfg_0 = 2048;
static int cfg_1 = 3;
 void Cluster_sub0_srv_0Init(){
}
void Cluster_sub0_srv_0(int cfg_0,int cfg_1,double *MAD_Computation_raw_data_real_i,double *MAD_Computation_raw_data_im_i,double *MAD_Computation_mad_R_o,double *MAD_Computation_mad_I_o){

// buffer declaration

// body 
for(int indexMAD_Computation = 0; indexMAD_Computation<29;indexMAD_Computation++){
MADCpt(N_SAMPLES,SIGMA,MAD_Computation_raw_data_real_i + indexMAD_Computation*2048,MAD_Computation_raw_data_im_i + indexMAD_Computation*2048,MAD_Computation_mad_R_o + indexMAD_Computation*2048,MAD_Computation_mad_I_o + indexMAD_Computation*2048);

 }

// free buffer
}
