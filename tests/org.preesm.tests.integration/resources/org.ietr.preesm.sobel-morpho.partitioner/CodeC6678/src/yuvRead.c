

#include "yuvRead.h"
#include <xdc/runtime/System.h>
#include <string.h>
#include <xdc/runtime/Timestamp.h>
#include <xdc/runtime/Types.h>

#define NB_FRAMES 10
#define WIDTH 1280
#define HEIGHT 720
#define PICSIZE WIDTH*HEIGHT*3/2

// Reserving memory for the input sequence
// This memory should be loaded by the Load function of CCS
// Forcing in DDR3
#pragma DATA_SECTION(input_sequence, ".myInputVideoMem");
static unsigned char input_sequence [PICSIZE*NB_FRAMES];

int currentFrameIndex;

/*========================================================================

   Global Variable

   ======================================================================*/

/*========================================================================

   initReadYUV DEFINITION

   ======================================================================*/
void initReadYUV(int width, int height) {
	//System_printf("initYUV\n");
	currentFrameIndex = 0;
}

/*========================================================================

   readYUV DEFINITION

   ======================================================================*/
void readYUV(int width, int height, unsigned char *y, unsigned char *u, unsigned char *v) {

	static int i = 0;
	static unsigned int time = 0;
	unsigned int now;
	unsigned char* input_y = input_sequence + currentFrameIndex*PICSIZE;
	unsigned char* input_u = input_y + height*width;
	unsigned char* input_v = input_u + (height*width/4);

	if(i==0){
	now = Timestamp_get32();
	unsigned int delta = (now-time)/10;
	float fps = 1000000000.0 / (float)delta;
	System_printf("fps: %f\n", fps);
	time = Timestamp_get32();
	}
	i = (i +1) %10;
	memcpy(y,input_y,height*width*sizeof(char));
	memcpy(u,input_u,height*width*sizeof(char)/4);
	memcpy(v,input_v,height*width*sizeof(char)/4);

	currentFrameIndex++;
	currentFrameIndex = currentFrameIndex%NB_FRAMES;
}
