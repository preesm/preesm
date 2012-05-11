/*
	============================================================================
	Name        : displayYUV.c
	Author      : mpelcat
	Version     :
	Copyright   : Displaying YUV frames one next to another in a row
	Description :
	============================================================================
*/
#ifndef DISPLAY_YUV
#define DISPLAY_YUV

#include <SDL.h>

#define DISPLAY_W 1056
#define DISPLAY_H 288
#define FRAME_NB_MAX 3	// Maximum number of frames

/**
* Structure representing one display
*/
typedef struct YuvDisplay {
	SDL_Overlay* overlays[FRAME_NB_MAX];	// One overlay per frame
	SDL_Surface *screen;					// SDL surface where to display
	int currentXMin;						// Position for next display
} YuvDisplay;


/**
* Initializes a display frame. Be careful, once a window size has been chosen,
* all videos must share the same window size
* 
* @param id display unique identifier
* @param xsize width
* @param ysize heigth
*/
void yuvDisplayInit (int id, int xsize, int ysize);

/**
* Display one YUV frame
* 
* @param id display unique identifier
* @param y luma
* @param u chroma U
* @param v chroma V
*/
void yuvDisplay(int id, unsigned char *y, unsigned char *u, unsigned char *v);

#endif