/*
 ============================================================================
 Name        : aggregateCost.h
 Author      : kdesnos
 Author      : JZHAHG
 Version     : 1.0
 Copyright   : CeCILL-C, IETR, INSA Rennes
 Description : Aggregate the horizontal and vertical disparity error for
 several offsets.
 ============================================================================
 */

#ifndef AGGREGATE_COST_H
#define AGGREGATE_COST_H

/**
 * This function perform both the horizontal and vertical cost aggregation
 * for all pixels of the input disparity error map. The horizontal and vertical
 * cost aggregation is iterated as many times as there are input offsets.
 * Aggregating a pixel consists of updating its value as a function of neighbor
 * pixels values and associated pre-computed weights.
 *
 * @param height
 *        The height of the processed stereo pair
 * @param width
 *        The width of the processed stereo pair
 * @param nbIterations
 *        The number of offsets that given to the function
 * @param disparityError
 *        The input cost map of size height*width. This array is used to store
 *        intermediary results during the function execution.
 * @param offsets
 *        For each of these nbIterations offset, a horizontal and a vertical
 *        cost aggregation is performed.
 * @param hWeights
 *        weights used during the horizontal cost aggregations. There are
 *        height*width*3*nbIterations weights in this array: three for each
 *        pair of pixel and offset.
 * @param vWeights
 *        weights used during the vertical cost aggregations. There are
 *        height*width*3*nbIterations weights in this array: three for each
 *        pair of pixel and offset.
 * @param aggregatedDisparity
 *        The output cost map of size height*width
 */
void aggregateCost(int height, int width, int nbIterations,
		float *disparityError, int *offsets, float *hWeights, float *vWeights,
		float *aggregatedDisparity);

#endif
