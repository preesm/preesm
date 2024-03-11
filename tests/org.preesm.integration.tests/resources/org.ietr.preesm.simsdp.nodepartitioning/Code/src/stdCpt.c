/*
	============================================================================
	Name        : medianCpt.c
	Author      : orenaud
	Version     : 1.1
	Copyright   : CECILL-C
	Description :
	============================================================================
*/

#include "preesm.h"

/*========================================================================

   Global Variable

   ======================================================================*/
void STDCpt(int N_SAMPLES, int SIGMA,  IN double *raw_data_real_i, IN double *raw_data_im_i, OUT double *std_R_o, OUT double *std_I_o){
    //sort list
//    double* sorted_list_R = (double*)malloc(N_SAMPLES * sizeof(double));
//    sortList(raw_data_real_i, N_SAMPLES, sorted_list_R);
//    double* sorted_list_I = (double*)malloc(N_SAMPLES * sizeof(double));
//    sortList(raw_data_im_i, N_SAMPLES, sorted_list_I);

    //average cpt
    double average_R = computeAverage(raw_data_real_i, N_SAMPLES);
    double average_I = computeAverage(raw_data_im_i, N_SAMPLES);
    printf("AVERAGE: %f\n",average_R);
    //deviation list
    double* deviation_list_R = (double*)malloc(N_SAMPLES * sizeof(double));
    stdDeviationList(raw_data_real_i, average_R, N_SAMPLES, deviation_list_R);
    double* deviation_list_I = (double*)malloc(N_SAMPLES * sizeof(double));
    stdDeviationList(raw_data_im_i, average_I, N_SAMPLES, deviation_list_I);



    //variance cpt
    double variance_R = computeVariance(deviation_list_R, N_SAMPLES);
    double variance_I = computeVariance(deviation_list_I, N_SAMPLES);
    printf("VARIANCE: %f\n",variance_R);
    //threshold cpt
    for(int i = 0; i<N_SAMPLES;i++){
        std_R_o[i]= sqrt(variance_R)*SIGMA ;
        std_I_o[i]=sqrt(variance_I)*SIGMA;
    }

    free(deviation_list_I);
    free(deviation_list_R);

}

//double MIN(double *tab,int len){
//    double num = tab[0];
//    for(int i=1;i<len;i++){
//        if(tab[i] < num){
//            num = tab[i];
//        }
//    }
//    return num;
//}

double computeAverage( double *list, int size) {
    double sum = 0.0;
    double av = 0.0;
    int i;

    for (i = 0; i < size; i++) {
        sum += list[i];
    }
    printf ("sum = %f\n",sum);
    av = sum/size;
    return av;
}
double computeVariance( double *list, int size) {
    double sum = 0.0;
    int i;

    for (i = 0; i < size; i++) {
        sum += list[i];
    }

    return sum / size;
}
void stdDeviationList(double *list, double average, int length, double *deviation_list) {
    for(int i=0;i<length;i++){
        deviation_list[i] = sqrt(pow(list[i]-average,2));
    }
}