/*
	============================================================================
	Name        : displayYUV.h
	Author      : mpelcat
	Version     :
	Copyright   : Displaying YUV frames one next to another in a row
	Description :
	============================================================================
*/
#ifndef DISPLAY_YUV
#define DISPLAY_YUV

#define DISPLAY_W 352*2
#define DISPLAY_H 288
#define FRAME_NB_MAX 2	// Maximum number of frames
#define INIT_OVERLAY {NULL, NULL} // must be se same size as FRAME_NB_MAX

void yuvDisplay(int id, unsigned char *y, unsigned char *u, unsigned char *v);
void yuvDisplayInit (int id, int xsize, int ysize);
void yuvFinalize(int id);
void yuvRefreshDisplay();

#endif
