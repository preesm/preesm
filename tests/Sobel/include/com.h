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
#ifndef COM_H
#define COM_H

#ifdef A9
/* OSAL & Utils headers */
#include <Trace.h>

/* Module level headers */
#include <ti/ipc/MessageQ.h>
#include <ti/ipc/HeapBufMP.h>
#include <ti/ipc/SharedRegion.h>
#include <semaphore.h>
#include <malloc.h>
#include <string.h> //memcpy

#define SLEEP(a) usleep(a*1000)

#else

/*  -----------------------------------XDC.RUNTIME module Headers    */
#include <xdc/runtime/Memory.h>
#include <xdc/runtime/System.h>

/*  ----------------------------------- IPC module Headers           */
#include <ti/ipc/MessageQ.h>
#include <ti/ipc/HeapBufMP.h>
#include <ti/ipc/SharedRegion.h>
#include <ti/sysbios/knl/Task.h>
#include <xdc/runtime/Memory.h>

#define SLEEP(a) Task_sleep(a)

#endif

#include "fifo.h"

#define FOREVER ~(0)

/**
 * Direction of a communication.
 */
typedef enum{
	SEND=0,//!< SEND
	RCV=1  //!< RCV
} direction;

/**
 * List of all operators.
 * Print is a special operator which is run on CortexA9 to display message from each other operators.
 */
typedef enum{
	CortexA9_1=0,//!< CortexA9_1
	CortexA9_2=1,//!< CortexA9_2
	Tesla=2,     //!< Tesla
	SysM3=3,     //!< SysM3
	AppM3=4,     //!< AppM3
	Print=5,     //!< A special operator which display messages from each other operators. Run on CortexA9.
	IDCount=6    //!< Keep count of operators
} actorID;

/**
 * A Communicator is an object used by an operator to communicate to other operators.
 */
typedef struct {
	actorID myID;
	MessageQ_Handle my_msgQ;
	MessageQ_QueueId msgQ[IDCount];
	fifo msgFifo[IDCount];
} communicator;

/**
 * Initialize communications system for CortexA9. Initialize shared memory as fixed sized blocks.
 * Blocks have to be sized to contain the biggest message.
 * The size have to be rounded with the alignment which is 128.
 * Then the maximal number of blocks is allocated.
 * @param blockSize size of the biggest message.
 */
void comInitA9(long blockSize);

/**
 * Initialize communication system for CoProcessors (M3s and DSP).
 */
void comInitCoPro();

/**
 * Clean communication system for all operators.
 */
void comClear();

/**
 * Initialize Communicator structure.
 * @param com Communicator structure to initialize.
 * @param me ID of communicator's operator
 */
void communicator_init(communicator* com, actorID me);

/**
 * Clean Communicator structure
 * @param com Communicator structure to clean.
 */
void communicator_clear(communicator* com);

/**
 * Open a link between the communicator of the operator to another operator.
 * @param com the communicator of the operator.
 * @param Dir Direction of the Link.
 * @param Actor ID of the actor to open link.
 */
void communicator_openLink(communicator* com, direction Dir, actorID Actor);

/**
 * Close a link between the communicator of the operator to another operator.
 * @param com the communicator of the operator.
 * @param Dir Direction of the Link.
 * @param Actor ID of the actor to close link.
 */
void communicator_closeLink(communicator* com, direction Dir, actorID Actor);

/**
 * Send data to the corresponding operator
 * @param com the communicator of the operator.
 * @param ReceiverID ID of the receiving operator.
 * @param PV_buffer Pointer of the data to send.
 * @param I_size_of_data Size of the data.
 * @return A positive integer if it succeed, a negative one otherwise.
 */
int communicator_send(communicator* com, actorID ReceiverID, void *PV_buffer, int I_size_of_data);

/**
 * Receive a data from another operator.
 * @param com the communicator of the operator.
 * @param SenderID ID of the sending operator.
 * @param PV_buffer Pointer of a local memory to copy data in it.
 * @param I_size_of_data Size of the data.
 * @param Timeout Timeout in ms.
 * @return A positive integer if it succeed, a negative one otherwise.
 */
int communicator_receive(communicator* com, actorID SenderID, void *PV_buffer, int I_size_of_data, UInt Timeout);

/**
 * Make a join between each operator. Operators wait an Ack from CortexA9_1 to continue its thread.
 * Useful for initialization and cleaning synchronizations.
 * \msc
 * 		arcgradient = 8;
 * 		a1 [label="CortexA9_1"],a2 [label="CortexA9_2"],app [label="AppM3"],sys [label="SysM3"],dsp [label="Tesla"];
 * 		a2=>a1  [label="Send join"];
 * 		app=>a1 [label="Send join"];
 * 		sys=>a1 [label="Send join"];
 * 		dsp=>a1 [label="Send join"];
 * 		a1=>a2  [label="Ack"];
 * 		a1=>app [label="Ack"];
 * 		a1=>sys [label="Ack"];
 * 		a1=>dsp [label="Ack"];
 * \endmsc
 * @param com the communicator of the operator.
 */
void join(communicator *com);

#endif
