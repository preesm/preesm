/*
	============================================================================
	Name        : dataAcq.c
	Author      : orenaud
	Version     : 1.1
	Copyright   : CECILL-C
	Description :
	============================================================================
*/

#include "dataAcq.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>


// DADA includes for this example
#include "futils.h"
#include "dada_def.h"
#include "dada_hdu.h"
#include "multilog.h"
#include "ipcio.h"
#include "ascii_header.h"

/*#define SAMPLE_RATE 128e6
#define N_SAMPLES 2048
#define N_BLOCKS 200

#define SIZE N_BLOCKS*N_SAMPLES

#define nBits 8
#define windowLen 201
#define windowMid (windowLen / 2)
#define K 1.4826 //refer to wikipedia
#define SIGMA 3;*/

/*========================================================================

   Global Variable

   ======================================================================*/



/*========================================================================

   initReadYUV DEFINITION

   ======================================================================*/
//void initDataAcq() {
//    int fsize;
//
//
//
//#ifdef PREESM_VERBOSE
//    printf("Opened file '%s'\n", PATH);
//#endif
//
//
//    //printf(" end init here we are\n");
//}




/*========================================================================

   Read the DADA file.
   The file is composed of 2 part
   - the header part:
    The file header is 4096 bytes of ASCII describing the rest of the binary data in the file
   - the raw data part:
    The raw data is 16-bits complex-valued, single polarisation, offset binary encoding
    Alternating real/imaginary values

   ======================================================================*/
void DataAcq(int N_BLOCKS, int N_SAMPLES, int SIZE, int HEADER_SIZE, double *raw_data_real_o, double *raw_data_im_o) {
    FILE *ptfile ;
    if((ptfile = fopen(INPUT_PATH, "rb")) == NULL )
    {
        fprintf(stderr,"ERROR: Task read cannot open dada_file '%s'", INPUT_PATH);
        exit(1);
    }
    fseek (ptfile , 0 , SEEK_END);
    int fsize = ftell (ptfile);
    rewind (ptfile);
    char* header= (char *) malloc(sizeof(char) * HEADER_SIZE);//4096=header size
    int h= fread(header,sizeof(char),HEADER_SIZE,ptfile);
    printf("HEADER INFO:\n");
    for(int i = 0; i<HEADER_SIZE;i++){
        printf("%c",header[i]);
        if(header[i]=='\n'){
            printf("\n");
        }
    }
    headerCopy(HEADER_SIZE, header);

    double complex *timeSeries = (double complex*)malloc(SIZE * sizeof(double complex));
    int dataBufSize = 4 * N_SAMPLES;
    for (int i = 0; i < N_BLOCKS; i++) {
        unsigned char* dataBuf = (unsigned char*)malloc(dataBufSize * sizeof(unsigned char));
        // Read dataBufSize bytes from file handler `fh` and store it in `dataBuf`
        int r= fread(dataBuf,sizeof(char),dataBufSize,ptfile);
        //unsigned short* data = (unsigned short*)dataBuf;
        //printf("Databuf[0]: %c\n",dataBuf[0]);
        uint16_t* data = (uint16_t*)dataBuf;
        //printf("data[0]: %c\n",data[0]);
        double complex* dataCmplx = (double complex*)malloc(N_SAMPLES * sizeof(double complex));

        // Convert offset-binary format to Numpy floats
        for (int j = 0; j < N_SAMPLES; j++) {
            int32_t val = (int32_t)data[j] - (1 << 15);//(int32_t)(pow(2, 15));

            //uint16_t array1 = (int16_t)(val & 0xFFFF);              // Extract the lower 16 bits
            //uint16_t array2 = (int16_t)((val >> 16) & 0xFFFF);
            //dataCmplx[j] = (double)array1+I*(double)array2;
            dataCmplx[j] = (double)((int32_t)data[2*j]- (1 << 15))+ (double)((int32_t)data[2*j+1]- (1 << 15))*I;
            int i =0;
        }
//        for(int j=0;j<N_SAMPLES;j++){
//            dataCmplx[j] = (double)data[j]+(double)data[j+1]*I;
//        }

        // Assign dataCmplx to the appropriate portion of timeSeries
        for (int j = 0; j < N_SAMPLES; j++) {
            timeSeries[i * N_SAMPLES + j] = dataCmplx[j];
        }
        free(dataBuf);
        free(dataCmplx);
    }
    fclose(ptfile);

    // Extract real and imaginary parts of timeSeries
//    double* realPart = (double*)malloc(SIZE * sizeof(double));
//    double* imagPart = (double*)malloc(SIZE * sizeof(double));

    for (int i = 0; i < SIZE; i++) {
        raw_data_real_o[i] = creal(timeSeries[i]);
        raw_data_im_o[i] = cimag(timeSeries[i]);
    }

    //To remove on true data
    double *xtRFI_R = (double*)malloc(SIZE * sizeof(double));
    generateRFI(SIZE,8,raw_data_real_o,xtRFI_R);
    raw_data_real_o = xtRFI_R;
    double *xtRFI_I = (double*)malloc(SIZE * sizeof(double));
    generateRFI(SIZE,8,raw_data_im_o,xtRFI_I);
    raw_data_im_o = xtRFI_I;


    free(timeSeries);
    free(xtRFI_R);
}

