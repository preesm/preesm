/*
	============================================================================
	Name        : displayYUV.c
	Author      : mpelcat
	Version     :
	Copyright   : Displaying YUV frames one next to another in a row
	Description :
	============================================================================
*/
#ifdef WIN32

#include "yuvDisplay.h"

// State of the yuvDisplay actor: an overlay of fixed size
YuvDisplay display;
int initialized = 0;

/**
* Initializes a display frame. Be careful, once a window size has been chosen,
* all videos must share the same window size
* 
* @param id display unique identifier
* @param xsize width
* @param ysize heigth
*/
void yuvDisplayInit (int id, int xsize, int ysize)
{	
	if(initialized==0){
		display.currentXMin = 0;
	}

	if(ysize > DISPLAY_H){
		fprintf(stderr, "SDL screen is not high enough for display %d.", id);
		exit(1);
	}
	else if(id >= FRAME_NB_MAX){
		fprintf(stderr, "The number of displays is limited to %d.", FRAME_NB_MAX);
		exit(1);
	}
	else if(display.currentXMin + xsize > DISPLAY_W){
		fprintf(stderr, "The number is not wide enough for display %d.", FRAME_NB_MAX);
		exit(1);
	}

	if(initialized==0){
		// Generating window name
		char* name = "Preesm display";
		initialized = 1;

		if(SDL_Init(SDL_INIT_VIDEO)) {
			fprintf(stderr, "Could not initialize SDL - %s\n", SDL_GetError());
			exit(1);
		}

		display.screen = SDL_SetVideoMode(DISPLAY_W, DISPLAY_H, 0, 0);
		SDL_WM_SetCaption(name, name);
		if(!display.screen) {
			fprintf(stderr, "SDL: could not set video mode - exiting\n");
			exit(1);
		}
	}

	display.overlays[id] = SDL_CreateYUVOverlay(xsize, ysize,
							   SDL_IYUV_OVERLAY, display.screen);
	
	display.currentXMin += xsize;
}
/**
* Display one YUV frame
* 
* @param id display unique identifier
* @param y luma
* @param u chroma U
* @param v chroma V
*/
void yuvDisplay(int id, unsigned char *y, unsigned char *u, unsigned char *v)
{
	SDL_Overlay* overlay = display.overlays[id];
	SDL_Rect video_rect = {overlay->w*id,0,overlay->w, overlay->h};	// SDL frame position and size (x, y, w, h)

	int ySize = video_rect.w * video_rect.h;
	SDL_LockYUVOverlay(overlay);
	memcpy(overlay->pixels[0], y, ySize);
	memcpy(overlay->pixels[1], u, ySize/4);
	memcpy(overlay->pixels[2], v, ySize/4);

	SDL_UnlockYUVOverlay(overlay);

	SDL_DisplayYUVOverlay(overlay, &video_rect);
}
#endif