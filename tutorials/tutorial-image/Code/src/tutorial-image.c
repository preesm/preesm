/*
	============================================================================
	Name        : tutorial-image.c
	Author      : mpelcat
	Version     :
	Copyright   : Main file calling PREESM generated code
	Description :
	============================================================================
*/
#include "x86.h"

#undef main

extern void * computationThread_Core0( void *arg );
extern void * computationThread_Core1( void *arg );
extern void * computationThread_Core2( void *arg );
extern void * computationThread_Core3( void *arg );
extern void * computationThread_Core4( void *arg );
extern void * computationThread_Core5( void *arg );
extern void * computationThread_Core6( void *arg );
extern void * computationThread_Core7( void *arg );

int main(void)
{
	void * (*routine[CORE_NUMBER])(void *arg);

	int i;

	routine[0] = computationThread_Core0;
	routine[1] = computationThread_Core1;
	routine[2] = computationThread_Core2;
	routine[3] = computationThread_Core3;
	routine[4] = computationThread_Core4;
	routine[5] = computationThread_Core5;
	routine[6] = computationThread_Core6;
	routine[7] = computationThread_Core7;

	for(i=0;i<CORE_NUMBER;i++){
		pthread_t thread; 
		
		if(pthread_create(&thread, NULL, routine[i], (void *)"1") != 0 ) {
			return -1;
		}
	}

	for(i=0;i<1000000000;i++){
		i--;
	}

	return 0;
}

// Initializing several semaphores
int sems_init(sem_t *sem, int number){
	int i;

	for(i=0; i<number; i++){
		if(sem_init(&sem[i], 0, 0) == -1){
			return -1;
		}
	}
	return 0;
}

