/*
	============================================================================
	Name        : displayRGB.c
	Author      : mpelcat & kdesnos
	Version     : 1.1
	Copyright   : CeCILL-C, IETR, INSA Rennes
	Description : Displaying RGB frames one next to another in a row
	============================================================================
	*/

#include <stdio.h>

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
	SDL_Texture* textures[NB_DISPLAY];	    // One overlay per frame
	SDL_Window *screen;					    // SDL window where to display
	SDL_Surface* surface[NB_DISPLAY];
	SDL_Renderer *renderer;
	int currentXMin;						// Position for next display
	int initialized;                        // Initialization done ?
} RGBDisplay;


// Initialize
RGBDisplay display = { .textures = { NULL }, .initialized = 0 };

void displayRGBInit(int id, int height, int width){
	if (display.initialized == 0)
	{
		display.currentXMin = 0;
	}

	if (height > DISPLAY_H)
	{
		fprintf(stderr, "SDL screen is not high enough for display %d.\n", id);
		system("PAUSE");
		exit(1);
	}
	else if (id >= NB_DISPLAY)
	{
		fprintf(stderr, "The number of displays is limited to %d.\n", NB_DISPLAY);
		system("PAUSE");
		exit(1);
	}
	else if (display.currentXMin + width > DISPLAY_W)
	{
		fprintf(stderr, "The number is not wide enough for display %d.\n", NB_DISPLAY);
		system("PAUSE");
		exit(1);
	}


#ifdef VERBOSE
	printf("SDL screen height OK, width OK, number of displays OK.\n", id);
#endif

	if (display.initialized == 0)
	{
		// Generating window name
		char* name = "Display";
		display.initialized = 1;

		if (SDL_Init(SDL_INIT_VIDEO))
		{
			fprintf(stderr, "Could not initialize SDL - %s\n", SDL_GetError());
			exit(1);
		}

		display.screen = SDL_CreateWindow(name, SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED,
										  DISPLAY_W, DISPLAY_H, SDL_WINDOW_SHOWN);
		if (!display.screen)
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

	if (display.textures[id] == NULL)
	{

		display.textures[id] = SDL_CreateTexture(display.renderer,
												 SDL_PIXELFORMAT_RGB888,
												 SDL_TEXTUREACCESS_STREAMING,
												 width, height);

		if (!display.textures[id])
		{
			fprintf(stderr, "SDL: could not create texture - exiting\n");
			exit(1);
		}

		display.currentXMin += width;
	}

	if (display.surface[id] == NULL){
		display.surface[id] = SDL_CreateRGBSurface(0, width, height, 32,0,0,0,0);
		if (!display.surface[id])
		{
			fprintf(stderr, "SDL: could not create surface - exiting\n");
			exit(1);
		}
	}

}

void displayLum(int id, unsigned char *lum){
	int idxPxl,w,h;
	SDL_Texture* texture = display.textures[id];
	SDL_Surface *surface = display.surface[id];

	// Prepare RGB texture
	// Retrieve texture attribute
	SDL_QueryTexture(texture, NULL, NULL, &w, &h);

	for (idxPxl = 0; idxPxl < h*w; idxPxl++){
		*(((char*)(surface->pixels)) + 4 * idxPxl)	   = *(lum + idxPxl);
		*(((char*)(surface->pixels)) + 4 * idxPxl + 1) = *(lum + idxPxl);
		*(((char*)(surface->pixels)) + 4 * idxPxl + 2) = *(lum + idxPxl);
	}

	refreshDisplayRGB(id);
}

void displayRGB(int id, int height, int width, unsigned char *rgb){

	int idxPxl, w, h;
	SDL_Texture* texture = display.textures[id];
	SDL_Surface *surface = display.surface[id];

	// Prepare RGB texture
	// Retrieve texture attribute
	SDL_QueryTexture(texture, NULL, NULL, &w, &h);

	for (idxPxl = 0; idxPxl < h*w; idxPxl++){
		*(((char*)(surface->pixels)) + 4 * idxPxl + 0) = *(rgb + 3 * idxPxl + 2);
		*(((char*)(surface->pixels)) + 4 * idxPxl + 1) = *(rgb + 3 * idxPxl + 1);
		*(((char*)(surface->pixels)) + 4 * idxPxl + 2) = *(rgb + 3 * idxPxl + 0);
	}

	refreshDisplayRGB(id);
}


void refreshDisplayRGB(int id)
{
	SDL_Texture* texture = display.textures[id];
	SDL_Event event;
	SDL_Rect screen_rect;
	
	SDL_QueryTexture(texture, NULL, NULL, &(screen_rect.w), &(screen_rect.h));

	SDL_UpdateTexture(texture, NULL, display.surface[id]->pixels, screen_rect.w*4);

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

void finalizeRGB(int id)
{
	SDL_FreeSurface(display.surface[id]);
	SDL_DestroyTexture(display.textures[id]);
	SDL_DestroyRenderer(display.renderer);
	SDL_DestroyWindow(display.screen);
}
