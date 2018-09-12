
#include "socketcom.h"

/**
 * Debug function
 */
void printRegistry(int numberOfProcessingElements, ProcessingElement registry[numberOfProcessingElements]) {
  printf("--\n");
  printf("-- Registry\n");
  for (int i = 0; i < numberOfProcessingElements; i++) {
    char* host= registry[i].host;
    int port = registry[i].port;
    int id = registry[i].id;
    printf(" ID = %3d (%3d)\t HOST = %15s\t PORT = %5d\n",id, i,host,port);
  }
  printf("--\n");
}

/**
 * Poll a socket : blocks until some data arrive (POLLIN) on the socket.
 */
void preesm_poll_socket_read_available(int socket) {
  int rc;
  struct pollfd fds[2];
  int nfds = 1;
  memset(fds, 0, sizeof(fds));
  fds[0].fd = socket;
  fds[0].events = POLLIN;
  rc = poll(fds, nfds, 0);
  if (rc < 0) {
    printf("error while polling\n"); fflush(stdout);
    exit(_PREESM_ERROR_POLLING);
  }
}

/**
 * Send 1 byte ack with value 1. Non-blocking.
 */
void preesm_send_ack(int socket) {
  char ack = 1;
  send(socket, &ack, sizeof(char), 0);
}

/**
 * Receive 1 byte ack and check its value is 1. Blocking.
 */
void preesm_receive_ack(int socket) {
  char ack = 0;
  int count = 0;

  // check how many bytes are available for read on the socket;
  ioctl(socket, FIONREAD, &count);
  // if not enough bytes are available, block until data arrive, then recheck
  while (count < sizeof(char)) {
    preesm_poll_socket_read_available(socket);
    ioctl(socket, FIONREAD, &count);
  }
  recv(socket, &ack, sizeof(char), 0);
  if (!ack) {
    printf("error ack ( = %d)\n",ack); fflush(stdout);
    exit(_PREESM_ERROR_ACK);
  }
}

/**
 * Send a packet. Non-blocking.
 */
void preesm_send_start(int from, int to, int * socketRegistry, char* buffer, int size, const char* bufferName) {
  int socket = socketRegistry[to];
  send(socket, buffer, size, 0);
}
void preesm_send_end(int from, int to, int * socketRegistry, char* buffer, int size, const char* bufferName) {
}

/**
 * Receive a packet. Blocking.
 */
void preesm_receive_start(int from, int to, int * socketRegistry, char* buffer, int size, const char* bufferName) {
  int socket = socketRegistry[from];

  int count = 0;
  // check how many bytes are available for read on the socket;
  ioctl(socket, FIONREAD, &count);
  // if not enough bytes are available, block until data arrive, then recheck
  while (count < size) {
    preesm_poll_socket_read_available(socket);
    ioctl(socket, FIONREAD, &count);
  }
  recv(socket, buffer, size, 0);
}
void preesm_receive_end(int from, int to, int * socketRegistry, char* buffer, int size, const char* bufferName) {
}

/**
 * Set some options to the socket:
 *  - send buffer size
 *  - receive buffer size
 *  - no delay
 *  - quick ack
 *  read :
 *  - https://stackoverflow.com/questions/7286592/set-tcp-quickack-and-tcp-nodelay
 *  - https://eklitzke.org/the-caveats-of-tcp-nodelay
 *  - https://www.extrahop.com/company/blog/2016/tcp-nodelay-nagle-quickack-best-practices/
 */
