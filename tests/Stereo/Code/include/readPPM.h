/*
	============================================================================
	Name        : readPPM.h
	Author      : kdesnos & mpelcat
	Version     : 1.0
	Copyright   : CECILL-C
	Description : Actor code to read a file from the hard drive
	============================================================================
*/

#ifndef READ_PPM_H
#define READ_PPM_H



//#define PATH "D:/Temp/BigBuckBunny_1920_1080_24fps.yuv"


void readPPMInit(int id, int height, int width);

void readPPM(int id,int height, int width, unsigned char *r, unsigned char *g, unsigned char *b);

#endif
