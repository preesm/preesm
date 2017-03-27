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
/*
 * com.h
 *
 *  Created on: Sept 10, 2013
 *      Author: rparois
 */

#ifndef COM_H_
#define COM_H_

/* Standard headers */
#include <xdc/std.h>

/* Module level headers */
#include <ti/ipc/MultiProc.h>
#include <ti/ipc/HeapMemMP.h>
#include <ti/ipc/SharedRegion.h>
#include <ti/ipc/MessageQ.h>

/* Interrupt line used (0 is default) */
#define INTERRUPT_LINE  0

/* Notify event number that the app uses */
#define EVENTID         10

/*
 * ------------- Communication with linux core -------------
 * */

/*
 * Open the heap created by the other processor. Loop until opened.
 * It also registers this heap with MessageQ.
 */
HeapMemMP_Handle openHeap(const char* heapName);

/*
 * Close the heap.
 */
void closeHeap(HeapMemMP_Handle heapHandle);

/*
 * Create a local queue to receive data.
 */
MessageQ_Handle createQueue(const char* name);

/*
 * Delete the messageQ.
 */
void deleteQueue(MessageQ_Handle hndl);

/*
 * Open the remote queue to send data.
 */
MessageQ_QueueId openQueue(const char* name);

/*
 * Close the remote queue.
 */
void closeQueue(MessageQ_QueueId id);

/*
 * Send a data on a single identified remote queue.
 */
void sendQ(MessageQ_QueueId id, void* data, int size);

/*
 * Receive a data from a local queue.
 */
void recvQ(MessageQ_Handle hndl, void* data, int size);

#endif /* COM_H_ */