void preesm_set_socket_options(int socket) {
  int rv;
  int newMaxBuff=_PREESM_SOCKET_BUFFER_SIZE;
  // set send buffer size
  rv = setsockopt(socket, SOL_SOCKET, SO_SNDBUF, &newMaxBuff, sizeof(newMaxBuff));
  if (rv != 0) {
    printf("[PREESM] - Could not set send socket buffer new size [%m]\n"); fflush(stdout);
    exit(_PREESM_ERROR_CREATE_SOCKET);
  }
  // set receive buffer size
  rv = setsockopt(socket, SOL_SOCKET, SO_RCVBUF, &newMaxBuff, sizeof(newMaxBuff));
  if (rv != 0) {
    printf("[PREESM] - Could not set receive socket buffer new size [%m]\n"); fflush(stdout);
    exit(_PREESM_ERROR_CREATE_SOCKET);
  }
  int flag = 1;
  // set TCP_NODELAY
  rv = setsockopt(socket, IPPROTO_TCP, TCP_NODELAY,(char *) &flag, sizeof(int));
  if (rv != 0) {
    printf("[PREESM] - Could not set socket tcp_nodelay [%m]\n"); fflush(stdout);
    exit(_PREESM_ERROR_CREATE_SOCKET);
  }
  // set TCP_QUICKACK
#ifndef __APPLE__
  // flag still = 1
  rv = setsockopt(socket, IPPROTO_TCP, TCP_QUICKACK,(char *) &flag, sizeof(int));
  if (rv != 0) {
    printf("[PREESM] - Could not set socket tcp_nodelay [%m]\n"); fflush(stdout);
    exit(_PREESM_ERROR_CREATE_SOCKET);
  }
#endif
}

/**
 * Open client connection.
 */
int preesm_connect(int from, ProcessingElement * to) {
  char* host = to->host;
  int port = to->port;
#ifdef _PREESM_TCP_DEBUG_
  printf("[TCP-DEBUG] %d connects to %d (%s:%d)\n", from, to->id, host, port); fflush(stdout);
#endif

  char portString[6];
  sprintf(portString,"%d",port);
  int sockfd;
  struct addrinfo hints, *servinfo;
  int rv;
  memset(&hints, 0, sizeof(struct addrinfo));
  hints.ai_family = AF_INET;
  hints.ai_socktype = SOCK_STREAM;

  if ((rv = getaddrinfo(host, portString, &hints, &servinfo)) != 0) {
    printf("[PREESM] - Could not resolve host [%m]\n"); fflush(stdout);
    exit(_PREESM_ERROR_RESOLVE_HOST);
  }
  if ((sockfd = socket(servinfo->ai_family, servinfo->ai_socktype, servinfo->ai_protocol)) < 0) {
    printf("[PREESM] - Could not create socket [%m]\n"); fflush(stdout);
    exit(_PREESM_ERROR_CREATE_SOCKET);
  }

  preesm_set_socket_options(sockfd);

  while (connect(sockfd, servinfo->ai_addr, servinfo->ai_addrlen) < 0) {
#ifdef _PREESM_TCP_DEBUG_
  printf("[TCP-DEBUG] %d connects to %d -- Waiting %d us [%m]\n", from, to->id, _PREESM_WAIT_SERVER_START_US); fflush(stdout);
#endif
    // wait for the server to start
    usleep(_PREESM_WAIT_SERVER_START_US);
  }
  preesm_receive_ack(sockfd);
  preesm_send_ack(sockfd);

  freeaddrinfo(servinfo);
  return sockfd;
}

/**
 * Open connection from listening socket.
 */
int preesm_accept(int listeningSocket) {
#ifdef _PREESM_TCP_DEBUG_
  printf("[TCP-DEBUG] preesm accept\n"); fflush(stdout);
#endif
  struct sockaddr_in clientAddress;
  socklen_t clientAddressLength = sizeof(clientAddress);
  int connfd = accept(listeningSocket, (struct sockaddr *)&clientAddress, &clientAddressLength);

  preesm_set_socket_options(connfd);

  preesm_send_ack(connfd);
  preesm_receive_ack(connfd);

#ifdef _PREESM_TCP_DEBUG_
  printf("[TCP-DEBUG] accepted connection\n"); fflush(stdout);
#endif
  return connfd;
}

/**
 * Open listening socket.
 * This new version tries to bind to the same port as PE #0.
 * Increases the port number when it fails;
 */
