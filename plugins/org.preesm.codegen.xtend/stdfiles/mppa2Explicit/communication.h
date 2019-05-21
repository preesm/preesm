/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2017 - 2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Daniel Madroñal <daniel.madronal@upm.es> (2019)
 * Julien Hascoet <jhascoet@kalray.eu> (2017)
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

#include "preesm_gen.h"
#include "preesm.h"

#include <semaphore.h>
#include <mppa_rpc.h>
#include <mppa_async.h>
#include <HAL/hal/hal_ext.h>
#include <mOS_vcore_u.h>
#include <assert.h>

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
