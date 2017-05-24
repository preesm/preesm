/*
	============================================================================
	Name        : morph.h
	Author      : kdesnos
	Version     : 1.0
	Copyright   : CeCILL-C, IETR, INSA Rennes
	Description : 
	============================================================================
*/

#ifndef MORPH_H
#define MORPH_H
#include "preesm.h"

#if 1
void dilation (int height , int width, 
			   const int window,
			   IN unsigned char *input,
			   OUT unsigned char *output);

			   
void erosion (int height , int width, 
			   const int window,
			   IN unsigned char *input,
			   OUT unsigned char *output);
#endif

#if 0
void dilation (int height , int width,
                           const int window,
                           int outHeight,
                           IN unsigned char *input,
                           OUT unsigned char *output);


void erosion (int height , int width,
                           const int window,
                            int outHeight,
                           IN unsigned char *input,
                           OUT unsigned char *output);
#endif

#endif
