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

#include <stdlib.h>
#include <stdio.h>
#include "com.h"
#include "application.h"
#include "receiveData.h"

#define HEAP_NAME  		"HeapMemMP"
#define BUFFER_SIZE_MAX 400*300*3

/*!
 *  ======== Ipc_writeConfig ========
 */
//Int Ipc_writeConfig(UInt16 remoteProcId, UInt32 tag, Ptr cfg, SizeT size);
/* App info tag ID */
//#define APP_INFO_TAG        0xBABA0000
//#define APPNOTIFY_EVENT_NO                11u
/* key to generate data pattern for messages */
//#define PATTERNKEY               1u
Semaphore_Handle semHandle1;

Void APPNotify_callbackFxn(UInt16 procId, UInt16 lineId, UInt32 eventNo,
		UArg arg, UInt32 payload) {
	Semaphore_post((Semaphore_Object*) arg);
}
#pragma DATA_SECTION(image_in, ".mySharedMem")
unsigned char image_in[300 * 400 * 3];
#pragma DATA_SECTION(image_out, ".mySharedMem")
unsigned char image_out[300 * 400 * 3];

/*
 *  ======== tsk1_func ========
 *  Send and receive messages
 */Void tsk1_func(UArg arg0, UArg arg1) {
	HeapMemMP_Handle heapHandle;
	Int status = 0, start, rank = -1;
	int data_size;
	int i =0;

	/*! KK: Add to cominit */
	{
		Error_init(NULL);
		UInt16 rProcId = (UInt16) arg0;

		/* Connect to remote processor */
		do {
			status = Ipc_attach(rProcId);
		} while (status < 0);

		heapHandle = openHeap(HEAP_NAME);
	}


	/* Create a message queue */
	initReceiveData();
	initSendData();

	for(i=0;i<40 ;i++){
		System_printf("iter: %d\n",i);
	/* Receive data */
	receiveData(300 * 400 * 3, image_in);

	//--image_out = image_in + 300 * 400 * 3;
	rank = 1;
	data_size = 300 * 400 * 3;

	/* Image processing */
	start = rank * data_size;
	decode(image_in, image_out, data_size, start);

	/* Send data */
	sendData(data_size, image_out);
	}

	closeHeap(heapHandle);


	System_printf("The test is complete\n");
}

/*
 *  ======== main ========
 */
Int main(Int argc, Char* argv[]) {
	Int status = Ipc_S_SUCCESS;

	do {
		/* Call Ipc_start() */
		status = Ipc_start();
	} while (status != Ipc_S_SUCCESS);

	BIOS_start();
	return (0);
}