void headerCopy(int size, char *header) {
    FILE* fstore;
    if((fstore = fopen(OUTPUT_PATH, "w")) == NULL )
    {
        fprintf(stderr,"ERROR: Task read cannot open data file '%s'", OUTPUT_PATH);
        exit(1);
    }
    for(int i=0;i<size;i++){
        fprintf(fstore, "%c",header[i]);
    }
    fclose(fstore);
}
/*void DataAcq2(unsigned char *raw_data_o) {

//    if( ftell(ptfile)/(width*height + width*height/2) >=NB_FRAME){
//    	unsigned int time = 0;
//        rewind(ptfile);
//        time = stopTiming(0);
//        printf("\nMain: %d frames in %d us - %f fps\n", NB_FRAME-1 ,time, (NB_FRAME-1.0)/(float)time*1000000);
//        startTiming(0);
//    }
printf("Data Acq here we are");
    if((ptfile = fopen(PATH2, "rb")) == NULL )
    {
        fprintf(stderr,"ERROR: Task read cannot open yuv_file '%s'", PATH2);
        exit(1);
    }
    //Read the file header
// Obtain file size:
    fseek (ptfile , 0 , SEEK_END);
    int fsize = ftell (ptfile);
    rewind (ptfile);
    char* header= (char *) malloc(sizeof(char) * 4096);//4096=header size
    int h= fread(header,sizeof(char),4096,ptfile);
    printf("HEADER INFO:\n");
    for(int i = 0; i<4096;i++){
        printf("%c",header[i]);
        if(header[i]=='\n'){
            printf("\n");
        }
    }

// read in the raw data
//test

int rawSize = fsize - 4096;
//    char* rawDataTemp= (char *) malloc(sizeof(char) * rawSize);
//    int r= fread(rawDataTemp,sizeof(char),rawSize,ptfile);
//    printf("RAW DATA:\n");
//    for(int i = 0; i<4096;i++){
//        printf("%c",rawDataTemp[i]);
//        if(rawDataTemp[i]=='\n'){
//            printf("\n");
//        }
//    }
    double fs = SAMPLE_RATE;
    int fc;
    int nSamples = N_SAMPLES;
    int nBlocks = N_BLOCKS;
    int size = SIZE;// * sizeof(double complex);
    int k = sizeof(double complex);
    int k2 = sizeof(char);
    double complex *timeSeries = (double complex*)malloc(size * sizeof(double complex));
    int dataBufSize = 4 * nSamples;
    for (int i = 0; i < nBlocks; i++) {
        unsigned char* dataBuf = (unsigned char*)malloc(dataBufSize * sizeof(unsigned char));
        // Read dataBufSize bytes from file handler `fh` and store it in `dataBuf`
        int r= fread(dataBuf,sizeof(char),dataBufSize,ptfile);

        unsigned short* data = (unsigned short*)dataBuf;
        int j;
        double complex* dataCmplx = (double complex*)malloc(nSamples * sizeof(double complex));

        // Convert offset-binary format to Numpy floats
        for (j = 0; j < nSamples; j++) {
            int32_t val = (int32_t)data[j] - (int32_t)(pow(2, 15));
            dataCmplx[j] = (double)val;
        }

        // Assign dataCmplx to the appropriate portion of timeSeries
        for (j = 0; j < nSamples; j++) {
            timeSeries[i * nSamples + j] = dataCmplx[j];
        }

        free(dataBuf);
        free(dataCmplx);
    }
    fclose(ptfile);
    *//*========================================================================

   Plot the real and imaginary time-series

   ======================================================================*//*

    double Ts;
    double* tmAx;
    // Calculate Ts and allocate memory for tmAx
    Ts = 1.0 / fs;
    tmAx = (double*)malloc(size * sizeof(double));

    // Calculate tmAx
    for (int i = 0; i < size; i++) {
        tmAx[i] = i * Ts;
    }

    // Extract real and imaginary parts of timeSeries
    double* realPart = (double*)malloc(size * sizeof(double));
    double* imagPart = (double*)malloc(size * sizeof(double));

    for (int i = 0; i < size; i++) {
        realPart[i] = creal(timeSeries[i]);
        imagPart[i] = cimag(timeSeries[i]);
    }

    // Plot real and imaginary parts using the plotData function
    //plotData(tmAx, realPart, N, "Real");
    //plotData(tmAx, imagPart, N, "Imaginary");


    // store data
    storeRealData(realPart, size);
    //double median_absolute_deviation[N_BLOCKS] = {0};
    double* median_absolute_deviation = (double*)malloc(N_BLOCKS * sizeof(double));
    double* threshold = (double*)malloc(SIZE * sizeof(double));//faut que ce soit size
    double* filtered_real_data = (double*)malloc(SIZE * sizeof(double));
    //double threshold[N_BLOCKS] = {0};
    //double filtered_real_data[SIZE];
    for(int i =0; i<nBlocks;i++) {
        //sort list
        double* sorted_list = (double*)malloc(N_SAMPLES * sizeof(double));
        sortList(realPart+i*N_SAMPLES, N_SAMPLES, sorted_list);
        //median cpt
        double median = computeMedian(sorted_list, N_SAMPLES);
        //deviation list
        double* deviation_list = (double*)malloc(N_SAMPLES * sizeof(double));
        deviationList(sorted_list, median, N_SAMPLES, deviation_list);
        //sort list a second time
        double* sorted_deviated_list = (double*)malloc(N_SAMPLES * sizeof(double));
        sortList(deviation_list, N_SAMPLES, sorted_deviated_list);
        //median cpt a second time
        median_absolute_deviation[i] = computeMedian(sorted_deviated_list, N_SAMPLES)*K;
        //threshold cpt
        for(int j = 0; j<N_SAMPLES;j++)
            threshold[j+i*N_SAMPLES]=median_absolute_deviation[i]*SIGMA;
        //filter
        madFilter(realPart+i*N_SAMPLES,N_SAMPLES,threshold[i*N_SAMPLES],filtered_real_data+i*N_SAMPLES);
        free(sorted_list);
        free(deviation_list);

    }
    // you can display something as you want
    plotThreshold(tmAx, threshold, realPart, size, "Real threshold");
    free(median_absolute_deviation);
    free(threshold);
    free(filtered_real_data);




    *//*========================================================================

Plot the histogram of the real and imaginary components

======================================================================*//*
    //plotHistogram(realPart, N, "Real component");
    //plotHistogram(imagPart, N, "Imaginary component");



    free(tmAx);
    free(realPart);
    free(imagPart);
    free(timeSeries);
    free(header);
}*/

