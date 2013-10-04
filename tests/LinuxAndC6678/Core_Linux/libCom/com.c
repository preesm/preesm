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
