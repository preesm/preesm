/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
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
 * knowledge of the CeCILL license and that you accept its terms.
 */
#ifndef _PREESM_SOCKETCOM_H_
#define _PREESM_SOCKETCOM_H_

#define _GNU_SOURCE

#define _PREESM_TCP_DEBUG_ 1

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>

#include <sys/ioctl.h>
#include <sys/poll.h>

#include <netdb.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/tcp.h>

#include "communication.h"


#ifndef max
    #define max(a,b) ((a) > (b) ? (a) : (b))
#endif
#ifndef min
    #define min(a,b) ((a) < (b) ? (a) : (b))
#endif

// during opening sequence, wait time between each client to server connection fail: 5 ms
#define _PREESM_WAIT_SERVER_START_US (5*1000)

// set TCP socket buffers to 208 KB
#define _PREESM_SOCKET_BUFFER_SIZE (208*1024)

// Error codes
#define _PREESM_ERROR_ACK 1
#define _PREESM_ERROR_RESOLVE_HOST 2
#define _PREESM_ERROR_CREATE_SOCKET 3
#define _PREESM_ERROR_BINDING 4
#define _PREESM_ERROR_POLLING 5

typedef struct processingElement_t {
  int id;
  char host[255];
  int port;
} ProcessingElement;

void preesm_send_start(int from, int to, int * socketRegistry, char* buffer, int size, const char* bufferName);
void preesm_send_end(int from, int to, int * socketRegistry, char* buffer, int size, const char* bufferName);

void preesm_receive_start(int from, int to, int * socketRegistry, char* buffer, int size, const char* bufferName);
void preesm_receive_end(int from, int to, int * socketRegistry, char* buffer, int size, const char* bufferName);

void preesm_open(int* socketFileDescriptors, int processingElementID, int numberOfProcessingElements, ProcessingElement registry[numberOfProcessingElements]);
void preesm_close(int * socketRegistry, int processingElementID, int numberOfProcessingElements);

void preesm_barrier(int * socketRegistry, int processingElementID, int numberOfProcessingElements);

#endif