//void madFilter(double *raw_data, int length, double threshold, double *filtered_data) {
//    for(int i = 0; i < length;i++){
//        if(raw_data[i]<0 && raw_data[i]<-threshold){
//            filtered_data[i] = -threshold;
//        }else if(raw_data[i]>0 && raw_data[i]>threshold){
//            filtered_data[i] = threshold;
//        }else{
//            filtered_data[i] = raw_data[i];
//        }
//    }
//
//}

//void deviationList(double *list, double median, int length, double *deviation_list) {
//    for(int i=0;i<length;i++){
//        deviation_list[i] = abs(list[i]-median);
//    }
//}
//
//double computeMedian(double *list, int length) {
//    if (length % 2 == 0) {
//        // If the length is even, average the middle two elements
//        int midIndex1 = length / 2 - 1;
//        int midIndex2 = length / 2;
//        float median = (list[midIndex1] + list[midIndex2]) / 2.0;
//        return median;
//    } else {
//        // If the length is odd, simply return the middle element
//        int midIndex = length / 2;
//        float median = list[midIndex];
//        return median;
//    }
//}

//void gaussian(){
//
//    int N_rounded = (int)ceil((double)N_SAMPLES / windowLen) * windowLen;
//    printf("Time-series length = %d\n", N_rounded);
//
//    double* rndSeq = (double*)malloc(N_rounded * sizeof(double));
//    double sigScale = 0.2;
//
//    for (int i = 0; i < N_rounded; i++) {
//        rndSeq[i] = (double)rand() / RAND_MAX * 2.0 - 1.0;  // Generate random numbers between -1 and 1
//    }
//
//    double maxVal = 0.0;
//    for (int i = 0; i < N_rounded; i++) {
//        if (fabs(rndSeq[i]) > maxVal) {
//            maxVal = fabs(rndSeq[i]);
//        }
//    }
//
//    double scl = sigScale / maxVal;
//    int* xt = (int*)malloc(N_rounded * sizeof(int));
//    for (int i = 0; i < N_rounded; i++) {
//       // xt[i] = (int)floor(pow(2, nBits - 1) * scl * rndSeq[i]);
//    }
//
//    // Print the values of xt
//    for (int i = 0; i < N_rounded; i++) {
//        printf("%d ", xt[i]);
//    }
//    printf("\n");
//
//    free(rndSeq);
//    free(xt);
//}