int preesm_listen(int processingElementID, int numberOfProcessingElements, ProcessingElement registry[numberOfProcessingElements]) {
  int listeningProcessingElementID = processingElementID;
  int port = registry[0].port - 1;
#ifdef _PREESM_TCP_DEBUG_
  printf("[TCP-DEBUG] PE %d opening listen socket on %d\n", listeningProcessingElementID, port+1); fflush(stdout);
#endif
  int sockfd = socket(AF_INET, SOCK_STREAM, 0);
  setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &(int){ 1 }, sizeof(int));

  preesm_set_socket_options(sockfd);

  if(sockfd < 0) {
    printf("[PREESM] - Error : Could not create listen socket [%m]\n"); fflush(stdout);
    exit(_PREESM_ERROR_CREATE_SOCKET);
  }

  int res = -1;
  do {
    port++;
    struct sockaddr_in listeningIPPort;
    listeningIPPort.sin_family = AF_INET;
    listeningIPPort.sin_addr.s_addr = INADDR_ANY;
    listeningIPPort.sin_port = htons(port);
    res = bind(sockfd, (struct sockaddr*)&(listeningIPPort), sizeof(struct sockaddr_in));
  } while (res < 0);

  //listen to all higher IDs
  listen(sockfd, numberOfProcessingElements - listeningProcessingElementID - 1);
#ifdef _PREESM_TCP_DEBUG_
  printf("[TCP-DEBUG] PE %d listening on %d\n", listeningProcessingElementID, port); fflush(stdout);
#endif
  // store port value
  registry[processingElementID].port = port;
  return sockfd;
}


void preesm_open_main(int* socketFileDescriptors, int processingElementID, int numberOfProcessingElements, ProcessingElement registry[numberOfProcessingElements]) {
  // processing element 0 listens to all other, accept their connections;
  // using connection data, it populates the registry and send it back to the other PEs.


  int socketTmpFDs[numberOfProcessingElements];
  // 1- create listen socket, put it in local array of socket at processingElementID index
  socketTmpFDs[0] = preesm_listen(processingElementID, numberOfProcessingElements, registry);
  if (socketTmpFDs[0] == -1) {
    exit(-1);
  }

  // 2- accept connection from all other who have higher ID
  for (int i = 1; i < numberOfProcessingElements; i++) {
    int socket = preesm_accept(socketTmpFDs[0]);
    socketTmpFDs[i] = socket;
  }

  // 3- other PEs are opening binding socket to get the port number

  // 4- receive PEs ID and port (and set proper indexes in socketFileDescriptors)
  socketFileDescriptors[0] = socketTmpFDs[0];
  for (int i = 1; i < numberOfProcessingElements; i++) {
    int socket = socketTmpFDs[i];
    int data[2];

    int count = 0;
    // check how many bytes are available for read on the socket;
    ioctl(socket, FIONREAD, &count);
    // if not enough bytes are available, block until data arrive, then recheck
    while (count < sizeof(int)*2) {
      preesm_poll_socket_read_available(socket);
      ioctl(socket, FIONREAD, &count);
    }

    recv(socket, data, sizeof(int)*2, 0);
    int id = data[0];
    int port = data[1];
    struct sockaddr_in clientAddress;
    socklen_t clientAddressLength = sizeof(clientAddress);
    getpeername(socket, (struct sockaddr *)&clientAddress, &clientAddressLength);
    char *ip = inet_ntoa(clientAddress.sin_addr);
    registry[id].id = id;
    registry[id].port = port;
    strcpy(registry[id].host, ip);
    socketFileDescriptors[id] = socket;
  }

  // 5- send complete registry to all other
  for (int i = 1; i < numberOfProcessingElements; i++) {
    int socket = socketFileDescriptors[i];
    send(socket, registry, sizeof(ProcessingElement)*numberOfProcessingElements, 0);
    preesm_receive_ack(socket);
    preesm_send_ack(socket);
  }
}
void preesm_open_secondaries(int* socketFileDescriptors, int processingElementID, int numberOfProcessingElements, ProcessingElement registry[numberOfProcessingElements]) {
  // secondaries processing elements connect to PE #0, then wait for the registry;
  // note: when calling the first entry of the registry is already filled using config file.

  // 1- Main PE creates socket

  // 2- connect to main
	int socket = preesm_connect(processingElementID, &registry[0]);
  socketFileDescriptors[0] = socket;

  // 3- bind port
  socketFileDescriptors[processingElementID] = preesm_listen(processingElementID, numberOfProcessingElements, registry);

  // 4- send known ID and Port to PE #0
  int data[2];
  data[0] = processingElementID;
  data[1] = registry[processingElementID].port;
  send(socket, data, sizeof(int)*2, 0);

#ifdef _PREESM_TCP_DEBUG_
  printf("[TCP-DEBUG] %d receiving registry ...\n", processingElementID); fflush(stdout);
#endif
  // 5- receive registry
  int count = 0;
  // check how many bytes are available for read on the socket;
  ioctl(socket, FIONREAD, &count);
  // if not enough bytes are available, block until data arrive, then recheck
  while (count < sizeof(ProcessingElement)*numberOfProcessingElements) {
    preesm_poll_socket_read_available(socket);
    ioctl(socket, FIONREAD, &count);
  }
  recv(socket, registry, sizeof(ProcessingElement)*numberOfProcessingElements, 0);

  preesm_send_ack(socket);
  preesm_receive_ack(socket);

#ifdef _PREESM_TCP_DEBUG_
  printf("[TCP-DEBUG] %d received registry\n", processingElementID); printRegistry(numberOfProcessingElements, registry); fflush(stdout);
#endif

  // 6- connect to all other who have lower ID
  for (int i = processingElementID-1; i > 0 ; i--) {
    socketFileDescriptors[i] = preesm_connect(processingElementID, &registry[i]);
  }
  // 7- accept connection from all other who have higher ID
  for (int i = processingElementID+1; i < numberOfProcessingElements; i++) {
    socketFileDescriptors[i] = preesm_accept(socketFileDescriptors[processingElementID]);
  }
}
void preesm_open(int* socketFileDescriptors, int processingElementID, int numberOfProcessingElements, ProcessingElement registry[numberOfProcessingElements]) {
  if (processingElementID == 0) {
    preesm_open_main(socketFileDescriptors, processingElementID, numberOfProcessingElements, registry);
  } else {
    preesm_open_secondaries(socketFileDescriptors, processingElementID, numberOfProcessingElements, registry);
  }

#ifdef _PREESM_TCP_DEBUG_
  // test that the processing element #i has the proper id;
  for (int i = 0; i < numberOfProcessingElements; i++) {
    if (i != processingElementID)
      preesm_send_start(processingElementID, i, socketFileDescriptors, (char*)&processingElementID, sizeof(int), "");
  }
  for (int i = 0; i < numberOfProcessingElements; i++) {
    if (i != processingElementID) {
      int id;
      preesm_receive_start(i, processingElementID, socketFileDescriptors, (char*)&id, sizeof(int), "");
      if (id != i) {
        printf("\n ** ERROR from %d ** \n", processingElementID);fflush(stdout);
        exit(-1);
      }
    }
  }
#endif
}


