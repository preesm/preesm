#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#ifdef __k1__
#include <HAL/hal/hal_ext.h>
#include <utask.h>
#endif
//#include "SDL.h"
#include "ppm.h"
#include "BP1D.h"
//#include "displayRGB.h"
#include "define.h"
#include <time.h>

void attendreTouche(void);


#if 0
unsigned char r_imgL[WIDTH * HEIGHT];
unsigned char g_imgL[WIDTH * HEIGHT];
unsigned char b_imgL[WIDTH * HEIGHT];
unsigned char r_imgR[WIDTH * HEIGHT];
unsigned char g_imgR[WIDTH * HEIGHT];
unsigned char b_imgR[WIDTH * HEIGHT];
unsigned char out_disparity[WIDTH * HEIGHT];
#endif

unsigned char *r_imgL;
unsigned char *g_imgL;
unsigned char *b_imgL;
unsigned char *r_imgR;
unsigned char *g_imgR;
unsigned char *b_imgR;
unsigned char *out_disparity;

int ref_bp1d(void)
{
	r_imgL = (unsigned char*) malloc(sizeof(*r_imgL)*WIDTH*HEIGHT);
	g_imgL = (unsigned char*) malloc(sizeof(*r_imgL)*WIDTH*HEIGHT);
	b_imgL = (unsigned char*) malloc(sizeof(*r_imgL)*WIDTH*HEIGHT);
	r_imgR = (unsigned char*) malloc(sizeof(*r_imgL)*WIDTH*HEIGHT);
	g_imgR = (unsigned char*) malloc(sizeof(*r_imgL)*WIDTH*HEIGHT);
	b_imgR = (unsigned char*) malloc(sizeof(*r_imgL)*WIDTH*HEIGHT);
	out_disparity = (unsigned char*) malloc(sizeof(*r_imgL)*WIDTH*HEIGHT);

	int i, j;
	char* outPath;

	/* ------ paths[left image , right image] ----- */
	char* paths[] = { "images/stereo_pairs/Teddy_L.ppm", "images/stereo_pairs/Teddy_R.ppm" };
	//char* paths[] = { "../images/stereo_pairs/Tsukuba_L.ppm", "../images/stereo_pairs/Tsukuba_R.ppm" };
	//char* paths[] = { "../images/stereo_pairs/Cones_L.ppm", "../images/stereo_pairs/Cones_R.ppm" };
	
	
	// Initialisation(s)
	readPPMInit(0/*id*/, HEIGHT, WIDTH, paths); // Read_PPM0
	readPPMInit(1/*id*/, HEIGHT, WIDTH, paths); // Read_PPM1
	//displayRGBInit(0/*index*/, HEIGHT, WIDTH); // Display_rgb0
	//displayRGBInit(1/*index*/, HEIGHT, WIDTH); // Display_rgb2

	readPPM(0/*id*/, HEIGHT, WIDTH, r_imgL, g_imgL, b_imgL); // Read_PPM0
	readPPM(1/*id*/, HEIGHT, WIDTH, r_imgR, g_imgR, b_imgR); // Read_PPM1

	//displayRGB(0, &r_imgL, &g_imgL, &b_imgL);
	for (i = 0; i < 1;i++)
	{
		#ifdef __k1__
		uint64_t start_t = __k1_read_dsu_timestamp();
		#endif
		BP1D_image(r_imgL, g_imgL, b_imgL, r_imgR, g_imgR, b_imgR, out_disparity);
		#ifdef __k1__
		uint64_t end_t = __k1_read_dsu_timestamp();
		#define CHIP_FREQ ((float)__bsp_frequency/1000.0f)
		float total_ms = (float)(end_t - start_t)/CHIP_FREQ;
		printf("IO%d Pref %.2f FPS %.2f ms\n", __k1_get_cluster_id()/192,  1/(total_ms/1000.0f), total_ms );
		#endif
	}

	//displayLum(1, &out_disparity);

	outPath = "images/Teddy_depth_map.ppm";
	writePPM(HEIGHT, WIDTH, out_disparity, outPath);

	/*Test de non modification !! */
	int test=0;
	for (j = 0; j < HEIGHT; j++)
	{
		for (i = 0; i < WIDTH; i++)
		{
			test += out_disparity[j*WIDTH + i];
		}
	}

	int should_be = 16842408;
	//if (test != 16842408){
	if (test != should_be){
		printf("ERREUR !!!!!!!! test %d should_be %d diff %d\n", test, should_be, abs(test-should_be));
	}else{
		printf("GOOD !\n");
	}
	/*------------------------------*/
	return EXIT_SUCCESS;
}
 
 
//void attendreTouche(void)
//{
//  SDL_Event event;
// 
//  do
//    SDL_WaitEvent(&event);
//  while (event.type != SDL_QUIT && event.type != SDL_KEYDOWN);
//}
