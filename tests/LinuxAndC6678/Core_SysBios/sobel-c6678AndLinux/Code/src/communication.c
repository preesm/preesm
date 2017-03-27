/*
 * communication.c
 *
 *  Created on: 7 août 2013
 *      Author: Karol
 */
#include <xdc/std.h>
/*  ----------------------------------- IPC module Headers           */
#include <ti/ipc/Ipc.h>
#include <ti/ipc/Notify.h>
#include <ti/sysbios/knl/Semaphore.h>
#include <xdc/runtime/System.h>
#include "communication.h"
#include <ti/sysbios/BIOS.h>
#include <ti/csl/csl_cacheAux.h>
#include <ti/csl/csl_semAux.h>
#include <ti/ipc/MultiProc.h>
#include <ti/sysbios/knl/Task.h>
#include <xdc/runtime/Error.h>
#include <ti/ipc/HeapMemMP.h>
#include "com.h"
#define HEAP_NAME  		"HeapMemMP"
// 8 local semaphore for each core (1 useless)
Semaphore_Handle interCoreSem[8];

HeapMemMP_Handle heapHandle;

/* Interrupt line used (0 is default) */
#define INTERRUPT_LINE  0

/* Semaphore used by the busy barrier */
#define BUSY_BARRIER_SEMAPHORE 31


/* Notify event number that the app uses */
#define EVENTID         10

#pragma DATA_SECTION(barrier, ".MSMCSRAM")
Char barrier = 0x01;

Void callbackInterCoreCom(UInt16 procId, UInt16 lineId, UInt32 eventId,
		UArg arg, UInt32 payload) {
	Semaphore_post(interCoreSem[procId]);
}

void sendStart(Uint16 coreID) {
	/* Send an event to the next processor */
	// The last parameter (TRUE) may cause this function to block.
	// In such case, maybe the penultimate parameter could be used to signal
	// that several send are grouped within a single notify event
	Int status = Notify_sendEvent(coreID, INTERRUPT_LINE, EVENTID, 0, TRUE);
	if (status < 0) {
		System_abort("sendEvent failed\n");
	}
}

void receiveEnd(Uint16 coreID) {
	Semaphore_pend(interCoreSem[coreID], BIOS_WAIT_FOREVER);
}

void communicationInit() {
	int i;
	Char procNumber = MultiProc_self();

	/*
	 *  Ipc_start() calls Ipc_attach() to synchronize all remote processors
	 *  because 'Ipc.procSync' is set to 'Ipc.ProcSync_ALL' in *.cfg
	 */
	int status = Ipc_start();
	if (status < 0) {
		System_abort("Ipc_start failed\n");
	}

	Error_init(NULL);

	// Connect to processor 0
	status = 0;
	do {
		status = Ipc_attach(0);
	} while (status < 0);

	heapHandle = openHeap(HEAP_NAME);

	// Allocate and initialize a new semaphores and return its handle
	for (i = 1; i < 8; i++) {
		interCoreSem[i] = Semaphore_create(0, NULL, NULL);
	}

	for (i = 1; i < 8; i++) {
		if (i != procNumber) {
			status = 0;
			do {
				status = Ipc_attach(i);
			} while (status < 0);
		}
	}

	/*
	 *  Register the same callback for all remote processor
	 */
	for (i = 1; i < 8; i++) {
		status = Notify_registerEvent(i, INTERRUPT_LINE, EVENTID,
				(Notify_FnNotifyCbck) callbackInterCoreCom, NULL);
		if (status < 0) {
			System_abort("Notify_registerEvent failed\n");
		}
	}

	if(procNumber == 1){
		hSEM->SEM_RST_RUN = 0x01;
	}
}

void receiveStart() {
}
void sendEnd() {
}

void busy_barrier() {
	Uint8 status;
	Char procNumber = MultiProc_self();

	do {
		status = CSL_semAcquireDirect(BUSY_BARRIER_SEMAPHORE);
	} while (status == 0);

	CACHE_invL2(&barrier, 1, CACHE_WAIT);
	barrier |= (1 << procNumber);
	CACHE_wbInvL2(&barrier, 1, CACHE_WAIT);
	CSL_semReleaseSemaphore(BUSY_BARRIER_SEMAPHORE);

	if (procNumber == 1) {
		while (barrier != (Char) 0xFF) {
			Task_sleep(1);
			CACHE_invL2(&barrier, 1, CACHE_WAIT);
		}
		barrier = (Char) 0x01;
		CACHE_wbInvL2(&barrier, 1, CACHE_WAIT);
		sendStart(2);
		receiveEnd(7);
	} else {
		int sendIdx;
		receiveEnd(procNumber - 1);
		sendIdx = (procNumber + 1) % 8;
		sendIdx = (sendIdx == 0) ? 1 : sendIdx;
		sendStart(sendIdx);

	}
}

