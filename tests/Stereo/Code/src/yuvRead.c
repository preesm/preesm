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

#include "yuvRead.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "clock.h"


/*========================================================================

   Global Variable

   ======================================================================*/
#define NB_PATH 2
// char* path[] = {"D:/Boulot/video/left.yuv","D:/Boulot/video/right.yuv"};
char* path[] = {"D:/Data/left.yuv","D:/Data/right.yuv"};

static FILE * ptfile[NB_PATH] ;
/*========================================================================

   initReadYUV DEFINITION

   ======================================================================*/
void initReadYUV(int id, int xSize, int ySize) {
    int fsize;
    if((ptfile[id] = fopen(path[id], "rb")) == NULL )
    {
        fprintf(stderr,"ERROR: Task read cannot open yuv_file '%s'\n", path[id]);
        system("PAUSE");
        return;
    }

#ifdef VERBOSE
    printf("Opened file '%s'\n", PATH);
#endif

    // Obtain file size:
    fseek (ptfile[id] , 0 , SEEK_END);
    fsize = ftell (ptfile[id]);
    rewind (ptfile[id]);
    if(fsize < NB_FRAME*(xSize*ySize + xSize*ySize/2))
    {
        fprintf(stderr,"ERROR: Task read yuv_file incorrect size");
        //system("PAUSE");
        //return;
    }

#ifdef VERBOSE
    printf("Correct size for yuv_file '%s'\n", PATH);
#endif

    // Set initial clock
    startTiming(0);
}

/*========================================================================

   readYUV DEFINITION

   ======================================================================*/
void readYUV(int id, int xSize, int ySize, unsigned char *y, unsigned char *u, unsigned char *v) {

    if( ftell(ptfile[id])/(xSize*ySize + xSize*ySize/2) >=NB_FRAME){
        rewind(ptfile[id]);
    }
	
	if(id == 1 && ftell(ptfile[id])%(FPS*(xSize*ySize + xSize*ySize/2)) == 0){
			unsigned int time = 0;
            time = stopTiming(0);
            printf("\nMain: %d frames in %d us - %f fps\n", FPS ,time, ((float)FPS)/(float)time*1000000);
            startTiming(0);
    }

    fread(y, sizeof(char), xSize * ySize, ptfile[id]);
    fread(u, sizeof(char), xSize * ySize / 4, ptfile[id]);
    fread(v, sizeof(char), xSize * ySize / 4, ptfile[id]);
}
