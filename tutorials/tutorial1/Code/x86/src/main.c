/*
	============================================================================
	Name        : main.c
	Author      : kdesnos
	Version     : 1.0
	Copyright   : CECILL-C
	Description : Launching the threads of the application
	============================================================================
*/




#include "x86.h"


pthread_barrier_t iter_barrier;
int stopThreads;


int main(void)
{

    // Declaring thread pointers
    pthread_t threadCore0;
    pthread_t threadCore1;

#ifdef VERBOSE
    printf("Launched main\n");
#endif

// Creating a synchronization barrier
    pthread_barrier_init(&iter_barrier, NULL, 2);

    // Creating threads
    pthread_create(&threadCore0, NULL, computationThread_Core0, NULL);
    pthread_create(&threadCore1, NULL, computationThread_Core1, NULL);

// Waiting for thread terminations
    pthread_join(threadCore0,NULL);
    pthread_join(threadCore1,NULL);



#ifdef VERBOSE
    printf("Press any key to stop application\n");
#endif

    // Waiting for the user to end the procedure
    getchar();
    exit(0);
}
