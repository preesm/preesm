/*
	============================================================================
	Name        : displayYUV.h
	Author      : mpelcat & kdesnos
	Version     :
	Copyright   :
	Description : Displaying YUV frames one next to another in a row.
	============================================================================
*/
#ifndef DISPLAY_YUV
#define DISPLAY_YUV

#define NB_DISPLAY 1
#define DISPLAY_W 352*NB_DISPLAY
#define DISPLAY_H 288

#define INIT_OVERLAY {NULL} // must be se same size as NB_DISPLAY

/**
* Function called to display one of the YUV frame of the window.
* The size of the displayed frame must correspond to the one declared
* when initializing the display.
*
* @param id
*        The id of the frame to display
* @param y
*        the Y component of the frame to display
* @param u
*        the U component of the frame to display
* @param v
*        the V component of the frame to display
*/
void yuvDisplay(int id, unsigned char *y, unsigned char *u, unsigned char *v);

/**
* Initialize a new display with a parameterizable resolution and open a
* display window (if not already opened).
* The resolution of the new display must be compatible with the one
* defined with the pre-processor variables.
* A single window can contain several display. In such case, the total width
* of the displays must not exceed DISPLAY_W and the maximum height of a
* display must not exceed DISPLAY_H. Displays will be placed side by side
* in ascending id. All displays must have the same width in the current
* implementation.
*
* @param id
*        The ID of the initialized display.
* @param xsize
*        the width of the initialized display
* @param ysize
*        the height of the initialized display
*/
void yuvDisplayInit (int id, int xsize, int ysize);

/**
* Function used to free a display
*
* @param id
*        the id of the freed display
*/
void yuvFinalize(int id);

/**
* Function used to refresh the display with the given id in the windo.
*
* @param id
*        the id of the refreshed display.
*/
void yuvRefreshDisplay(int id);

#endif