//void sortList(double *data, int n, double *sorted_list) {
//
//    int count[SIZE] = {0};
//
//    int min_value = -50;
//    int max_value = 50;
//// Count the occurrence of each number
//    for (int i = 0; i < n; i++) {
//        int num = data[i];
//        count[num - min_value]++;
//    }
//    // Generate the sorted list
//    int index = 0;
//    for (int i = 0; i < n; i++) {
//        for (int j = 0; j < count[i]; j++) {
//            sorted_list[index++] = i + min_value;
//        }
//    }
//// STORE the sorted list
//    FILE* fstore;
//    if((fstore = fopen(SORTREALPATH, "w")) == NULL )
//    {
//        fprintf(stderr,"ERROR: Task read cannot open sort real file '%s'", SORTREALPATH);
//        exit(1);
//    }
//   // printf("\n\nCount of each number:\n");
//    for (int i = 0; i < SIZE; i++) {
//        if (count[i] > 0) {
//            fprintf(fstore,"%d: %d\n", i + min_value, count[i]);
//        }
//    }
//    fclose(fstore);
//}

//void storeRealData(double *data,int N) {
//    FILE* fstore;
//    if((fstore = fopen(REALPATH, "w")) == NULL )
//    {
//        fprintf(stderr,"ERROR: Task read cannot open real file '%s'", REALPATH);
//        exit(1);
//    }
//    //fopen(REALPATH, "w");
//    for(int i=0;i<N;i++){
//        fprintf(fstore, "%f \n",data[i]);
//    }
//    fclose(fstore);
//}

