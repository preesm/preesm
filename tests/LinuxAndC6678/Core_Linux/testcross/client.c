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
#if defined (WIN32)
#include <winsock2.h>
typedef int socklen_t;
#elif defined (linux)
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#define INVALID_SOCKET -1
#define SOCKET_ERROR -1
#define closesocket(s) close(s)
typedef int SOCKET;
typedef struct sockaddr_in SOCKADDR_IN;
typedef struct sockaddr SOCKADDR;
#endif

#include <stdio.h>
#include <stdlib.h>

#ifndef NULL
#define NULL 0
#endif

/* Standard headers */
#include <ti/syslink/Std.h>

/* OSAL & Utils headers */
#include <ti/syslink/utils/Trace.h>
#include <ti/syslink/utils/OsalPrint.h>
#include <ti/syslink/utils/Memory.h>
#include <ti/syslink/utils/String.h>
#include <ti/syslink/utils/Cache.h>

#include <ti/syslink/ProcMgr.h>
#include <ProcMgrApp.h>

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

#include "com.h"

#include <unistd.h>
#include <string.h>
#include "init.h"

/* OSAL & Utils headers */
#include <ti/syslink/inc/_MultiProc.h>
#include <ti/syslink/SysLink.h>

/* App info tag ID */
#define APP_INFO_TAG        0xBABA0000
#define APPNOTIFY_EVENT_NO                11u
/* key to generate data pattern for messages */
#define PATTERNKEY               1u

#define MSGQ_NAME		"MSGQ_"
#define HEAP_NAME  		"HeapMemMP"
#define HEAP_ALIGN  	128
#define HEAP_MSGSIZE  	128-32
#define HEAP_NUMMSGS  	10
#define HEAPID  		0
#define NUMLOOPS  		1000

#define PORT_IN 2013
#define PORT_OUT 567
#define BUFFER_SIZE_MAX 400*300*3

#define SILENT 1

#if SILENT==0
#define PRINTF Osal_printf
#else
#define PRINTF(a,...)
#endif

static unsigned char image_in[BUFFER_SIZE_MAX];
static unsigned char image_out[BUFFER_SIZE_MAX];

/***********************************************************/

/******************** From messageQAppOS.c *****************/

/***********************************************************/

