
#[[#]]#include "socketcom.h"
#[[#]]#include <pthread.h>

ProcessingElement registry[NB_PE];

void actualThreadComputations(int processingElementID) {
  int socketFileDescriptors[NB_PE];
  preesm_open(socketFileDescriptors, processingElementID, NB_PE, registry);

  // computation / comm
  switch (processingElementID) {
    case 0:
      {
        char data[23] = "some message for 0000";
        for (int i = 2; i < NB_PE-2; i++) {
        sprintf(data,"some message for %4d",i);
          preesm_send(i,socketFileDescriptors,data, 22);
        }
        for (int i = 2; i < NB_PE-2; i++) {
          preesm_receive(i,socketFileDescriptors,data, 4);
          printf("ack from %4d : %s\n",i,data);
        }
        break;
      }
    case 1:
    case NB_PE - 1:
    case NB_PE - 2:
      break;
    default:
      {
        char buffer[23];
        memset(buffer, 0, 23);
        preesm_receive(0,socketFileDescriptors,buffer, 22);
        printf("%4d -> [%s]\n", processingElementID, buffer);
        char data[23] = "some message for 0000";
        sprintf(data,"some message for %4d",processingElementID);
        int cmp = strcmp (buffer, data);
        if (cmp != 0) {
          printf("%d error : received message is wrong\n", processingElementID);
          exit (-1);
        }

        sprintf(data,"%4d",processingElementID);
        preesm_send(0,socketFileDescriptors,data, 4);
        break;
      }
  }
  preesm_close(socketFileDescriptors, processingElementID, NB_PE);
}

void * threadComputations (void *arg) {
  int processingElementID = *((int*) arg);
  actualThreadComputations(processingElementID);
  return NULL;
}

int main(int argc, char** argv) {
  // TODO overide pes array with values from config file/arguments
  for (int i = 0; i < NB_PE; i++) {
    registry[i].id = i;
    registry[i].host = "127.0.0.1";
    registry[i].port=(PREESM_COM_PORT+i);
  }
  if (argc == 2) {
    // read ID from arguments
    int peId = atoi(argv[1]);
    actualThreadComputations(peId);
  } else {
    // launch all IDs in separate threads
    pthread_t threads[NB_PE];
    int threadArguments[NB_PE];
    for (int i = 0; i < NB_PE; i++) {
      threadArguments[i] = i;
      pthread_create(&threads[i], NULL, threadComputations, &(threadArguments[i]));
    }
    for (int i = 0; i < NB_PE; i++) {
      pthread_join(threads[i], NULL);
    }
  }
  return 0;
}



