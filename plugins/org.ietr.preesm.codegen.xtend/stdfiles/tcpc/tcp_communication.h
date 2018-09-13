

#ifndef _SOCKETCOM_H_
#define _SOCKETCOM_H_

#define _GNU_SOURCE

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

// during opening sequence, wait time between each client to server connection fail: 50 ms
#define _PREESM_WAIT_SERVER_START_US (50*1000)

// set TCP socket buffers to 2MB
#define _PREESM_SOCKET_BUFFER_SIZE (2*1024*1024)

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
