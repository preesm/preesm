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
#include <SDL_thread.h>

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

#endif