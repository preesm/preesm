/*
 ============================================================================
 Name        : computeWeights.h
 Author      : kdesnos
 Author      : JZHAHG
 Version     : 1.0
 Copyright   : CeCILL-C, IETR, INSA Rennes
 Description : Computation of the weights associated to the pixel of an rgb
 image.
 ============================================================================
 */

#ifndef COMPUTE_WEIGHTS_H
#define COMPUTE_WEIGHTS_H

/**
 * This function computes 3 weights for each pixel of the input rgb image.
 * Each weight is computed as a function of the pixel RGB value as well as
 * the rgb value of two neighbor pixel.
 *
 * @param height
 *        The height of the processed image
 * @param width
 *        The width of the processed image
 * @param horOrVert
 *        This integer is used to indicate if the weight are computed with
 *        horizontal (=0) or vertical (=1) offsets.
 * @param offset
 *        The distance of the neighbor pixels used to compute the 3 weights
 *        associated to a given pixel is specified by this parameter.
 * @param rgbL
 *        The input rgb image. The image is stored as follows:
 *        height*width values for the r component then height*width values for
 *        the g component, then height*width values for the b component.
 *        associated to a given pixel is specified by this parameter.
 * @param weights
 *        The outputed 3*height*width weights.
 */
void computeWeights(int height, int width, int horOrVert, int *offset,
		unsigned char *rgbL, float *weights);

#endif
