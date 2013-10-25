/*
	============================================================================
	Name        : datCreate.c
    Author      : mpelcat
	Version     : 1.0
	Copyright   : CECILL-C
	Description :
	============================================================================
*/

#include "datCreate.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#define LINESIZE 80
/*========================================================================

   Global Variable

   ======================================================================*/
static FILE *ptDatFile = NULL ;

/*========================================================================

   Initialize the dat file.

   ======================================================================*/
int createDatFile(char* filePath, int size){
	//unsigned char temp[LINESIZE];
	if((ptDatFile = fopen(filePath, "w")) != NULL )
    {
        fprintf (ptDatFile, "1651 6 0 0 %d\n", size);

		#ifdef VERBOSE
			printf("Opened file '%s'\n", filePath);
		#endif
        return SUCCESS;
    }
    else{
        fprintf(stderr,"ERROR: Task read cannot open dat_file '%s'\n", filePath);
        return FAILURE;
    }

}

void writeData(unsigned char* buffer, int size){
    unsigned char temp[LINESIZE];
    int i=0;
    for(i=0;i<size;i++){
        fprintf (ptDatFile, "0x%02x\n", buffer[i]);
    }
}

void closeDatFile(){
    fclose(ptDatFile);
}
