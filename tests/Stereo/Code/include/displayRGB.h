/*
	============================================================================
	Name        : displayRGB.h
	Author      : mpelcat & kdesnos
	Version     :
	Copyright   :
	Description : Displaying RGB frames one next to another in a row.
	============================================================================
*/
#ifndef DISPLAY_RGB
#define DISPLAY_RB

#define NB_DISPLAY 2
#define DISPLAY_W 450*NB_DISPLAY
#define DISPLAY_H 375

#define INIT_OVERLAY {NULL, NULL} // must be se same size as NB_DISPLAY


void displayRGB(int id, unsigned char *r, unsigned char *g, unsigned char *b);

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