int main(int argc, char ** argv) {

	Int status = 0;
	int image_size;
	int dataSize;
	int nbRepeat;
	UInt32 i = 0;
	UInt16 Cores[7];
	UInt16 MessageQApp_numProcs = 0;
	char addressW[16]; //windows address
	char MESSAGE_Q_NAME[16];
	char REMOTE_Q_NAME[16];
	LocalQueue messageQ;
	RemoteQueue remoteQueueId;
	Heap heapHandle;
	SOCKET sock1;
	SOCKADDR_IN sin1;
	SOCKET sock2;
	SOCKADDR_IN sin2;
	FILE * file = NULL;

	int heapSize = 0;
	//unsigned long bufferAlloc = 2;

	file = fopen("./config.ini", "r");
	if (file == NULL) {
		Osal_printf("Error while opening configuration file\n");
	} else {
		PRINTF("Success while opening configuration file\n");
		i = readAddress(addressW, file);
		PRINTF("Address Windows: %s\n", addressW);
		heapSize = readHeap(file);
		PRINTF("Heap size = 0x%x\n", heapSize);
		nbRepeat = readNbRepeat(file);
		PRINTF("Nb of repetition %d\n", nbRepeat);
		rewind(file);
	}
	fclose(file);

#define end
#ifdef end
	PRINTF("MessageQApp sample application\n");
	MessageQApp_numProcs = 7;

	SysLink_setup();
	PRINTF("Syslink Initialized\n");

	/* If there is more than 2 arguments, select cores in an order defined by user */
	/* Else select from Core1 to Core7 by default */

	for (i = 0; i < MessageQApp_numProcs; i++) {
		Cores[i] = i + 1;
	}

	/* Run cores */
	for (i = 0; i < MessageQApp_numProcs; i++) {
		PRINTF("Starting procId %d \n", Cores[i]);
		status = ProcMgrApp_startup(Cores[i]);
		PRINTF("status [%d]\n", status);
	}

	/* MessageQ creation with core 1*/
	heapHandle = createHeap(0, heapSize, HEAP_NAME);

	sprintf(MESSAGE_Q_NAME, "Core0 to Core%d", Cores[0]);
	sprintf(REMOTE_Q_NAME, "Core%d to Core0", Cores[0]);
	messageQ = createQueue(MESSAGE_Q_NAME);
	remoteQueueId = openQueue(REMOTE_Q_NAME);

	PRINTF("Cores number : %d\n", MessageQApp_numProcs);

	/*******************************************************/

	/*********************** Sockets ***********************/

	/*******************************************************/
	int connectedSocket1 = 0;
	int connectedSocket2 = 0;

	/* Sockets creation */
	sock1 = socket(AF_INET, SOCK_STREAM, 0);
	sock2 = socket(AF_INET, SOCK_STREAM, 0);

	/* Configuration socket1 */
	sin1.sin_addr.s_addr = inet_addr(addressW);
	sin1.sin_family = AF_INET;
	sin1.sin_port = htons(PORT_IN);
	while (connectedSocket1 == 0) {
		/* Communication on socket1 */
		if (connect(sock1, (SOCKADDR*) &sin1, sizeof(sin1)) != SOCKET_ERROR) {
			PRINTF("Connected to %s on port %d\n",
					inet_ntoa(sin1.sin_addr), htons(sin1.sin_port));

			connectedSocket1 = 1;

		} else {
			connectedSocket1 = 0;
			PRINTF(
					"Connect to server with socket1 (%d) failed\n",
					htons(sin1.sin_port));

		}
	}

	/* Configuration socket2 */
	sin2.sin_addr.s_addr = inet_addr(addressW);
	sin2.sin_family = AF_INET;
	sin2.sin_port = htons(PORT_OUT);

	while (connectedSocket2 == 0) {
		/* Communication on socket2 */
		if (connect(sock2, (SOCKADDR*) &sin2, sizeof(sin2)) != SOCKET_ERROR) {
			PRINTF("Connected to %s on port %d\n",
					inet_ntoa(sin2.sin_addr), htons(sin2.sin_port));

			connectedSocket2 = 1;
		} else {
			PRINTF(
					"Connection to server with socket2 (%d) failed\n",
					htons(sin2.sin_port));
			connectedSocket2 = 0;
		}
	}

	while (nbRepeat != 0) {
		PRINTF("Remaining %d\n", nbRepeat);
		nbRepeat = (nbRepeat >= 0) ? nbRepeat - 1 : -1;

		/* Receiving information from server */

		/* Receive image size */
		if (recv(sock1, &image_size, sizeof(int), MSG_WAITALL) != 0) {
			image_size = htonl(image_size);
			PRINTF("Image size: %d\n", image_size);
		}
		/* Receive an image */
		if ((i = recv(sock1, image_in, image_size, MSG_WAITALL))) {
			PRINTF("Image received %d bytes\n", i);
		}

		// Send data to core 1
		dataSize = image_size;

		/* Send data to Core i */
		sendQ(remoteQueueId, &dataSize, sizeof(int));
		sendQ(remoteQueueId, image_in, dataSize);

		/* Receive data from core 1 */
		recvQ(messageQ, image_out, dataSize);

		/* Send result to sever */
		send(sock2, image_out, image_size, 0);
		PRINTF("Image processed sent %d\n", image_size);

	}

	/* Socket1 closing */
	closesocket(sock1);
	PRINTF("Connection on port %d closed\n", htons(sin1.sin_port));
	/* Socket2 closing */
	closesocket(sock2);
	PRINTF("Connection on port %d closed\n", htons(sin2.sin_port));

	/* MessageQ closing */
	PRINTF("closing %s\n", REMOTE_Q_NAME);
	closeQueue(remoteQueueId);
	PRINTF("deleting %s\n", MESSAGE_Q_NAME);
	deleteQueue(messageQ);

	deleteHeap(heapHandle, heapSize);

	for (i = 0; i < MessageQApp_numProcs; i++) {
		status = ProcMgrApp_shutdown(i + 1);
	}

	SysLink_destroy();

#endif
	Osal_printf("Done\n");

	return EXIT_SUCCESS;
}
