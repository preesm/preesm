/*
	============================================================================
	Name        : displayRGB.c
	Author      : mpelcat & kdesnos
	Version     :
	Copyright   : Displaying RGB frames one next to another in a row
	Description :
	============================================================================
*/

#include "displayRGB.h"
#include <SDL.h>
#include <time.h>

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
    printf("displayRGBInit()\n");

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

void displayRGB(int id, unsigned char *r, unsigned char *g, unsigned char *b)
{
    SDL_Surface* overlay = display.overlays[id];
    SDL_Rect video_rect = {overlay->w*id,0,overlay->w, overlay->h};	// SDL frame position and size (x, y, w, h)
    int vSize = video_rect.w * video_rect.h;
	int idxPxl;
    int rgb = 0;

	printf("displayRGB()\n");
     if (SDL_LockSurface(overlay) < 0)
    {
        fprintf(stderr, "Can't lock screen: %s\n", SDL_GetError());
        system("PAUSE");
    }
    
    for(idxPxl = 0; idxPxl < 3*overlay->h*overlay->w; idxPxl++){
        switch(rgb){
        case 0:
            *(((char*)(overlay->pixels))+idxPxl) = *(r+idxPxl/3) ;
            break;
        case 1:
            *(((char*)(overlay->pixels))+idxPxl) = *(g+idxPxl/3) ;
            break;
        case 2:
            *(((char*)(overlay->pixels))+idxPxl) = *(b+idxPxl/3) ;
            break;
        }
        rgb = (rgb + 1)%3;
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
            exit(0);
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
