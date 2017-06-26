/*
	============================================================================
	Name        : communication.h
	Author      : kdesnos
	Version     : 1.0
	Copyright   : CECILL-C
	Description : Communication primitive for Preesm Codegen.
                  Currently, primitives were tested only for x86, shared_mem
                  communications.
	============================================================================
*/

#ifndef COMMUNICATION_H
#define COMMUNICATION_H

/**
* Non-blocking function called by the sender to signal that a buffer is ready
* to be sent.
* 
* @param sem
*        the semaphore used to signal that a data is available.
*/
void sendStart(sem_t* sem);

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
* @param sem
*        the semaphore used to signal that a data is available.
*/
void receiveEnd(sem_t* sem);

#endif
