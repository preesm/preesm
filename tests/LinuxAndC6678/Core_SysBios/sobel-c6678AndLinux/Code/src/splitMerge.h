/*
	============================================================================
	Name        : splitMerge.h
	Author      : kdesnos
	Version     : 1.0
	Copyright   : CECILL-C
	Description : Function used to split an image into overlapping slices and
                  merge them back together.
	============================================================================
*/

#ifndef SPLIT_MERGE_H
#define SPLIT_MERGE_H

/**
* Function used to split an input image of size xSize*ySize into nbSlices 
* slices of size xSize*(ySize/nbSlice+2). It is the developper responsibility
* to ensure that ySize is a multiple of nbSlice.
*
* @param nbSlice
*        the number of slices produced
* @param xSize
*        the width of the input image
* @param ySize
*        the height of the input image
* @param input
*        the input image of size xSize*ySize
* @param output
*        the output buffer of size nbSlice*[xSize*(ySize/nbSlice+2)]
*/
void split(int nbSlice, int xSize, int ySize, unsigned char *input, unsigned char *output);

/**
* Function used to assemble nbSlices slices of size xSize*(ySize/nbSlice+2) 
* into an output image of size xSize*ySize.
*
* @param nbSlice
*        the number of slices assembled
* @param xSize
*        the width of the output image
* @param ySize
*        the height of the output image
* @param input
*        the input image slices
* @param output
*        the output image of size xSize*Size
*/
void merge(int nbSlice, int xSize, int ySize, unsigned char *input, unsigned char *output);

#endif