//void plotData(double* x, double* y, int size, const char* label) {
//    FILE* gp;
//    int i;
//    gp = popen("gnuplot -persist", "w");
//    fprintf(gp, "set xlabel 'Time (uS)'\n");
//    fprintf(gp, "set ylabel 'Amplitude (lin.)'\n");
//    fprintf(gp, "set grid\n");
//    fprintf(gp, "set title '%s'\n", label);
//    fprintf(gp, "plot '-' with lines title '%s'\n", label);
//    for (i = 0; i < size; i++) {
//        fprintf(gp, "%.6f %.6f\n", x[i] * 1e6, y[i]);
//    }
//    fprintf(gp, "e\n");
//    fflush(gp);
//    fprintf(gp, "exit\n");
//    pclose(gp);
//}

//void plotThreshold(double* x, double* threshold, double* raw_data , int size, const char* label) {
//    FILE* gp;
//    int i;
//    gp = popen("gnuplot -persist", "w");
//    fprintf(gp, "set xlabel 'Time (uS)'\n");
//    fprintf(gp, "set ylabel 'Amplitude (lin.)'\n");
//    fprintf(gp, "set grid\n");
//    fprintf(gp, "set title '%s'\n", label);
//    fprintf(gp, "plot '-' with lines lw 1 title '%s', \ '-' with lines lw 2 title '%s', \ '-' with lines lw 2 title '%s' \n","Raw data", "Pos - MAD Threshold", "Neg - MAD Threshold");
//
//    // print raw data
//    for (i = 0; i < size; i++) {
//        fprintf(gp, "%.6f %.6f\n", x[i] * 1e6, raw_data[i]);
//    }
//    fprintf(gp, "e\n");
//    // print positive threshold
//    for (i = 0; i < size; i++) {
//        fprintf(gp, "%.6f %.6f\n", x[i] * 1e6, threshold[i]);
//    }
//    fprintf(gp, "e\n");
//    // print negative threshold
//    for (i = 0; i < size; i++) {
//        fprintf(gp, "%.6f %.6f\n", x[i] * 1e6, -threshold[i]);
//    }
//    fprintf(gp, "e\n");
//
//
//    fflush(gp);
//    fprintf(gp, "exit\n");
//    pclose(gp);
//}
/*
void plotHistogram(double* y, int size, const char* label) {
    FILE* gp;
    gp = popen("gnuplot -persist", "w");
    fprintf(gp, "set style data histogram\n");
    fprintf(gp, "set style fill solid\n");
    fprintf(gp, "set style histogram cluster gap 1\n");
    fprintf(gp, "set xtics rotate by -75 offset -0.8,-0.5\n");
    fprintf(gp, "plot '/home/orenaud/preesm2/RFI/Code/dat/sort_real.data' using 2:xtic(1) title 'Real component' with histogram\n");

    //fprintf(gp, "e\n");
    fflush(gp);
    fprintf(gp, "exit\n");
    pclose(gp);
}*/
void generateRFI(int size, int nBits, double *xt, double *xtRFI) {
    int nOutliers = 50;
    float rfiScale =3.8;
    int i;
    int outlierIdx[nOutliers];

    for (i = 0; i < nOutliers; i++) {
        outlierIdx[i] = (int)((double)size * rand() / (RAND_MAX + 1.0));
    }
    xtRFI = xt;
    for (i = 0; i < nOutliers; i++) {
        xtRFI[outlierIdx[i]] = rfiScale * ((1 << (nBits - 1)) - 1) * (2 * (double)rand() / (RAND_MAX + 1.0) - 1);
    }
}