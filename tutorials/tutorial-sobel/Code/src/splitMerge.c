/*
============================================================================
Name        : splitMerge.c
Author      : kdesnos
Version     : 1.0
Copyright   : CECILL-C
Description :
============================================================================
*/

#include <string.h>
#include <stdlib.h>

#include "splitMerge.h"

void split(int nbSlice, int xSize, int ySize, unsigned char *input, unsigned char *output){
    int i,j;
    int sliceSize =  xSize*ySize/nbSlice;
    // Fill first and last line with 0
    memset(output,0,xSize);
    // First Slice
    memcpy(output+xSize, input, sliceSize);
    // Copy next line if several slice
    if (nbSlice > 1){
        memcpy(output +  xSize + sliceSize , input + sliceSize, xSize);
    }
    // Slice other than first and last
    for(i=1; i<nbSlice-1; i++){
        int destIndex = i*(sliceSize+2*xSize);
        memcpy(output + destIndex, input+i*sliceSize-xSize, sliceSize+2*xSize);
    }
    // Last Slice
    i = nbSlice-1;
    if(nbSlice > 1){
        // we have i = nbSlice -1;
        int destIndex = i*(sliceSize+2*xSize);
        memcpy(output + destIndex, input+i*sliceSize-xSize, sliceSize+xSize);
    }
    // Last line
    memset(output + (ySize+nbSlice*2-1)*xSize,0,xSize);
}


void merge(int nbSlice, int xSize, int ySize, unsigned char *input, unsigned char *output){
    int i;
    int sliceSize =  xSize*ySize/nbSlice;
    // Copy the slice content except the first and last lines
    for(i = 0; i< nbSlice; i++){
        int idx = i*(sliceSize+2*xSize);
        memcpy(output+i*sliceSize, input+idx+xSize, sliceSize);
    }
}

