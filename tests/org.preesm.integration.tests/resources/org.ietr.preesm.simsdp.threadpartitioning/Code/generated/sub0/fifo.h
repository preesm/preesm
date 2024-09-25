/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2019) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Julien Hascoet [jhascoet@kalray.eu] (2017)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
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
 * knowledge of the CeCILL license and that you accept its terms.
 */
/*
 ============================================================================
 Name        : fifo.h
 Author      : kdesnos
 Version     : 1.0
 Copyright   : CECILL-C
 Description : FIFO primitive for Preesm Codegen.
 Currently, primitives were tested only for x86 with shared_mem.
 ============================================================================
 */

#ifndef _PREESM_FIFO_H
#define _PREESM_FIFO_H

#ifdef __cplusplus
extern "C" {
#endif

/**
 * Initialize a FIFO by filling its memory with 0.
 *
 * @param headBuffer
 *        pointer to the memory space containing the first element of the fifo.
 * @param headSize
 *        Size of the first element of the fifo (>0)
 * @param bodyBuffer
 *        pointer to the memory space containing all but the first element of
 *        the fifo.
 * @param bodySize
 *        Size of the body of the fifo (>=0)
 */
void fifoInit(void *headBuffer, int headSize, void *bodyBuffer, int bodySize);

/**
 * Push a new element in the FIFO from an input buffer.
 *
 * @param inputBuffer
 *        pointer to the data pushed in the fifo.
 * @param headBuffer
 *        pointer to the memory space containing the first element of the fifo.
 * @param headSize
 *        Size of the pushed data and of the first element of the fifo (>0)
 * @param bodyBuffer
 *        pointer to the memory space containing all but the first element of
 *        the fifo.
 * @param bodySize
 *        Size of the body of the fifo (>=0)
 */
void fifoPush(void *inputBuffer, void *headBuffer, int headSize, void *bodyBuffer, int bodySize);

/**
 * Pop the head element from the FIFO to an output buffer.
 *
 * @param outputBuffer
 *        pointer to the destination of the popped data.
 * @param headBuffer
 *        pointer to the memory space containing the first element of the fifo.
 * @param headSize
 *        Size of the popped data and of the first element of the fifo (>0)
 * @param bodyBuffer
 *        pointer to the memory space containing all but the first element of
 *        the fifo.
 * @param bodySize
 *        Size of the body of the fifo (>=0)
 */
void fifoPop(void *outputBuffer, void *headBuffer, int headSize, void *bodyBuffer, int bodySize);

#ifdef __cplusplus
}
#endif

#endif
