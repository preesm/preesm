/*
	============================================================================
	Name        : communication.c
	Author      : kdesnos
	Version     : 1.0
	Copyright   : CECILL-C
	Description : sending and receiving data via shared memory
	============================================================================
*/

#include "x86.h"

void sendStart(sem_t* sem){
    sem_post(sem);
}

void sendEnd(){}

void receiveStart(){}

void receiveEnd(sem_t* sem){
    sem_wait(sem);
}
