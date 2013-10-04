/*
 * com.h
 *
 *  Created on: Jun 10, 2013
 *      Author: jheulot
 */

#ifndef COM_H_
#define COM_H_

/* Standard headers */
#include <ti/syslink/Std.h>

/* OSAL & Utils headers */
#include <ti/syslink/utils/Trace.h>
#include <ti/syslink/utils/OsalPrint.h>
#include <ti/syslink/utils/Memory.h>
#include <ti/syslink/utils/String.h>
#include <ti/syslink/utils/Cache.h>

#include <ti/syslink/ProcMgr.h>

/* Module level headers */
#include <ti/ipc/MultiProc.h>
#include <ti/ipc/HeapMemMP.h>
#include <ti/ipc/SharedRegion.h>

/* Module level headers */
#include <ti/ipc/MessageQ.h>
#include <ti/ipc/Notify.h>
/* Ipc header */
#include <ti/ipc/Ipc.h>

#include <ti/syslink/inc/_MultiProc.h>

#include <stddef.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>

typedef void* Heap;
typedef void* LocalQueue;
typedef unsigned int RemoteQueue;

Heap createHeap(int sharedRegion, int heapSize, const char* name);
void deleteHeap(Heap heapHandle, int heapSize);

LocalQueue createQueue(const char* name);
void deleteQueue(LocalQueue hndl);

RemoteQueue openQueue(const char* name);
void closeQueue(RemoteQueue id);

void sendQ(RemoteQueue id, void* data, int size);
void recvQ(LocalQueue hndl, void* data, int size);

#endif /* COM_H_ */
