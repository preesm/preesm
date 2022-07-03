/*
	============================================================================
	Name        : displayRGB.h
	Author      : mpelcat & kdesnos
	Version     : 1.0
	Copyright   : CeCILL-C, IETR, INSA Rennes
	Description : Displaying RGB frames one next to another in a row.
	============================================================================
*/
#ifndef DISPLAY_RGB
#define DISPLAY_RGB
#include "preesm.h"

#define NB_DISPLAY 2
#define DISPLAY_W 480*NB_DISPLAY
#define DISPLAY_H 270

/**
* Function used to display an RGB image
*
* @param id
*        The id of the displayed frame in the window.
* @param height
*        height of the frame
* @param width
*        width of the frame
* @param rgb
*        Array containing the 3 components of the displayed image.
*/
void displayRGB(int id, int height, int width, IN unsigned char *rgb);

/**
* Identical to display RGB, but receive only one luminance component.
*
* @param id
*        The id of the displayed frame in the window.
* @param lum
*        Array containing the lum component of the displayed image.
*/
void displayLum(int id, IN unsigned char *lum);

/**
* Function used to initialize a frame in the SDL window.
* The initialize frame have a unique id, and its own height and width.
* Each new frame is placed at the right of previously initialized frames.
* This function may fail if the id >= NB_DISPLAY or if the new frame exceeds
* the dimensions of the window (DISPLAY_W and DISPLAY_H).
*
* @param id
*        Unique identifier of the new frame.
* @param height
*        height of the new frame
* @param width
*        width of the new frame
*/
void displayRGBInit (int id, int height, int width);

/**
* Function used to free a display
*
* @param id
*        the id of the freed display
*/
void finalizeRGB(int id);

/**
* Function used to refresh the display with the given id in the windo.
*
* @param id
*        the id of the refreshed display.
*/
void refreshDisplayRGB(int id);

#endif
