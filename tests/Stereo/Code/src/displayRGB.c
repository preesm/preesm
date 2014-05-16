/*
	============================================================================
	Name        : displayRGB.c
	Author      : mpelcat & kdesnos
	Version     : 1.0
	Copyright   : CeCILL-C, IETR, INSA Rennes
	Description : Displaying RGB frames one next to another in a row
	============================================================================
*/

#include "displayRGB.h"
#include <SDL.h>
#include <pthread.h>
#include <time.h>

extern int stopThreads;

/**
* Structure representing one display
*/
typedef struct RGBDisplay
{
    SDL_Surface* overlays[NB_DISPLAY];	    // One overlay per frame
    SDL_Surface *screen;					// SDL surface where to display
    int currentXMin;						// Position for next display
    int initialized;                        // Initialization done ?

} RGBDisplay;


// State of the yuvDisplay actor: an overlay of fixed size
RGBDisplay display = { INIT_OVERLAY, NULL, 0, 0 };

void displayRGBInit (int id, int height, int width)
{
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

        display.screen = SDL_SetVideoMode(DISPLAY_W, DISPLAY_H, 24, SDL_HWSURFACE);
        SDL_WM_SetCaption(name, name);
        if(!display.screen)
        {
            fprintf(stderr, "SDL: could not set video mode - exiting\n");
            exit(1);
        }
    }
    if(display.overlays[id] == NULL)
    {

        display.overlays[id] = SDL_CreateRGBSurface(SDL_SWSURFACE, width, height,
                               24, 0x0000FF,0x00FF00,0xFF0000,0);
        display.overlays[id]->pitch = 3*width;

        display.currentXMin += width;
    }
}

void displayLum(int id, unsigned char *lum){
    display3Components(id,lum,lum,lum,1);
}

void displayRGB(int id, int height, int width, unsigned char *rgb){
	unsigned char *r = rgb;
    unsigned char *g = rgb+1;
    unsigned char *b = rgb+2;
    display3Components(id,r,g,b,3);
}

void display3Components(int id, unsigned char *r, unsigned char *g, unsigned char *b, int pixelStride){
    SDL_Surface* overlay = display.overlays[id];
    SDL_Rect video_rect = {overlay->w*id,0,overlay->w, overlay->h};	// SDL frame position and size (x, y, w, h)
    int vSize = video_rect.w * video_rect.h;
	int idxPxl;
    int rgb = 0;

     if (SDL_LockSurface(overlay) < 0)
    {
        fprintf(stderr, "Can't lock screen: %s\n", SDL_GetError());
        system("PAUSE");
    }

    for(idxPxl = 0; idxPxl < overlay->h*overlay->w; idxPxl++){
        *(((char*)(overlay->pixels))+3*idxPxl)   = *(r+idxPxl*pixelStride) ;
        *(((char*)(overlay->pixels))+3*idxPxl+1) = *(g+idxPxl*pixelStride) ;
        *(((char*)(overlay->pixels))+3*idxPxl+2) = *(b+idxPxl*pixelStride) ;        
    }

    SDL_UnlockSurface(overlay);
    refreshDisplayRGB(id);
}
void refreshDisplayRGB(int id)
{

    SDL_Event event;
    SDL_Rect video_rect;

        video_rect.x = display.overlays[id]->w*id;
        video_rect.y = 0;
        video_rect.w = display.overlays[id]->w;
        video_rect.h = display.overlays[id]->h;

        SDL_BlitSurface(display.overlays[id],0,display.screen,&video_rect);
        SDL_UpdateRect(display.screen, 0, 0, 0, 0);

        //SDL_DisplayYUVOverlay(display.overlays[id], &video_rect);

    // Grab all the events off the queue.
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

}

void finalizeRGB(int id)
{
    /*
    SDL_FreeYUVOverlay(display.overlays[id]);
    */
}
