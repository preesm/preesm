/*
 ============================================================================
 Name        : offsetGen.h
 Author      : kdesnos
 Version     : 1.0
 Copyright   : CeCILL-C, IETR, INSA Rennes
 Description : Generation of an array of the offsets used in the
 computation of the depth map.
 ============================================================================
 */

#ifndef OFFSET_GEN_H
#define OFFSET_GEN_H

/**
 * Generation of an array of the offsets used in the computation
 * of the depth map.
 *
 * @param nbIterations
 *        The number of offset to generate.
 * @param disparities
 *        Output array for the generated offsets.
 */
void offsetGen(int nbIterations, int *offsets);

#endif
