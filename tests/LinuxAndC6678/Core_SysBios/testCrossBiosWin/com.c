/*
 * com.c
 *
 *  Created on: Sept 10, 2013
 *      Author: rparois
 */

/* Standard headers */
#include <xdc/std.h>
#include <string.h>

/*  -----------------------------------XDC.RUNTIME module Headers    */
#include <xdc/runtime/Memory.h>
#include <xdc/runtime/Error.h>
#include <xdc/runtime/System.h>
#include <xdc/runtime/IHeap.h>

/*  ----------------------------------- IPC module Headers           */
#include <ti/ipc/GateMP.h>
#include <ti/ipc/Ipc.h>
#include <ti/sdo/ipc/Ipc.h>
#include <ti/ipc/MessageQ.h>
#include <ti/ipc/HeapMemMP.h>
#include <ti/ipc/MultiProc.h>
#include <ti/ipc/Notify.h>

/*  ----------------------------------- BIOS6 module Headers         */
#include <ti/sysbios/BIOS.h>
#include <ti/sysbios/knl/Semaphore.h>
#include <ti/sysbios/hal/Cache.h>
/*  ----------------------------------- To get globals from .cfg Header */
#include <xdc/cfg/global.h>

#include "com.h"

// 8 local semaphore for each core (1 useless)
Semaphore_Handle interCoreSem[8];

HeapMemMP_Handle openHeap(const char* heapName){
    Int                 status  = 0;
    HeapMemMP_Handle    heapHandle;

	/* Open the heap created by the other processor. Loop until opened. */
	do {
	   status = HeapMemMP_open((String)heapName, &heapHandle);
	   if (status == HeapMemMP_E_NOTFOUND) {
		   /* Sleep for a while before trying again. */
		   Task_sleep (10);
	   }
	}
	while (status != HeapMemMP_S_SUCCESS);

	/* Register this heap with MessageQ */
	MessageQ_registerHeap((IHeap_Handle)heapHandle, HEAPID);

    return heapHandle;
}

void closeHeap(HeapMemMP_Handle heapHandle){
    Int                 status  = 0;
    MessageQ_unregisterHeap (HEAPID);
    status = HeapMemMP_close (&heapHandle);
    if (status < 0) {
        System_abort("HeapMemMP_close failed\n" );
    }
}

MessageQ_Handle createQueue(const char* name){
    MessageQ_Handle     messageQ = NULL;
    Int                 status  = 0;

	/* Construct the messageQ name for the master */
	messageQ = MessageQ_create ((String)name, NULL);
	if (messageQ == NULL) {
		status = MessageQ_E_FAIL;
		System_printf ("Error in MessageQ_create [0x%x]\n", status);
	}
	return messageQ;
}

MessageQ_QueueId openQueue(const char* name){
    MessageQ_QueueId    remoteQueueId;
    Int                 status  = 0;

    /* Open the remote message queue. Spin until it is ready. */
    do {
        status = MessageQ_open((String)name, &remoteQueueId);
        if (status == MessageQ_E_NOTFOUND) {
            /* Sleep for a while before trying again. */
            Task_sleep (10);
        }
    }
    while (status != MessageQ_S_SUCCESS);

	return remoteQueueId;
}


void deleteQueue(MessageQ_Handle hndl){
    Int                 status  = 0;
    status = MessageQ_delete (&hndl);
    if (status < 0) {
    	System_printf ("MessageQ_delete failed. Status [0x%xx]\n",
                     status);
    }
}

void closeQueue(MessageQ_QueueId id){
    Int                 status  = 0;
    status = MessageQ_close (&id);
    if (status < 0) {
    	System_printf ("MessageQ_close failed. Status [0x%xx]\n",
                     status);
    }
}

void sendQ(MessageQ_QueueId id, void* data, int size){
    MessageQ_Msg        msg    = NULL;

	/* Ping-pong the same message around the processors */
	msg = MessageQ_alloc(0, size+sizeof(MessageQ_MsgHeader));
	if (msg == NULL) {
		System_printf ("Error in MessageQ_alloc\n");
	}

	memcpy(((UInt8*)msg)+sizeof(MessageQ_MsgHeader), data, size);

	if (MessageQ_put(id, msg) != MessageQ_S_SUCCESS) {
		System_printf ("MessageQ_put had a failure/error\n");
	}
}

void recvQ(MessageQ_Handle hndl, void* data, int size){
    Int                 status  = 0;
    MessageQ_Msg        msg    = NULL;

	/* Get a message */
	MessageQ_get(hndl, &msg, MessageQ_FOREVER);
	if (status != MessageQ_S_SUCCESS) {
		System_printf ("This should not happen since timeout is forever\n");
	}

	memcpy(data, ((UInt8*)msg)+sizeof(MessageQ_MsgHeader), size);

	MessageQ_free (msg);
	if (status < 0) {
		System_printf ("MessageQ_free failed. Status [0x%xx]\n", status);
	}
}
