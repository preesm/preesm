/*******************************************************************************
 * Copyright or © or Copr. %%LOWERDATE%% - %%UPPERDATE%% IETR/INSA:
 *
 * %%AUTHORS%%
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/

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
