/*
	============================================================================
	Name        : readYUV.c
	Author      : kdesnos
    Author      : mpelcat
	Version     : 1.0
	Copyright   : CECILL-C
	Description :
	============================================================================
*/

#include "readYUV.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>

/*========================================================================

   Global Variable

   ======================================================================*/
static FILE *ptfile ;
clock_t tick;

/*========================================================================

   initReadYUV DEFINITION

   ======================================================================*/
void initReadYUV(int xSize, int ySize) {
    int fsize;
    if((ptfile = fopen(PATH, "rb")) == NULL )
        {
            fprintf(stderr,"ERROR: Task read cannot open yuv_file '%s'\n", PATH);
            system("PAUSE");
            return;
        }

        // Obtain file size:
        fseek (ptfile , 0 , SEEK_END);
        fsize = ftell (ptfile);
        rewind (ptfile);
        if(fsize < NB_FRAME*(xSize*ySize + xSize*ySize/2))
        {
            fprintf(stderr,"ERROR: Task read yuv_file incorrect size");
            system("PAUSE");
            return;
        }

        // Set initial clock
        tick = clock();
}

/*========================================================================

   readYUV DEFINITION

   ======================================================================*/
void readYUV(int xSize, int ySize, unsigned char *y, unsigned char *u, unsigned char *v) {

    if( ftell(ptfile)/(xSize*ySize + xSize*ySize/2) >=NB_FRAME){
        rewind(ptfile);
        tick = clock()-tick;
        printf("\nMain: %d frames in %f - %f fps\n", NB_FRAME-1 ,tick/(float)CLOCKS_PER_SEC, (NB_FRAME-1.0)/(float)tick*(float)CLOCKS_PER_SEC);
        tick = clock();
    }
    fread(y, sizeof(char), xSize * ySize, ptfile);
    fread(u, sizeof(char), xSize * ySize / 4, ptfile);
    fread(v, sizeof(char), xSize * ySize / 4, ptfile);
}
