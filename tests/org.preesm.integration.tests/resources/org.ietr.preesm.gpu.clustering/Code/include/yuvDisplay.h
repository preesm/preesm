/*
============================================================================
Name        : displayYUV.h
Author      : mpelcat & kdesnos & jheulot
Version     :
Copyright   :
Description : Displaying YUV frames one next to another in a row.
============================================================================
*/
#ifndef DISPLAY_YUV
#define DISPLAY_YUV

#include "preesm.h"

#define NB_DISPLAY 1
#define DISPLAY_W 1920*NB_DISPLAY
#define DISPLAY_H 1080

#define INIT_OVERLAY {NULL} // must be se same size as NB_DISPLAY

#define PATH_TTF PROJECT_ROOT_PATH "/dat/DejaVuSans.ttf"

/**
* Function called to display one of the YUV frame of the window.
* The size of the displayed frame must correspond to the one declared
* when initializing the display.
* This function also display the number of Frames per seconds.
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
void yuvDisplay(int id, IN unsigned char *y, IN unsigned char *u, IN unsigned char *v);

/**
* Function called to display one of the YUV frame of the window.
* The size of the displayed frame must correspond to the one declared
* when initializing the display.
* This function also display the number of Frames per seconds and the number
* of slices used for parallelization.
*
* @param id
*        The id of the frame to display
* @param nbSlice
*        The number of slice used to parallelize computations.
* @param y
*        the Y component of the frame to display
* @param u
*        the U component of the frame to display
* @param v
*        the V component of the frame to display
*/
void yuvDisplayWithNbSlice(int id, int nbSlice, IN unsigned char *y, IN unsigned char *u, IN unsigned char *v);

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
* @param width
*        the width of the initialized display
* @param height
*        the height of the initialized display
*/
void yuvDisplayInit(int id, int width, int height);

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
