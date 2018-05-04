//
// Created by farresti on 31/10/17.
//

// std libs
#include <stdlib.h>
#include <stdio.h>
#include <time.h>
// SDL libs
#include <SDL.h>
#include <SDL_image.h>
#include <SDL_ttf.h>
#include <zconf.h>
#include <float.h>
// timing lib
#include "../include/clock.h"
// file header
#include "../include/render.h"

// file defines
#define FPS_MEAN 50
#define PI 3.1415926535898f

extern int stopThreads;

static int render = 0;

int exitCallBack(void* userdata, SDL_Event* event){
    if (event->type == SDL_QUIT) {
        fprintf(stderr, "What action do you wish to perform ?\n");
        fprintf(stderr, "\t- Quit (q)\n");
        fprintf(stderr, "\t- Toggle rendering (r)\n");
        fprintf(stderr, "answer: \n");
        char ans = (char)getchar();
        // Clear stdin buffer
        int c;
        while ((c = getchar()) != '\n' && c != EOF);
        if (ans == 'r') {
            render ^= 1;
        } else if (ans == 'q') {
            printf("Exit request from GUI.\n");
            stopThreads = 1;
            return 0;
        }
        return 1;
    }

    return 1;
}

typedef struct sdlDisplay {
    SDL_Renderer *renderer;
    SDL_Window *screen;
    SDL_Texture *texture;
    TTF_Font *font;
    int stampId;
}sdlDisplay;

static sdlDisplay display;

void renderInit(void) {
    // Initializes random module
    srand((unsigned int)time(NULL));

    display.screen = NULL;
    display.renderer = NULL;

    // Initialize SDL
    fprintf(stderr, "SDL_Init_Start\n");
    if (SDL_Init(SDL_INIT_VIDEO)) {
        fprintf(stderr, "Could not initialize SDL - %s\n", SDL_GetError());
        exit(1);
    }
    fprintf(stderr, "SDL_Init_End\n");


    // Initialize SDL TTF for text display
    fprintf(stderr, "SDL_TTF_Init_Start\n");
    if (TTF_Init()) {
        printf("TTF initialization failed: %s\n", TTF_GetError());
    }
    fprintf(stderr, "SDL_TTF_Init_End\n");

    // Open Font for text display
    display.font = TTF_OpenFont(PATH_TTF, 20);
    if (!display.font) {
        printf("TTF_OpenFont: %s\n", TTF_GetError());
    }

    // Create window
    display.screen = SDL_CreateWindow("Environment_Display", SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED,
                     DISPLAY_W, DISPLAY_H, SDL_WINDOW_SHOWN);
    if (!display.screen) {
        fprintf(stderr, "SDL: could not set video mode - exiting\n");
        exit(1);
    }
    // Create renderer
    display.renderer = SDL_CreateRenderer(display.screen, -1, SDL_RENDERER_ACCELERATED);
    if (!display.renderer) {
        fprintf(stderr, "SDL: could not create renderer - exiting\n");
        exit(1);
    }
    SDL_Surface *surface = IMG_Load(PATH);

    if(!surface) {
        fprintf(stderr, "IMG_Load: %s\n", IMG_GetError());
        exit(1);
    }

    display.texture = SDL_CreateTextureFromSurface(display.renderer,
                                                   surface);
    if (!display.texture) {
        fprintf(stderr, "SDL: could not create texture - exiting\n");
        exit(1);
    }

    display.stampId = 0;
    for (int i = 0; i < FPS_MEAN; ++i) {
        startTiming(i + 1);
    }

    fprintf(stderr, "register exit callback\n");
    SDL_SetEventFilter(exitCallBack, NULL);
}

void renderEnv(int size, float *state) {
    static long int i = 0;
    static double max_fps = 0.;
    static double avg_fps = 0.;
    static double min_fps = DBL_MAX;
    SDL_Event event;
    // Grab all the events off the queue.
    while (SDL_PollEvent(&event)) {
        switch (event.type)
        {
            default:
                break;
        }
    }

    // Compute FPS
    char stringFPS[20];
    int timeStamp = stopTiming(display.stampId + 1);
    double fps = 1. / (timeStamp / 1000000. / FPS_MEAN);
    sprintf(stringFPS, "%.2f fps", fps);
    startTiming(display.stampId + 1);
    display.stampId = (display.stampId + 1) % FPS_MEAN;

    if (fps > max_fps) {
        max_fps = fps;
    }
    if (fps < min_fps && fps > 0.) {
        min_fps = fps;
    }
    avg_fps += fps;
//    if (i % 10000 == 0) {
//        avg_fps /= 10000.;
//        fprintf(stderr, "Avg FPS: %.2f\n", avg_fps);
//        fprintf(stderr, "Min FPS: %.2f\n", min_fps);
//        fprintf(stderr, "Max FPS: %.2f\n", max_fps);
//        avg_fps = 0.;
//        min_fps = DBL_MAX;
//        max_fps = 0.;
//    }
    ++i;

    if (!render) {
        return;
    }

    /* Select the color for drawing. It is set to red here. */
    SDL_SetRenderDrawColor(display.renderer, 255, 255, 255, 255);
    /* Clear the entire screen to our selected color. */
    SDL_RenderClear(display.renderer);

    // Position of the pendulum in the window
    SDL_Rect dest = {225, 250, 49, 234};
    SDL_Point center = {25, 15};
    // Convert the angle to degree with the offset to match the python training
    float angle = 180.f - state[0] * 180.f / (PI);
    // Display the pendulum
    SDL_RenderCopyEx(display.renderer, display.texture, NULL, &dest, angle, &center, SDL_FLIP_NONE);

    // Print FPS text
    SDL_Color colorGreen = {0, 255, 0, 255};
    SDL_Surface* surfaceFPS = TTF_RenderText_Blended(display.font, stringFPS, colorGreen);
    SDL_Texture* textureFPS = SDL_CreateTextureFromSurface(display.renderer, surfaceFPS);

    int widthFPS, heightFPS;
    SDL_QueryTexture(textureFPS, NULL, NULL, &widthFPS, &heightFPS);
    SDL_Rect rectFPSText = {0, 0, widthFPS, heightFPS};
    SDL_RenderCopy(display.renderer, textureFPS, NULL, &rectFPSText);

    // Free resources
    SDL_FreeSurface(surfaceFPS);
    SDL_DestroyTexture(textureFPS);

    // Proceed to the actual display
    SDL_RenderPresent(display.renderer);

    // Sleep to smooth the rendering
//    usleep(500000);
}

void renderFinalize(void)
{
    SDL_DestroyTexture(display.texture);
    SDL_DestroyRenderer(display.renderer);
    SDL_DestroyWindow(display.screen);
}
