/*
 ============================================================================
 Name        : census.h
 Author      : kdesnos
 Author      : JZHAHG
 Version     : 1.0
 Copyright   : CeCILL-C, IETR, INSA Rennes
 Description : Computation of the census corresponding to an input gray image
 ============================================================================
 */

#ifndef CENSUS_H
#define CENSUS_H

/**
 * This method associates a signature to each pixel of an image. This signature
 * is an 8-bit word obtained by comparing each pixel value with the neighbor
 * pixels values. A bit is set to 1 if the compared pixel is inferior to the
 * current.
 * Example:
 * |20|21|13|
 * |15|18|19|  => 0x35 (00110101)
 * |13|18|16|
 *
 * @param height
 *        The height of the processed image
 * @param width
 *        The width of the processed image
 * @param gray
 *        The input gray image whose census is computed.
 * @param cen
 *        The outputed height*width census image.
 */
void census(int height, int width, float *gray, unsigned char *cen);

#endif
