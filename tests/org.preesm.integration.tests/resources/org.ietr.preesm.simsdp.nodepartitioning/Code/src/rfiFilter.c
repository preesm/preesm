/*
	============================================================================
	Name        : plotRnISeries.c
	Author      : orenaud
	Version     : 1.1
	Copyright   : CECILL-C
	Description :
	============================================================================
*/

#include "rfiFilter.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
//#include "clock.h"

/*========================================================================

   Global Variable

   ======================================================================*/
void RFIFilter(int MODE, int N_SAMPLES,IN double *mad_R_i, IN double *mad_I_i, IN double *std_R_i, IN double *std_I_i, IN double *raw_data_real_i, IN double *raw_data_im_i, OUT double *filtered_real_data_o, OUT double *filtered_im_data_o){
    if(MODE==0){
        double av_threshold = (mad_I_i[0]+ mad_R_i[0])/2;
        //double av_threshold =mad_R_i[0];
        madFilter(raw_data_real_i,N_SAMPLES,av_threshold,filtered_real_data_o);
        madFilter(raw_data_im_i,N_SAMPLES,av_threshold,filtered_im_data_o);
    }else if(MODE==1){
        //TODO
    }
}
void madFilter(double *raw_data, int length, double threshold, double *filtered_data) {
    for(int i = 0; i < length;i++){
        if(raw_data[i]<0 && raw_data[i]<-threshold){
            filtered_data[i] = -threshold;
        }else if(raw_data[i]>0 && raw_data[i]>threshold){
            filtered_data[i] = threshold;
        }else{
            filtered_data[i] = raw_data[i];
        }
    }
}
void stdFilter(double *raw_data, int length, double threshold, double *filtered_data) {
    //TODO
}