/*
   ============================================================================
Name        : ppm.c
Author      : kdesnos
Version     : 1.0
Copyright   : CECILL-C, IETR, INSA Rennes
Description : Actor code to read/write a ppm file from the hard drive
============================================================================
 */

#include "ppm.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>
#include <assert.h>

/*========================================================================

  Global Variable

  ======================================================================*/
#define NB_PATH 2
//char* paths[] = {"../src/im0.ppm","../src/im1.ppm"};
char* paths[] = { "../images/stereo_pairs/Teddy_L.ppm", "../images/stereo_pairs/Teddy_R.ppm" };
char outputPath[] = { "../images/Teddy_depth_map.ppm" };
//char* outPath = "../src/disparity_map.ppm";
static FILE * ptfile[NB_PATH];

long imageStartPosition[NB_PATH];

void writePPM(int height, int width, unsigned char *gray){
	#if 1
	FILE * outFile;
	int i;
	if((outFile = fopen(outputPath, "wb+")) == NULL )
	{
		fprintf(stderr,"ERROR: Task read cannot create/open ppm_file '%s'\n", outputPath);
		system("PAUSE");
		return;
	}

	fprintf(outFile,"P6\n");
	fprintf(outFile,"%d %d\n",width,height);
	fprintf(outFile,"255\n");
	for(i=0; i<height*width;i++){
		fwrite(gray+i,sizeof(char),1,outFile);
		fwrite(gray+i,sizeof(char),1,outFile);
		fwrite(gray+i,sizeof(char),1,outFile);
	}

	fclose(outFile);
	//printf("doone\n");
	//while(1);
	#endif
	#if 0
	int fd = open(outputPath, O_RDWR | O_CREAT, S_IWUSR | S_IRUSR);
	char header[50];
	fprintf(header, "P6\n");
	write(fd, header, strlen(header)); 
	fprintf(header, "%d %d\n", width, height);
	write(fd, header, strlen(header));
	fprintf(header, "255\n");
	write(fd, header, strlen(header)); 
	#endif
}

void readPPMInit(int id, int height, int width, char* paths[]) {
	char magicNumber[3];
	int readWidth;
	int readHeight;
	int maxRGBValue;
	int fsize __attribute__((unused));

	printf("readPPMInit()\n");
	if((ptfile[id] = fopen(paths[id], "rb")) == NULL )
	{
		fprintf(stderr,"ERROR: Task read cannot open ppm_file '%s'\n", paths[id]);
		system("PAUSE");
		return;
	}

	// Read ppm file header
	// 1. Magic Numper
	fread(magicNumber, sizeof(char),2, ptfile[id]);
	magicNumber[2] = '\0';
	if(strcmp(magicNumber,"P6")){
		fprintf(stderr,"ERROR: PPM_file '%s' is not a valid PPM file.\n", paths[id]);
		system("PAUSE");
		return;
	}
	fseek(ptfile[id],1,SEEK_CUR); // skip space or EOL character


	// 2. Width and Height
	fscanf(ptfile[id],"%d", &readWidth);
	fscanf(ptfile[id],"%d", &readHeight);

	fseek(ptfile[id],1,SEEK_CUR); // skip space or EOL character

	// 3. Max RGB value
	fscanf(ptfile[id],"%d", &maxRGBValue);
	if(maxRGBValue > 255){
		fprintf(stderr,"ERROR: PPM_file '%s' has is coded with 32bits values, 8bits values are expected.\n", paths[id]);
		system("PAUSE");
		return;
	}
	fseek(ptfile[id],1,SEEK_CUR); // skip space or EOL character

	// Register the position of the file pointer
	imageStartPosition[id] = ftell(ptfile[id]);

	// check file size:
	fseek (ptfile[id] , 0 , SEEK_END);
	fsize = ftell (ptfile[id]) - imageStartPosition[id];
	fseek(ptfile[id],imageStartPosition[id], SEEK_SET);

	/*if(fsize != height*width*3)
	  {
	  fprintf(stderr,"ERROR: PPM_file has incorrect data size.\n\nExpected: %d\t Read: %d\n",height*width*3, fsize);
	  system("PAUSE");
	  return;
	  }*/

	// Set initial clock
}

#ifdef __k1__
#include <stdint.h>
#include <mOS_vcore_u.h>
#endif

//void readPPM(int id,int height, int width, unsigned char *r, unsigned char *g, unsigned char *b){
void readPPM(int height, int width, unsigned char *rgbLeft, unsigned char *rgbRight){

	#ifdef __k1__
	uint64_t s = __k1_read_dsu_timestamp();
	#endif
	char magicNumber[3];
	int readWidth;
	int readHeight;
	int maxRGBValue;
	int fsize __attribute__((unused));
	int id;

	for(id=0; id<NB_PATH; id++){
		if((ptfile[id] = fopen(paths[id], "rb")) == NULL )
		{
			fprintf(stderr,"ERROR: Task read cannot open ppm_file '%s'\n", paths[id]);
			system("PAUSE");
			return;
		}

		// Read ppm file header
		// 1. Magic Numper
		fread(magicNumber, sizeof(char),2, ptfile[id]);
		magicNumber[2] = '\0';
		if(strcmp(magicNumber,"P6")){
			fprintf(stderr,"ERROR: PPM_file '%s' is not a valid PPM file.\n", paths[id]);
			system("PAUSE");
			return;
		}
		fseek(ptfile[id],1,SEEK_CUR); // skip space or EOL character

		// 2. Width and Height
		fscanf(ptfile[id],"%d", &readWidth);
		fscanf(ptfile[id],"%d", &readHeight);
		fseek(ptfile[id],1,SEEK_CUR); // skip space or EOL character

		// 3. Max RGB value
		fscanf(ptfile[id],"%d", &maxRGBValue);
		if(maxRGBValue > 255){
			fprintf(stderr,"ERROR: PPM_file '%s' has is coded with 32bits values, 8bits values are expected.\n", paths[id]);
			system("PAUSE");
			return;
		}
		fseek(ptfile[id],1,SEEK_CUR); // skip space or EOL character

		// Register the position of the file pointer
		imageStartPosition[id] = ftell(ptfile[id]);

		// check file size:
		fseek (ptfile[id] , 0 , SEEK_END);
		fsize = ftell (ptfile[id]) - imageStartPosition[id];
		fseek(ptfile[id],imageStartPosition[id], SEEK_SET);

		fseek(ptfile[id], imageStartPosition[id], SEEK_SET);
	}

	// read buffer
	fread(rgbLeft, sizeof(char), 3*width*height, ptfile[0]);
	fread(rgbRight, sizeof(char), 3*width*height, ptfile[1]);

	assert(NB_PATH == 2 && "Please check NB_PATH");

	//for(idxPxl = 0; idxPxl < 3*height*width; idxPxl++){
	//    switch(rgb_val){
	//    case 0:
	//        *(r+idxPxl/3) = *(readBuffer+idxPxl);
	//        break;
	//    case 1:
	//        *(g+idxPxl/3) = *(readBuffer+idxPxl);
	//        break;
	//    case 2:
	//        *(b+idxPxl/3) = *(readBuffer+idxPxl);
	//        break;
	//    }
	//    rgb_val = (rgb_val + 1)%3;
	//}
	#ifdef __k1__
	printf("read ms %.2f\n", ((float)(__k1_read_dsu_timestamp()-s)) / ((float)__bsp_frequency/1000));
	#endif
}

