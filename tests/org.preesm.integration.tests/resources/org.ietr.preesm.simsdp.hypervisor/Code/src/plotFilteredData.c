/*
	============================================================================
	Name        : plotRnISeries.c
	Author      : orenaud
	Version     : 1.1
	Copyright   : CECILL-C
	Description :
	============================================================================
*/

#include "plotFilteredData.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
//#include "clock.h"

/*========================================================================

   Global Variable

   ======================================================================*/
void PlotFilteredData( int SIZE, int SAMPLE_RATE, int DISPLAY, IN double *filtered_real_data_i, IN double *filtered_im_data_i){
    //DISPLAY = 0;
    if(DISPLAY==1) {
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

        plotData(tmAx, filtered_real_data_i, SIZE, "Real",REAL_FILTER_PATH);
        plotData(tmAx, filtered_im_data_i, SIZE, "Imaginary",IM_FILTER_PATH);
    }
}

void plotData(double* x, double* y, int size, const char* label,const char* path) {
    FILE* fstore;
    if((fstore = fopen(path, "w")) == NULL )
    {
        fprintf(stderr,"ERROR: Task read cannot open sort real file '%s'", path);
        exit(1);
    }

    for (int i = 0; i < size; i++) {
        fprintf(fstore, "%.6f %.6f\n", x[i] * 1e6, y[i]);
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
    fprintf(gp, "plot '%s' using 1:2 with lines lw 1 title '%s'\n",path, "Filtered data");
    //fprintf(gp, "plot '-' with lines lw 1
    //fprintf(gp, "plot '-' with lines title '%s'\n", label);
//    for (i = 0; i < size; i++) {
//        fprintf(gp, "%.6f %.6f\n", x[i] * 1e6, y[i]);
//    }
//    fprintf(gp, "e\n");
    fflush(gp);
    fprintf(gp, "exit\n");
    pclose(gp);
}