/*
	============================================================================
	Name        : communication.c
	Author      : kdesnos
	Version     : 2.1
	Copyright   : CECILL-C
	Description :
	============================================================================
*/

#include <pthread.h>
#include <semaphore.h>

#include "x86.h"
#include "communication.h"

// 8 local semaphore for each core (1 useless per core)
sem_t interCoreSem[MAX_NB_CORES][MAX_NB_CORES];

void communicationInit(){
	int i, j;
	for (i = 0; i < MAX_NB_CORES; i++){
		for (j = 0; j < MAX_NB_CORES; j++){
			sem_init(&interCoreSem[i][j], 0, 0);
		}
	}
}

void sendStart(int senderID, int receiverID){
#ifndef NDEBUG
	if(senderID >= MAX_NB_CORES || receiverID >= MAX_NB_CORES){
		printf("Number of core exceeds the limit specified in communication.h. \n Update MAX_NB_CORES.");
		exit(-1);
	}

#endif
	sem_post(&interCoreSem[receiverID][senderID]);
}

void sendEnd(){}

void receiveStart(){}

void receiveEnd(int senderID, int receiverID){
	sem_wait(&interCoreSem[receiverID][senderID]);
}
