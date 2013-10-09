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

static unsigned char image_in[BUFFER_SIZE_MAX];
static unsigned char image_out[BUFFER_SIZE_MAX];

void printValue(FILE * file, unsigned char image[], int start, int end) {
	int i;
	for (i = start; i < end; i++) {
		fprintf(file, "0x%x\n", image[i]);
	}
}

/***********************************************************/

/******************** From messageQAppOS.c *****************/

/***********************************************************/

int main(int argc, char ** argv) {
#if defined (WIN32)
	WSADATA WSAData;
	int erreur = WSAStartup(MAKEWORD(2,2), &WSAData);
#else
	int erreur = 0;
#endif
	Int status = 0, size_split = 0, rest = 0;
	int image_size;
	int dataSize[7];
	int nbRepeat;
	UInt32 i = 0;
	UInt16 Cores[7];
	UInt16 MessageQApp_numProcs = 0;
	char addressW[16]; //windows address
	char MESSAGE_Q_NAME[8][16];
	char REMOTE_Q_NAME[8][16];
	LocalQueue messageQ[8];
	RemoteQueue remoteQueueId[8];
	Heap heapHandle;
	SOCKET sock1;
	SOCKADDR_IN sin1;
	SOCKET sock2;
	SOCKADDR_IN sin2;
	char fileName[7][10];
	FILE * file = NULL;
	char s[20];
	long alloc;
	char c;

	int heapSize = 0;
	//unsigned long bufferAlloc = 2;

	file = fopen("./config.ini", "r");
	rewind(file);
	if (file == NULL) {
		printf("Error while opening configuration file\n");
	} else {
		Osal_printf("Success while opening configuration file\n");
		i = readAddress(addressW, file);
		Osal_printf("Address Windows: %s", addressW);
		heapSize = readHeap(file);
		Osal_printf("Heap size = 0x%x\n", heapSize);
		nbRepeat = readNbRepeat(file);
		Osal_printf("Nb of repetition %d\n", nbRepeat);
		rewind(file);
		while (c != EOF) {
			c = fgetc(file);
			if (c == ';') {
				fgets(s, 100, file);
			} else if (c == 'b') {
				fgets(s, 6, file);
				if (strncmp(s, "uffer", 6) == 0) {
					while (c != '=') {
						c = fgetc(file);
					}
					fgets(s, 100, file);
					alloc = strtoll(s, NULL, 16);
					Osal_printf("%lx\n", alloc);
				}
			}
		}
	}
	fclose(file);

	printf("MessageQApp sample application\n");

	if (argc > 1) {
		MessageQApp_numProcs = strtol(argv[1], NULL, 10);
	} else {
		/* If no special run instructions are given, run for all procs. */
		Osal_printf("You must specify the number of procs\n%s (procNum)\n",
				argv[0]);
		return -1;
	}

	SysLink_setup();
	printf("Syslink Initialized\n");

	/* If there is more than 2 arguments, select cores in an order defined by user */
	/* Else select from Core1 to Core7 by default */
	printf("argc : %d, argv[2] : %s", argc, argv[2]);
	if (argc > 2) {
		for (i = 2; i < argc; i++)
			Cores[i - 2] = strtol(argv[i], NULL, 10);
	} else {
		for (i = 0; i < MessageQApp_numProcs; i++)
			Cores[i] = i + 1;
	}

	/* Run cores */
	for (i = 0; i < MessageQApp_numProcs; i++) {
		Osal_printf("Starting procId %d \n", Cores[i]);
		status = ProcMgrApp_startup(Cores[i]);
		Osal_printf("status [%d]\n", status);
	}

	/* MessageQ creation */
	heapHandle = createHeap(0, heapSize, HEAP_NAME);
	for (i = 0; i < MessageQApp_numProcs; i++) {
		sprintf(MESSAGE_Q_NAME[i], "Core0 to Core%d", Cores[i]);
		sprintf(REMOTE_Q_NAME[i], "Core%d to Core0", Cores[i]);
		messageQ[i] = createQueue(MESSAGE_Q_NAME[i]);
		remoteQueueId[i] = openQueue(REMOTE_Q_NAME[i]);
	}

	Osal_printf("Cores number : %d\n", MessageQApp_numProcs);

	/*******************************************************/

	/*********************** Sockets ***********************/

	/*******************************************************/
	while (nbRepeat != 0) {
		nbRepeat = (nbRepeat >= 0) ? nbRepeat - 1 : -1;
		if (!erreur) {
			/* Sockets creation */
			sock1 = socket(AF_INET, SOCK_STREAM, 0);
			sock2 = socket(AF_INET, SOCK_STREAM, 0);

			/* Configuration socket1 */
			sin1.sin_addr.s_addr = inet_addr(addressW);
			sin1.sin_family = AF_INET;
			sin1.sin_port = htons(PORT_IN);

			/* Communication on socket1 */
			if (connect(sock1, (SOCKADDR*) &sin1, sizeof(sin1)) != SOCKET_ERROR) {
				printf("Connected to %s on port %d\n", inet_ntoa(sin1.sin_addr),
						htons(sin1.sin_port));

				/* Receiving information from server */

				/* Receive image size */
				if (recv(sock1, &image_size, sizeof(int), MSG_WAITALL) != 0) {
					image_size = htonl(image_size);
					Osal_printf("Image size: %d\n", image_size);
				}
				/* Receive an image */
				if ((i = recv(sock1, image_in, image_size, MSG_WAITALL))) {
					Osal_printf("Image received %d bytes\n", i);
				}

			} else {
				printf("Error when trying to connect server with socket1\n");
			}

			/* Socket1 closing */
			closesocket(sock1);
			printf("Connection on port %d closed\n", htons(sin1.sin_port));
			size_split = image_size / MessageQApp_numProcs;
			rest = image_size % MessageQApp_numProcs;

			for (i = 0; i < MessageQApp_numProcs; i++) {
				dataSize[i] = size_split;
				if (rest != 0) {
					dataSize[i]++;
					rest--;
				}
				/* Send data to Core i */
				//-sendQ(remoteQueueId[i], &i,sizeof(int));	//rank of core
				//-- sendQ(remoteQueueId[i], &alloc, sizeof(long));
				sendQ(remoteQueueId[i], &dataSize[i], sizeof(int));
				sendQ(remoteQueueId[i], image_in + i * dataSize[i],
						dataSize[i]);
			}

			/* Receive data from cores */
			for (i = 0; i < MessageQApp_numProcs; i++) {
				sprintf(fileName[i], "/LogFiles/Core%d.log", Cores[i]);
				file = fopen(fileName[i], "w+");
				/* Receive data from Core i */
				recvQ(messageQ[i], image_out + i * dataSize[i], dataSize[i]);
				/* Print value in a file */
				if (file != NULL) {
					printValue(file, image_out, i * dataSize[i],
							(i + 1) * dataSize[i]);
					fclose(file);
				}
			}

			/* Configuration socket2 */
			sin2.sin_addr.s_addr = inet_addr(addressW);
			sin2.sin_family = AF_INET;
			sin2.sin_port = htons(PORT_OUT);

			/* Communication on socket2 */
			if (connect(sock2, (SOCKADDR*) &sin2, sizeof(sin2)) != SOCKET_ERROR) {
				printf("Connected to %s on port %d\n", inet_ntoa(sin2.sin_addr),
						htons(sin2.sin_port));
				send(sock2, &MessageQApp_numProcs, sizeof(int), MSG_WAITALL);

				/* Send result to sever */
				send(sock2, image_out, image_size, 0);
				printf("Image processed sent %d\n", image_size);
			} else {
				printf("Error when trying to connect server with socket2\n");
			}

			/* Socket2 closing */
			closesocket(sock2);
			printf("Connection on port %d closed\n", htons(sin2.sin_port));
		}
	}

	/* MessageQ closing */
	for (i = 0; i < MessageQApp_numProcs; i++) {
		printf("closing %s\n", REMOTE_Q_NAME[i]);
		closeQueue(remoteQueueId[i]);
		printf("deleting %s\n", MESSAGE_Q_NAME[i]);
		deleteQueue(messageQ[i]);
	}
	deleteHeap(heapHandle, heapSize);

	for (i = 0; i < MessageQApp_numProcs; i++) {
		status = ProcMgrApp_shutdown(i + 1);
	}

	SysLink_destroy();
	printf("Fini!\n");

#if defined (WIN32)
	WSACleanup();
#endif

	return EXIT_SUCCESS;
}
