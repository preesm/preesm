/*
============================================================================
Name        : splitMerge.c
Author      : kdesnos
Version     : 1.1
Copyright   : CECILL-C
Description :
============================================================================
*/

#include <string.h>
#include <stdlib.h>
#include <stdio.h>

#include <utask.h>
#include <HAL/hal/hal_ext.h>

#include "splitMerge.h"

extern void *__wrap_memset (void *s, int c, size_t n);
extern void *__wrap_memcpy(void *dest, const void *src, size_t n);

#define memset __wrap_memset
#define memcpy __wrap_memcpy

void split(int nbSlice, int width, int height, int overlap, unsigned char *input, unsigned char *output){
    if(output != NULL){
        int i;
        int sliceSize =  width*height/nbSlice;
        // Fill first and last line(s) with 0
        memset(output,0,width*overlap);
        // First Slice
        memcpy(output+width*overlap, input, sliceSize);
        // Copy next line(s) if several slice
        if (nbSlice > 1){
            memcpy(output +  width*overlap + sliceSize , input + sliceSize, width*overlap);
        }
        // Slice other than first and last
        for(i=1; i<nbSlice-1; i++){
            int destIndex = i*(sliceSize+2*width*overlap);
            memcpy(output + destIndex, input+i*sliceSize-width*overlap, sliceSize+2*width*overlap);
        }
        // Last Slice
        i = nbSlice-1;
        if(nbSlice > 1){
            // we have i = nbSlice -1;
            int destIndex = i*(sliceSize+2*width*overlap);
            memcpy(output + destIndex, input+i*sliceSize-width*overlap, sliceSize+width*overlap);
        }
        // Last line
        memset(output + (height+overlap*(nbSlice*2-1))*width,0,width*overlap);
    } else {
        // Output has been splitted and is null
        // Fill first and last line with 0
        memset(input - width*overlap, 0, width*overlap);
        // Last line
        memset(input + height*width, 0, width*overlap);
    }
}

void hierarchicalSplit(int nbSlice, int width, int height, int overlap, unsigned char *input, unsigned char *output){
	//printf("cid %d nbSlice %d width %d height %d overlap %d input %p output %p total out %d\n", __k1_get_cluster_id(), nbSlice, width, height, overlap, input, output, width*height+(nbSlice-1)*2*overlap*width);
    if(output != NULL)
    {
	if(nbSlice==1){
		memcpy(output, input, width*height+(nbSlice-1)*2*overlap*width);
	}else{
		#if 0
		int i;
		int sliceSize =  width*height/nbSlice;
		int size = (sliceSize+2*width*overlap);
		printf("cid %d ==> %p %p, nbSlice %d width %d height %d overlap %d size %d sliceSize %d\n", __k1_get_cluster_id(), input, output, nbSlice, width, height, overlap, size, sliceSize);
		for(i=0;i<nbSlice;i++)
		{
        		int destIndex = i*size;
			
	       		memcpy(output + destIndex, input+i*sliceSize, size);
			printf("cid %d ==> destIndex %d\n", __k1_get_cluster_id(), destIndex);
        	}
		#endif
		#if 0
		int i;
		//int sliceSize =  width*height/nbSlice;
		int sliceSize = (height+(nbSlice-1)*2*overlap)/nbSlice * width;
		//int size = sliceSize+2*width*overlap;
		//printf("cid %d ==> %p %p, nbSlice %d width %d height %d overlap %d size %d sliceSize %d\n", __k1_get_cluster_id(), input, output, nbSlice, width, height, overlap, size, sliceSize);
		//memcpy(output, input, sliceSize+overlap*width);
		memcpy(output, input, sliceSize);
		for(i=1; i<nbSlice-1; i++)
		{
		//    printf("cid %d booooooo\n", __k1_get_cluster_id());
			//int destIndex = i*(sliceSize+2*width*overlap);
			int destIndex = i*sliceSize;
			memcpy(output + destIndex, input+i*(height*width/nbSlice)-width*overlap, sliceSize);
		}
		i = nbSlice - 1;
		//int destIndex = i*(sliceSize+2*width*overlap);
		int destIndex = i*sliceSize;
		//printf("cid %d, output %d input %d, size %d\n", __k1_get_cluster_id(), destIndex, i*sliceSize-width*overlap, sliceSize+2*width*overlap);
		memcpy(output + destIndex, input+i*(height*width/nbSlice)-width*overlap, sliceSize);
		#endif
		assert((height)%nbSlice == 0 && "(height*width) modulo nbSlice must be 0");
		int sliceSize = (height+(nbSlice-1)*2*overlap)/nbSlice * width;
		int i;
		for(i=0;i<nbSlice;i++)
		{
			int destIndex = i*sliceSize;
			int sourceIndex = i*(sliceSize - 2*overlap*width);
			memcpy(output + destIndex, input + sourceIndex, sliceSize);
			//printf("cid %d i %d output %.1f input %.1f size %.1f\n", __k1_get_cluster_id(), i, (float)destIndex/(float)width, (float)sourceIndex/(float)width, (float)sliceSize/(float)width);
		}
		//printf("\n");
	}
    } else {
        // Output has been splitted and is null
        // Fill first and last line with 0
        memset(input - width*overlap, 0, width*overlap);
        // Last line
        memset(input + height*width, 0, width*overlap);
    }
}

void merge(int nbSlice, int width, int height, unsigned char *input, unsigned char *output){
    int i;
    int sliceSize =  width*height/nbSlice;
    // Copy the slice content except the first and last lines
    for(i = 0; i< nbSlice; i++){
        int idx = i*(sliceSize+2*width);
        memcpy(output+i*sliceSize, input+idx+width, sliceSize);
    }
}
