/*
	============================================================================
	Name        : medianCpt.c
	Author      : orenaud
	Version     : 1.1
	Copyright   : CECILL-C
	Description :
	============================================================================
*/

#include "madCpt.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
//#include "clock.h"

/*========================================================================

   Global Variable

   ======================================================================*/
void MADCpt(int N_SAMPLES, int SIGMA,  IN double *raw_data_real_i, IN double *raw_data_im_i, OUT double *mad_R_o, OUT double *mad_I_o){
    //sort list
    double* sorted_list_R = (double*)malloc(N_SAMPLES * sizeof(double));
    sortList(raw_data_real_i, N_SAMPLES, sorted_list_R);
    double* sorted_list_I = (double*)malloc(N_SAMPLES * sizeof(double));
    sortList(raw_data_im_i, N_SAMPLES, sorted_list_I);

    //median cpt
    double median_R = computeMedian(sorted_list_R, N_SAMPLES);
    double median_I = computeMedian(sorted_list_I, N_SAMPLES);

    //deviation list
    double* deviation_list_R = (double*)malloc(N_SAMPLES * sizeof(double));
    deviationList(sorted_list_R, median_R, N_SAMPLES, deviation_list_R);
    double* deviation_list_I = (double*)malloc(N_SAMPLES * sizeof(double));
    deviationList(sorted_list_I, median_I, N_SAMPLES, deviation_list_I);

    //sort list a second time
    double* sorted_deviated_list_R = (double*)malloc(N_SAMPLES * sizeof(double));
    sortList(deviation_list_R, N_SAMPLES, sorted_deviated_list_R);
    double* sorted_deviated_list_I = (double*)malloc(N_SAMPLES * sizeof(double));
    sortList(deviation_list_I, N_SAMPLES, sorted_deviated_list_I);

    //median cpt a second time
    double median_absolute_deviation_R = computeMedian(sorted_deviated_list_R, N_SAMPLES)*K;
    double median_absolute_deviation_I = computeMedian(sorted_deviated_list_I, N_SAMPLES)*K;

    //threshold cpt
    for(int i = 0; i<N_SAMPLES;i++){
        mad_R_o[i]=median_absolute_deviation_R*SIGMA;
        mad_I_o[i]=median_absolute_deviation_I*SIGMA;
    }
    free(sorted_list_I);
    free(sorted_list_R);
    free(deviation_list_I);
    free(deviation_list_R);
    free(sorted_deviated_list_I);
    free(sorted_deviated_list_R);

}
void sortList(double *data, int size, double *sorted_list) {
    //int* count = (int*)malloc(size * sizeof(int));
    int count[409600] = {0};

    int min_value = MIN(data,size);
    int max_value = 50;
// Count the occurrence of each number
    for (int i = 0; i < size; i++) {
        int num = data[i];
        count[num - min_value]++;
    }
    // Generate the sorted list
    int index = 0;
    for (int i = 0; i < size; i++) {
        for (int j = 0; j < count[i]; j++) {
            sorted_list[index++] = i + min_value;
        }
    }
    //free(count);
/*// STORE the sorted list
    FILE* fstore;
    if((fstore = fopen(SORTREALPATH, "w")) == NULL )
    {
        fprintf(stderr,"ERROR: Task read cannot open sort real file '%s'", SORTREALPATH);
        exit(1);
    }
    // printf("\n\nCount of each number:\n");
    for (int i = 0; i < SIZE; i++) {
        if (count[i] > 0) {
            fprintf(fstore,"%d: %d\n", i + min_value, count[i]);
        }
    }
    fclose(fstore);*/
}
double MIN(double *tab,int len){
    double num = tab[0];
    for(int i=1;i<len;i++){
        if(tab[i] < num){
            num = tab[i];
        }
    }
    return num;
}

double computeMedian(double *list, int length) {
    if (length % 2 == 0) {
        // If the length is even, average the middle two elements
        int midIndex1 = length / 2 - 1;
        int midIndex2 = length / 2;
        float median = (list[midIndex1] + list[midIndex2]) / 2.0;
        return median;
    } else {
        // If the length is odd, simply return the middle element
        int midIndex = length / 2;
        float median = list[midIndex];
        return median;
    }
}

void deviationList(double *list, double median, int length, double *deviation_list) {
    for(int i=0;i<length;i++){
        deviation_list[i] = abs(list[i]-median);
    }
}