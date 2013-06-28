/*
	============================================================================
	Name        : readYUV.h
	Author      : kdesnos
	Version     :
	Copyright   :
	Description :
	============================================================================
*/

#ifndef READ_YUV_H
#define READ_YUV_H

#define PATH "./akiyo_cif.yuv"
#define NB_FRAME 300

void initReadYUV(int xSize, int ySize);
void readYUV(int xSize, int ySize, unsigned char *y, unsigned char *u, unsigned char *v);

#endif
