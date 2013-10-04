#include <xdc/runtime/System.h>
#include <stdio.h>

#include "receiveData.h"
#include "com.h"
#include <ti/ipc/MessageQ.h>

MessageQ_Handle messageQ;
MessageQ_QueueId remoteQueueId;

void initReceiveData() {

	UInt16 lProcId;
	char MESSAGE_Q_NAME[16];

	lProcId = MultiProc_self();

	sprintf(MESSAGE_Q_NAME, "Core%d to Core0", lProcId);
	messageQ = createQueue(MESSAGE_Q_NAME);

}

void initSendData() {
	char REMOTE_Q_NAME[16];
	UInt16 lProcId;

	lProcId = MultiProc_self();

	sprintf(REMOTE_Q_NAME, "Core0 to Core%d", lProcId);
	remoteQueueId = openQueue(REMOTE_Q_NAME);
}

void receiveData(int size, unsigned char *data) {

	//Int rank = -1;
	int data_size;
	//-- long alloc;

	/* Receive data */
	//--recvQ(messageQ, &rank, sizeof(int));
	//-- recvQ(messageQ, &alloc, sizeof(long));

	recvQ(messageQ, &data_size, sizeof(int));
	if (data_size != size) {
		System_printf(
				"Expected size (%d) is different from received size (%d).\n",
				size, data_size);
		System_abort("Expected size is different from received size.\n");
	}
	System_printf("data_size: %d\n", data_size);

	//-- *data = (unsigned char *) alloc + rank * data_size;
	recvQ(messageQ, data, data_size);

	//deleteQueue(messageQ);
}

void sendData(int size, unsigned char *data) {
	sendQ(remoteQueueId, data, size);

	/* Closing */
	closeQueue(remoteQueueId);
}
