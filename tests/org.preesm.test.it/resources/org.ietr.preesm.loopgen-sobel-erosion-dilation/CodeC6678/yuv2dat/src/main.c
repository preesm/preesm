/*
	============================================================================
	Name        : main.c
	Author      : mpelcat
	Version     : 1.0
	Copyright   : CECILL-C
	Description : This executable aims at converting a yuv file into a dat file
				  that can be loaded in c6678 memory.
				  The dat file contains textual hexadecimal bytes that can be
				  loaded in the memory of the c6678 via CCS.
	============================================================================
*/

#include "yuvRead.h"
#include "datCreate.h"
#include <stdio.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#define SUCCESS 0
#define FAILURE -1

int xsize = 0;
int ysize = 0;
char* inputFileName = NULL;
char* outputFileName = NULL;
int nbFrames = 0;

int parseCmdLine(int argc, char *argv[]){
	int i=0;
	for (i=0;i<argc;i++){
		char* option = argv[i];
		if(strstr (option,"-x=") == option){
			xsize = atoi (option+3);
		}
		else if(strstr (option,"-y=") == option){
			ysize = atoi (option+3);
		}
		else if(strstr (option,"-i=") == option){
			inputFileName = option+3;
		}
		else if(strstr (option,"-o=") == option){
			outputFileName = option+3;
		}
		else if(strstr (option,"-f=") == option){
			nbFrames = atoi (option+3);
		}
	}

	if(xsize <= 0 || ysize <= 0 || inputFileName == NULL || outputFileName == NULL || nbFrames <= 0){
		printf("Invalid arguments. The arguments should look like (for example):\n");
		printf("yuv2dat -x=352 -y=288 -i=akiyo.yuv -o=akiyo.dat -f=300\n");

		return FAILURE;
	}

#ifdef VERBOSE
    printf("Arguments xsize %d ysize %d %s %s nbFrames %d\n", xsize, ysize, inputFileName, outputFileName, nbFrames);
#endif

	return SUCCESS;
}

int main(int argc, char *argv[])
{
	int i=0;

	if(parseCmdLine(argc, argv) == SUCCESS &&
		initReadYUV(inputFileName, xsize, ysize, nbFrames) == SUCCESS &&
		createDatFile(outputFileName, xsize*ysize*nbFrames/2*3) == SUCCESS){

			int lumasize = xsize * ysize * sizeof(unsigned char);
			int chromasize = lumasize / 4;
			unsigned char *y = (unsigned char *) malloc(lumasize);
			unsigned char *u = (unsigned char *) malloc(chromasize);
			unsigned char *v = (unsigned char *) malloc(chromasize);

		for(i=0;i<nbFrames;i++){
			readYUV(xsize, ysize, y, u, v);
			writeData(y,lumasize);
			writeData(u,chromasize);
			writeData(v,chromasize);
		}

		free(y);
		free(u);
		free(v);

        closeDatFile();
        closeYuvFile();
		return SUCCESS;
	}
	else{
		return FAILURE;
	}
}
