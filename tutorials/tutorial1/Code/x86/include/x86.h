/*
	============================================================================
	Name        : x86.h
	Author      : mpelcat
	Version     :
	Copyright   : A few includes and defines necessary for pthread synchro and 
					TCP communication
	Description :
	============================================================================
*/

#ifndef X86_H_
#define X86_H_

#include <stdio.h>
#include <stdlib.h>
#include "pthread.h"
#include "semaphore.h"
#include "communication.h" // TCP communication wrapper

// Defining posix semaphore type
#define semaphore sem_t

/* ID of the cores for PC */
#define CORE_NUMBER 2

#define Core0 	0
#define Core1 	1

// Initializing several semaphores
int sems_init(sem_t *sem, int number);

// Application specific sources
#include "testcomSources.h"

#endif /* X86_H_ */
