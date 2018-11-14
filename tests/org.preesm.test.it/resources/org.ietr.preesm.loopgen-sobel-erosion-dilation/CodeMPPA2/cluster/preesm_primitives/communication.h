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

#endif
