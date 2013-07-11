/*
	============================================================================
	Name        : dump.c
	Author      : kdesnos
	Version     :
	Copyright   :
	Description :
	============================================================================
*/

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>
#include "../include/dump.h"

static FILE *ptfile;

void dumpTime(int id,long* dumpBuffer){
    dumpBuffer[id] = clock();
}

void initNbExec(int* nbExec, int nbDump){
    int i = 0;

    if((ptfile = fopen(DUMP_FILE, "a+")) == NULL )
    {
        fprintf(stderr,"ERROR: Task read cannot open yuv_file '%s'\n", DUMP_FILE);
        return;
    }

    // Go to the end:
    fseek (ptfile , 0 , SEEK_END);

    //printf(";;");
    for(i=1; i<nbDump-1; i++){
        *(nbExec+i) = 1;
        fprintf(ptfile,"%d;",i);
    }
    fprintf(ptfile,"\n");
    fflush(ptfile);
}

void writeTime(long* dumpBuffer, int nbDump, int* nbExec){
    static int stable = 0;
    int i ;
    int changed = 0;
    if(stable != 0) {
        printf("--\n");
        for(i=1;i< nbDump;i++){
            float nbEx = (float)*(nbExec+i);
            float res;
            nbEx = (nbEx != 0)? 1/nbEx : 0;
            res = ((float)dumpBuffer[i]-(float)dumpBuffer[i-1]) * nbEx;
            fprintf(ptfile,"%.2f;",res*1000 );
        }
        fprintf(ptfile,"\n");
        fflush(ptfile);
    } else {
        for(i=nbDump-1;i>=0;i--){
            int nbExecBefore;

            dumpBuffer[i] = dumpBuffer[i]-dumpBuffer[i-1];
            // We consider that all measures below 5 ms are not precise enough
            nbExecBefore = *(nbExec+i);
            if(dumpBuffer[i] < 150) {
                *(nbExec+i) = ceil(*(nbExec+i) * 1.5);
                if(*(nbExec+i) > 131072) {
                    *(nbExec+i) = 131072;
                }
            }
            if(dumpBuffer[i] < 0){
                *(nbExec+i) = 0;
            }
            if(nbExecBefore != *(nbExec+i)){
                changed |= 1;
            }
        }
        if(changed == 0) {
            stable = 1;
            //printf(";;");
            for(i=1;i<nbDump-1;i++){
                fprintf(ptfile,"%d;",*(nbExec+i));
            }
            fprintf(ptfile,"\n");
            fflush(ptfile);
        }
    }    
}