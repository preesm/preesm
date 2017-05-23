/*
 ============================================================================
 Name        : ppm.c
 Author      : kdesnos
 Version     : 1.0
 Copyright   : CECILL-C, IETR, INSA Rennes
 Description : Actor code to read/write a ppm file from the hard drive
 ============================================================================
 */

#include "ppm.h"

#include <xdc/runtime/System.h>
#include <xdc/runtime/Timestamp.h>
#include <xdc/runtime/Types.h>

#include <string.h>

/** Input Data in a "NOINIT" section must be loaded separately */
/** DAT File can be downloaded here:
 *  http://preesm.sourceforge.net/website/data/uploads/other/im0.dat*/
#pragma DATA_SECTION(im0_data,".myNoInitMem")
static const unsigned char im0_data[375*450*3];

/** Input Data in a "NOINIT" section must be loaded separately */
/** DAT File can be downloaded here:
 *  http://preesm.sourceforge.net/website/data/uploads/other/im1.dat*/
#pragma DATA_SECTION(im1_data,".myNoInitMem")
static const unsigned char im1_data[375*450*3];

#define NB_ITERATION_MEASURED 1

/*========================================================================

 Global Variable

 ======================================================================*/

void writePPM(int height, int width, unsigned char *gray) {

}

void readPPMInit(int id, int height, int width) {

}

void readPPM(int id, int height, int width, unsigned char *rgbPtr) {
	const unsigned char* im_data = (id == 0) ? im0_data : im1_data;
	static int i = 0;
	static unsigned int time = 0;
	unsigned int now;

	if (i == 0) {
		// 32bits precision is not sufficient here. Needs 64bits instead.
		now = Timestamp_get32();
		unsigned int delta = (now - time) / NB_ITERATION_MEASURED;
		float fps = 1000000000.0 / (float) delta;
		System_printf("fps: %f\n", fps);
		time = Timestamp_get32();
	}
	i = (i + 1) % NB_ITERATION_MEASURED;

	memcpy((void*)rgbPtr,(void*)im_data, 3*height*width*sizeof(unsigned char));
}
