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
#include <xdc/runtime/System.h>
#include <stdio.h>

#include "sendReceiveData.h"
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

	int data_size;

	recvQ(messageQ, &data_size, sizeof(int));
	if (data_size != size) {
		System_printf(
				"Expected size (%d) is different from received size (%d).\n",
				size, data_size);
		System_abort("Expected size is different from received size.\n");
	}
	//System_printf("data_size: %d\n", data_size);

	recvQ(messageQ, data, data_size);

	//deleteQueue(messageQ);
}

void sendData(int size, unsigned char *data) {
	sendQ(remoteQueueId, data, size);

	/* Closing */
	//closeQueue(remoteQueueId);
}
