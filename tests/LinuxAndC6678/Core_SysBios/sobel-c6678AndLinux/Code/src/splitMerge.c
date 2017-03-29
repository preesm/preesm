/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
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

#include <string.h>

#include "splitMerge.h"
#include <xdc/runtime/System.h>

void split(int nbSlice, int xSize, int ySize, unsigned char *input, unsigned char *output){
    int i;
    int sliceSize =  xSize*ySize/nbSlice;

    // Fill first and last line with 0
    memset(output,0,xSize);
    // First Slice
    memcpy(output+xSize, input, sliceSize);
    // Copy next line if several slice
    if (nbSlice > 1){
        memcpy(output +  xSize + sliceSize , input + sliceSize, xSize);
    }
    // Slice other than first and last
    for(i=1; i<nbSlice-1; i++){
        int destIndex = i*(sliceSize+2*xSize);
        memcpy(output + destIndex, input+i*sliceSize-xSize, sliceSize+2*xSize);
    }
    // Last Slice
    i = nbSlice-1;
    if(nbSlice > 1){
        // we have i = nbSlice -1;
        int destIndex = i*(sliceSize+2*xSize);
        memcpy(output + destIndex, input+i*sliceSize-xSize, sliceSize+xSize);
    }
    // Last line
    memset(output + (ySize+nbSlice*2-1)*xSize,0,xSize);
}


void merge(int nbSlice, int xSize, int ySize, unsigned char *input, unsigned char *output){
    int i;
    int sliceSize =  xSize*ySize/nbSlice;

    // Copy the slice content except the first and last lines
    for(i = 0; i< nbSlice; i++){
        int idx = i*(sliceSize+2*xSize);
        memcpy(output+i*sliceSize, input+idx+xSize, sliceSize);
    }
}

