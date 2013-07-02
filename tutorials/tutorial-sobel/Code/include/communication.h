/*
	============================================================================
	Name        : communication.h
	Author      : kdesnos
	Version     :
	Copyright   :
	Description :
	============================================================================
*/

#ifndef COMMUNICATION_H
#define COMMUNICATION_H

#include <pthread.h>
#include <semaphore.h>

void sendStart(sem_t* sem);

void sendEnd();

void sendReserve(sem_t* sem);

void receiveStart();

void receiveEnd(sem_t* sem);

void receiveRelease(sem_t* sem);

#endif
