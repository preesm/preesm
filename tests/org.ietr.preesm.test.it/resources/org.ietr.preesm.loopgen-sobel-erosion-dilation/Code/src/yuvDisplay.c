/*
	============================================================================
	Name        : displayYUV.c
	Author      : mpelcat & kdesnos
	Version     :
	Copyright   : CECILL-C
	Description : Displaying YUV frames one next to another in a row
	============================================================================
*/

#include "yuvDisplay.h"
#include <SDL.h>

extern int stopThreads;

/**
* Structure representing one display
*/
typedef struct YuvDisplay
{
    SDL_Overlay* overlays[NB_DISPLAY];	    // One overlay per frame
    SDL_Surface *screen;					// SDL surface where to display
    int currentXMin;						// Position for next display
    int initialized;                        // Initialization done ?

} YuvDisplay;

//#define DISPLAY_DISABLE

// State of the yuvDisplay actor: an overlay of fixed size
YuvDisplay display = { INIT_OVERLAY, NULL, 0, 0 };

/**
* Initializes a display frame. Be careful, once a window size has been chosen,
* all videos must share the same window size
*
* @param id display unique identifier
* @param width width
* @param height heigth
*/
void yuvDisplayInit (int id, int width, int height)
{
#ifndef DISPLAY_DISABLE
    if(display.initialized==0)
    {
        display.currentXMin = 0;
    }

    if(height > DISPLAY_H)
    {
        fprintf(stderr, "SDL screen is not high enough for display %d.", id);
        system("PAUSE");
        exit(1);
    }
    else if(id >= NB_DISPLAY)
    {
        fprintf(stderr, "The number of displays is limited to %d.", NB_DISPLAY);
        system("PAUSE");
        exit(1);
    }
    else if(display.currentXMin + width > DISPLAY_W)
    {
        fprintf(stderr, "The number is not wide enough for display %d.", NB_DISPLAY);

        system("PAUSE");
        exit(1);
    }


#ifdef VERBOSE
    printf("SDL screen height OK, width OK, number of displays OK.\n", id);
#endif

    if(display.initialized==0)
    {
        // Generating window name
        char* name = "Display";
        display.initialized = 1;

        if(SDL_Init(SDL_INIT_VIDEO))
        {
            fprintf(stderr, "Could not initialize SDL - %s\n", SDL_GetError());
            exit(1);
        }

        display.screen = SDL_SetVideoMode(DISPLAY_W, DISPLAY_H, 32, SDL_HWSURFACE);
        SDL_WM_SetCaption(name, name);
        if(!display.screen)
        {
            fprintf(stderr, "SDL: could not set video mode - exiting\n");
            exit(1);
        }
    }

    if(display.overlays[id] == NULL)
    {

        display.overlays[id] = SDL_CreateYUVOverlay(width, height,
                               SDL_IYUV_OVERLAY, display.screen);

        memset(display.overlays[id]->pixels[0], 0, width*height);
        memset(display.overlays[id]->pixels[1], 0, width*height/4);
        memset(display.overlays[id]->pixels[2], 0, width*height/4);
        display.currentXMin += width;
    }
#endif
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
#ifndef DISPLAY_DISABLE
    SDL_Overlay* overlay = display.overlays[id];
    SDL_Rect video_rect = {overlay->w*id,0,overlay->w, overlay->h};	// SDL frame position and size (x, y, w, h)
    int height = video_rect.w * video_rect.h;

    //SDL_LockYUVOverlay(overlay);
    if (SDL_LockYUVOverlay(overlay) < 0)
    {
        fprintf(stderr, "Can't lock screen: %s\n", SDL_GetError());
        system("PAUSE");
    }
    memcpy(overlay->pixels[0], y, height);
    memcpy(overlay->pixels[1], u, height/4);
    memcpy(overlay->pixels[2], v, height/4);

    SDL_UnlockYUVOverlay(overlay);

    yuvRefreshDisplay(id);
#endif
}
void yuvRefreshDisplay(int id)
{
#ifndef DISPLAY_DISABLE
    SDL_Event event;
    SDL_Rect video_rect;

        video_rect.x = display.overlays[id]->w*id;
        video_rect.y = 0;
        video_rect.w = display.overlays[id]->w;
        video_rect.h = display.overlays[id]->h;

        SDL_DisplayYUVOverlay(display.overlays[id], &video_rect);

    /* Grab all the events off the queue. */
    while (SDL_PollEvent(&event))
    {
        switch (event.type)
        {
        case SDL_QUIT:
        stopThreads = 1;
            break;
        default:
            break;
        }
    }
#endif
}

void yuvFinalize(int id)
{
#ifndef DISPLAY_DISABLE
    SDL_FreeYUVOverlay(display.overlays[id]);
#endif
}