/**
 * Close connection to all other processing elements.
 */
void preesm_close(int * socketRegistry, int processingElementID, int numberOfProcessingElements) {
#ifdef _PREESM_TCP_DEBUG_
  printf("[TCP-DEBUG] %d closing connections\n", processingElementID); fflush(stdout);
#endif
  // 1- close "client" connection
  for (int i = processingElementID-1; i >= 0 ; i--) {
    preesm_send_ack(socketRegistry[i]);
    close(socketRegistry[i]);
  }
  // 2- close accepted ("server") connection
  for (int i = processingElementID+1; i < numberOfProcessingElements; i++) {
    preesm_receive_ack(socketRegistry[i]);
    close(socketRegistry[i]);
  }
  // 3- close listening socket
  close(socketRegistry[processingElementID]);
#ifdef _PREESM_TCP_DEBUG_
  printf("[TCP-DEBUG] %d done closing connections\n", processingElementID);
#endif
}

/**
 * Barrier using TCP consists in sending an ACK to everyone then block on the receive of everyone ACK;
 */
void preesm_barrier(int * socketRegistry, int processingElementID, int numberOfProcessingElements) {
#ifdef _PREESM_TCP_DEBUG_
  //printf("[TCP-DEBUG] %d at barrier - sync of %d PEs\n", processingElementID, numberOfProcessingElements);
#endif
  for (int i = 0; i < numberOfProcessingElements; i++) {
	if (i != processingElementID)
      preesm_send_ack(socketRegistry[i]);
  }
  for (int i = 0; i < numberOfProcessingElements; i++) {
	if (i != processingElementID)
      preesm_receive_ack(socketRegistry[i]);
  }
#ifdef _PREESM_TCP_DEBUG_
  //printf("[TCP-DEBUG] %d Passed barrier\n", processingElementID);
#endif
}

