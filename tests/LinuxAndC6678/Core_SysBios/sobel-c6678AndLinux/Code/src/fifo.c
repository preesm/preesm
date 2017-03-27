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
#include <string.h>
#include "fifo.h"

void fifoInit(void* headBuffer, int headSize, void* bodyBuffer, int bodySize) {
    memset(headBuffer,0,headSize);
    if(bodyBuffer != NULL) {
        memset(bodyBuffer,0,bodySize);
    }
}


void fifoPush(void * inputBuffer, void* headBuffer, int headSize, void* bodyBuffer, int bodySize){
    if(bodyBuffer != NULL){
        memcpy(headBuffer,bodyBuffer,headSize);
        memcpy(bodyBuffer, (char *)bodyBuffer+headSize, bodySize-headSize);
        memcpy((char *)bodyBuffer+bodySize-headSize,  inputBuffer, headSize);
    } else {
        memcpy(headBuffer,inputBuffer,headSize);
    }
    //memcpy(headBuffer, headBuffer+outputSize, fifoSize-outputSize);
    //memset(headBuffer+fifoSize-outputSize, 0, outputSize); // Useless
}

void fifoPop(void * outputBuffer, void* headBuffer, int headSize, void* bodyBuffer, int bodySize){
    memcpy(outputBuffer, headBuffer, headSize);
}
