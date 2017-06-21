/*
	============================================================================
	Name        : displayYUV.c
	Author      : mpelcat & kdesnos
	Version     :
	Copyright   : CECILL-C
	Description : Displaying YUV frames one next to another in a row
	============================================================================
*/
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#include "yuvDisplay.h"
#include <SDL.h>


extern int stopThreads;

/**
* Structure representing one display
*/
typedef struct YuvDisplay
{
    SDL_Texture* textures[NB_DISPLAY];	    // One overlay per frame
    SDL_Window *screen;					    // SDL surface where to display
    SDL_Renderer *renderer;
    int currentXMin;						// Position for next display
    int initialized;                        // Initialization done ?

} YuvDisplay;


// Initialize
YuvDisplay display = {.textures={NULL},.initialized=0};

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

    if(display.initialized==0)
    {
        display.currentXMin = 0;
    }

    if(height > DISPLAY_H)
    {
        fprintf(stderr, "SDL screen is not high enough for display %d.\n", id);
        system("PAUSE");
        exit(1);
    }
    else if(id >= NB_DISPLAY)
    {
        fprintf(stderr, "The number of displays is limited to %d.\n", NB_DISPLAY);
        system("PAUSE");
        exit(1);
    }
    else if(display.currentXMin + width > DISPLAY_W)
    {
        fprintf(stderr, "The number is not wide enough for display %d.\n", NB_DISPLAY);
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

        display.screen = SDL_CreateWindow(name, SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED,
                                          DISPLAY_W, DISPLAY_H, SDL_WINDOW_SHOWN);
        if(!display.screen)
        {
            fprintf(stderr, "SDL: could not set video mode - exiting\n");
            exit(1);
        }

        display.renderer = SDL_CreateRenderer(display.screen, -1, SDL_RENDERER_ACCELERATED);
        if (!display.renderer)
        {
            fprintf(stderr, "SDL: could not create renderer - exiting\n");
            exit(1);
        }
    }

    if(display.textures[id] == NULL)
    {

        display.textures[id] = SDL_CreateTexture(display.renderer,
                               SDL_PIXELFORMAT_IYUV,
                               SDL_TEXTUREACCESS_STREAMING,
                               width, height);

        if (!display.textures[id])
        {
            fprintf(stderr, "SDL: could not create texture - exiting\n");
            exit(1);
        }

        display.currentXMin += width;
    }

}


void yuvDisplay(int id, unsigned char *y, unsigned char *u, unsigned char *v)
{

    SDL_Texture* texture = display.textures[id];
    int w,h;

    // Retrieve texture attribute
    SDL_QueryTexture(texture, NULL, NULL, &w, &h);

    SDL_UpdateYUVTexture(
                        texture, NULL,
                        y, w,
                        u, w/2,
                        v, w/2
                    );

    yuvRefreshDisplay(id);
}


void yuvRefreshDisplay(int id)
{
    SDL_Texture* texture = display.textures[id];
    SDL_Event event;
    SDL_Rect screen_rect;

    SDL_QueryTexture(texture, NULL, NULL, &(screen_rect.w), &(screen_rect.h));

    screen_rect.x = screen_rect.w*id;
    screen_rect.y = 0;

    SDL_RenderCopy(display.renderer, texture, NULL, &screen_rect);
    SDL_RenderPresent(display.renderer);

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


void yuvFinalize(int id)
{
    SDL_DestroyTexture(display.textures[id]);
    SDL_DestroyRenderer(display.renderer);
    SDL_DestroyWindow(display.screen);
}
