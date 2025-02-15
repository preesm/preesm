/*
	============================================================================
	Name        : dataRes.c
	Author      : orenaud
	Version     : 1.1
	Copyright   : CECILL-C
	Description :
	============================================================================
*/

#include "dataRes.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <complex.h>
#include <math.h>

//// DADA includes for this example
//#include "futils.h"
//#include "dada_def.h"
//#include "dada_hdu.h"
//#include "multilog.h"
//#include "ipcio.h"
//#include "ascii_header.h"


void DataRes(int N_BLOCKS, int N_SAMPLES, int SIZE, int HEADER_SIZE, double *filtered_real_data_i, double *filtered_im_data_i) {
    FILE *ptfile ;
    if((ptfile = fopen(OUTPUT_PATH, "a")) == NULL )
    {
        fprintf(stderr,"ERROR: Task read cannot open dada_file '%s'", OUTPUT_PATH);
        exit(1);
    }



    double complex *timeSeries = (double complex*)malloc(SIZE * sizeof(double complex));
    for (int i = 0; i < SIZE; i++) {
        timeSeries[i] = filtered_real_data_i[i] + I * filtered_im_data_i[i];
    }

    for(int i =0; i<N_BLOCKS;i++){
        double complex* dataCmplx = (double complex*)malloc(N_SAMPLES * sizeof(double complex));
        // Assign the appropriate portion of timeSeries to dataCmplx
        for(int j = 0; j<N_SAMPLES;j++){
            dataCmplx[j] = timeSeries[i * N_SAMPLES + j];
        }
        int dataBufSize = 4 * N_SAMPLES;
        unsigned short* data = (unsigned short*)malloc(dataBufSize* sizeof(unsigned short));
        // Convert Numpy floats format to offset-binary
        for (int j = 0; j < N_SAMPLES; j++) {
            int32_t val = (int32_t)dataCmplx[j];
            data[j] = (unsigned short)val - (unsigned short)(pow(2, 15));
        }
        unsigned char* dataBuf = (unsigned char*)data;
        int r= fwrite(dataBuf,sizeof(char),dataBufSize,ptfile);
        free(data);
        free(dataCmplx);
    }
    free(timeSeries);
    fclose(ptfile);


}

