/*******************************************************************************
 * Copyright or © or Copr. 2013 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2013)
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
 * com.c
 *
 *  Created on: Jun 10, 2013
 *      Author: jheulot
 */

#include "com.h"

static IHeap_Handle        srHeap = NULL;
static Ptr                 ptr = NULL;

Heap createHeap(int sharedRegion, int heapSize, const char* heapName){
    Int                 status  = 0;
    HeapMemMP_Params    heapMemParams;
    HeapMemMP_Handle    heapHandle;

    /* Create Heap and register it with MessageQ */
	HeapMemMP_Params_init (&heapMemParams);
	heapMemParams.sharedAddr = NULL;
	srHeap = SharedRegion_getHeap (sharedRegion);
	if (srHeap == NULL) {
		printf ("SharedRegion_getHeap failed. srHeap: [0x%x]\n", (UInt)srHeap);
	}else{
		ptr = Memory_alloc (srHeap,	heapSize + HeapMemMP_sharedMemReq (&heapMemParams), 0, NULL);
		if (ptr == NULL) {
			printf ("Memory_alloc failed."
						 " ptr: [0x%x]\n",
						 (UInt)ptr);
		}else{
			heapMemParams.name       = (String)heapName;
			heapMemParams.sharedAddr = ptr;
			heapMemParams.sharedBufSize = heapSize;
			heapHandle = HeapMemMP_create (&heapMemParams);
			if (heapHandle == NULL) {
				printf ("HeapMemMP_create failed."
							 " Handle: [0x%x]\n",
							 (UInt)heapHandle);
			}
		}
	}

    if (status >= 0) {
        /* Register this heap */
//        printf ("Registering heapId %d with MessageQ\n", 0);
        MessageQ_registerHeap (heapHandle, 0);
    }

    return heapHandle;
}

void deleteHeap(Heap heapHandle, int heapSize){
    Int                 status  = 0;

    MessageQ_unregisterHeap (0);
	status = HeapMemMP_delete ((HeapMemMP_Handle*)&heapHandle);
	if (status < 0) printf ("HeapMemMP_delete failed. Status [0x%xx]\n", status);

	Memory_free (srHeap, ptr, heapSize);
}

LocalQueue createQueue(const char* name){
    MessageQ_Handle     messageQ = NULL;
    Int                 status  = 0;

	/* Construct the messageQ name for the master */
	messageQ = MessageQ_create ((String)name, NULL);
	if (messageQ == NULL) {
		status = MessageQ_E_FAIL;
		printf ("Error in MessageQ_create [0x%x]\n", status);
	}

	return messageQ;
}

RemoteQueue openQueue(const char* name){
    MessageQ_QueueId    remoteQueueId;
    Int                 status  = 0;

	/* Construct the messageQ name for the slave */
	do {
	   status = MessageQ_open ((String)name, &remoteQueueId);
	   usleep(10000);
	} while (status == MessageQ_E_NOTFOUND);
	if (status < 0) {
		printf ("Error in MessageQ_open [0x%x]\n", status);
	}
	return remoteQueueId;
}


void deleteQueue(LocalQueue hndl){
    Int                 status  = 0;
    status = MessageQ_delete ((MessageQ_Handle*)&hndl);
    if (status < 0) {
        printf ("MessageQ_delete failed. Status [0x%xx]\n",
                     status);
    }
}

void closeQueue(RemoteQueue id){
    Int                 status  = 0;
    status = MessageQ_close (&id);
    if (status < 0) {
        printf ("MessageQ_close failed. Status [0x%xx]\n",
                     status);
    }
}

void sendQ(RemoteQueue id, void* data, int size){
    MessageQ_Msg        msg    = NULL;

	/* Ping-pong the same message around the processors */
	msg = MessageQ_alloc(0, size+sizeof(MessageQ_MsgHeader));
	if (msg == NULL) {
		printf ("Error in MessageQ_alloc\n");
	}

	memcpy(((UInt8*)msg)+sizeof(MessageQ_MsgHeader), data, size);

	if (MessageQ_put(id, msg) != MessageQ_S_SUCCESS) {
		printf ("MessageQ_put had a failure/error\n");
	}
}

void recvQ(LocalQueue hndl, void* data, int size){
    Int                 status  = 0;
    MessageQ_Msg        msg    = NULL;

	/* Get a message */
	MessageQ_get(hndl, &msg, MessageQ_FOREVER);/*besoin de status=?*/
	if (status != MessageQ_S_SUCCESS) {
		printf ("This should not happen since timeout is forever\n");
	}

	memcpy(data, ((UInt8*)msg)+sizeof(MessageQ_MsgHeader), size);

	status = MessageQ_free (msg);/*besoin de status=?*/
	if (status < 0) {
		printf ("MessageQ_free failed. Status [0x%xx]\n", status);
	}
}
