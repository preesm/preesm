/*
	============================================================================
	Name        : plotRnISeries.c
	Author      : orenaud
	Version     : 1.1
	Copyright   : CECILL-C
	Description :
	============================================================================
*/

#include "plotThreshold.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
//#include "clock.h"

/*========================================================================

   Global Variable

   ======================================================================*/
void PlotThreshold(int SAMPLE_RATE,int SIZE, int DISPLAY, IN double *raw_data_real_i, IN double *raw_data_im_i, IN double *mad_R_i, IN double *mad_I_i, IN double *std_R_i, IN double *std_I_i){
    if(DISPLAY==1){
        double fs = SAMPLE_RATE;
        double Ts;
        double* tmAx;
        // Calculate Ts and allocate memory for tmAx
        Ts = 1.0 / fs;
        tmAx = (double*)malloc(SIZE * sizeof(double));

        // Calculate tmAx
        for (int i = 0; i < SIZE; i++) {
            tmAx[i] = i * Ts;
        }

        plotThreshold(tmAx, mad_R_i,std_R_i , raw_data_real_i, SIZE, "Real threshold",REAL_THRESHOLD_PATH);
        plotThreshold(tmAx, mad_I_i,std_I_i, raw_data_im_i, SIZE, " Imaginary threshold",IM_THRESHOLD_PATH);
        //plotThreshold(tmAx, std_R_i, raw_data_real_i, SIZE, "STD Real threshold");
        //plotThreshold(tmAx, std_I_i, raw_data_real_i, SIZE, "STD Imaginary threshold");
    }
}
void plotThreshold(double* x, double* MADthreshold, double* STDthreshold, double* raw_data , int size, const char* label,const char* path) {

    FILE* fstore;
    if((fstore = fopen(path, "w")) == NULL )
    {
        fprintf(stderr,"ERROR: Task read cannot open sort real file '%s'", path);
        exit(1);
    }

    for (int i = 0; i < size; i++) {
        fprintf(fstore, "%.6f %.6f %.6f %.6f %.6f %.6f\n", x[i] * 1e6, raw_data[i], MADthreshold[i],-MADthreshold[i], STDthreshold[i],-STDthreshold[i]);
    }
    fclose(fstore);



    //-----------------------------------------------
   FILE* gp;
    int i;
    gp = popen("gnuplot -persist", "w");
    fprintf(gp, "set xlabel 'Time (uS)'\n");
    fprintf(gp, "set ylabel 'Amplitude (lin.)'\n");
    fprintf(gp, "set grid\n");
    fprintf(gp, "set title '%s'\n", label);
    fprintf(gp, "plot '%s' using 1:2 with lines lw 1 title '%s','%s' using 1:3 with lines lw 2 title '%s','%s' using 1:4 with lines lw 2 title '%s','%s' using 1:5 with lines lw 2 title '%s','%s' using 1:6 with lines lw 2 title '%s' \n",path, "Raw data",path, "Pos - MADThreshold",path, "Neg - MADThreshold",path, "Pos - STDThreshold",path, "Neg - STDThreshold");
    //fprintf(gp, "plot '-' with lines lw 1 title '%s', \ '-' with lines lw 2 title '%s', \ '-' with lines lw 2 title '%s' \n","Raw data", "Pos - Threshold", "Neg - Threshold");
/*
    // print raw data
    for (i = 0; i < size; i++) {
        fprintf(gp, "%.6f %.6f\n", x[i] * 1e6, raw_data[i]);
    }
    fprintf(gp, "e\n");
    // print positive threshold
    for (i = 0; i < size; i++) {
        fprintf(gp, "%.6f %.6f\n", x[i] * 1e6, threshold[i]);
    }
    fprintf(gp, "e\n");
    // print negative threshold
    for (i = 0; i < size; i++) {
        fprintf(gp, "%.6f %.6f\n", x[i] * 1e6, -threshold[i]);
    }
    */
    //printf("end");
    //fprintf(gp, "e\n");
    fflush(gp);
    fprintf(gp, "exit\n");
    pclose(gp);
   // printf("close");
}