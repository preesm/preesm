/*
	============================================================================
	Name        : costConstruction.h
	Author      : kdesnos
	Author      : JZHAHG
	Version     : 1.0
	Copyright   : CeCILL-C, IETR, INSA Rennes
	Description : Computation of the costs associated to the pixels of the 
	              stereo pair for a given disparity
	============================================================================
*/

#ifndef COST_CONSTRUCTION_H
#define COST_CONSTRUCTION_H
#include "preesm.h"

/**
* This function computes the costs associated to the pixels of a stereo pair
* for a given disparity. Each cost is obtained as a function of a pixel of the
* left and a pixel of the right image. The two pixels belong to the same line
* of pixels in the matched stereo pair.
*
* @param height
*        The height of the processed stereo pair.
* @param width
*        The width of the processed stereo pair
* @param truncValue
*        If the absolute difference between the values of the two pixels
*        is superior to this value, this value will be used instead in the
*        cost computation.
* @param disparity
*        The difference between the position of the two pixels in the line
*        of their image.
* @param grayL
*        The height*width pixels of the left image of the matched stereo pair.
* @param grayR
*        The height*width pixels of the right image of the matched stereo pair.
* @param cenL
*        The census image corresponding to the left image of the matched stereo
*        pair. (@see census())
* @param cenR
*        The census image corresponding to the right image of the matched 
*        stereo pair. (@see census())
* @param disparityError
*        The outputed height*width costs.
*/
void costConstruction (int height, int width, float truncValue,
                       IN unsigned char *disparity,
                       IN float *grayL, IN float *grayR,
                       IN unsigned char *cenL, IN unsigned char *cenR,
                       OUT float *disparityError,
                       IN unsigned char* back);

/**
* This function computes the hamming cost between two 8-bit words.
* The Hamming cost is the number of bit that are not identical in the two
* words. For example: a:=1001 0111 and b:= 1011 0101 => cost:= 2.
*
* @param a
*        The first 8-bit word.
* @param b
*        The second 8-bit word.
* @return the hamming cost between the two words.
*/
unsigned char hammingCost(unsigned char *a, unsigned char *b);

#endif
