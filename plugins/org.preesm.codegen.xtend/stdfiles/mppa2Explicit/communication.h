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

#include <preesm_gen.h>

/**
* Communication Initialization (archi dependent)
*/
void communicationInit();

/**
* Non-blocking function called by the sender to signal that a buffer is ready
* to be sent.
* 
* @param cluster
*        the id of the cluster to post the token.
*/
void sendStart(int cluster);

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
* @param cluster
*        the id of the cluster to sync.
*/
void receiveEnd(int cluster);

/**
* Blocking function (for distributed_mem communication) called by the sender to
* signal that a buffer is ready to be sent.
*
* @param cluster
*        the id of the cluster to post the token.
*/
void sendDistributedStart(int cluster);

/**
* Blocking function (for distributed_mem communication) called by the sender to
* signal that communication is completed.
*/
void sendDistributedEnd(int cluster);

/**
* Blocking function (for distributed_mem communication) called by the receiver to
* begin receiving the data.
*/
void receiveDistributedStart(int remotePE, off64_t remoteOffset, void* localAddress, size_t transmissionSize);

/**
* Blocking function (for distributed_mem communication) called by the receiver to
* signal that data has already been copied.
*
* @param cluster
*        the id of the cluster to sync.
*/
void receiveDistributedEnd(int cluster);

#endif
