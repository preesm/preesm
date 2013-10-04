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


/*
 * ------------- Communication with BIOS core -------------
*/

void communicationInit();

/**
 * Non-blocking function called by the sender to signal that a buffer is ready
 * to be sent.
 *
 * @param[in] coreID
 *        the ID of the receiver core
 */
void sendStart(Uint16 coreID);

/**
 * Blocking function (not for shared_mem communication) called by the sender to
 * signal that communication is completed.
 */
void sendEnd();

/**
 * Non-blocking function called by the receiver begin receiving the
 * data. (not implemented with shared memory communications).
 */
void receiveStart();

/**
 * Blocking function called by the sender to wait for the received data
 * availability.
 *
 * @param[in] coreID
 *        the ID of the sender core
 */
void receiveEnd(Uint16 coreID);

#endif /* COM_H_ */
