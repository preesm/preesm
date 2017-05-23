/*
 ============================================================================
 Name        : communication.h
 Author      : kdesnos
 Version     : 1.0
 Copyright   : CeCILL-C, IETR, INSA Rennes
 Description : Communication primitives for Preesm generated C6X code.
 ============================================================================
 */

#ifndef COMMUNICATION_H
#define COMMUNICATION_H
#include <xdc/std.h>

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

/**
 * Barrier used to synchronize all the 8 cores of the DSP.
 * The communication must be initialized in order to use this method.
 */
void busy_barrier();

#endif
