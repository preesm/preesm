/*
	============================================================================
	Name        : main.c
	Author      : kdesnos
	Version     :
	Copyright   : CECILL-C
	Description :
	============================================================================
*/




#include "x86.h"


pthread_barrier_t iter_barrier;
int stopThreads;


int main(void)
{

    // Declaring thread pointers
#ifdef X1_CORE
    pthread_t threadCore0;
#else
#ifdef X4_CORES
    pthread_t threadCore0;
    pthread_t threadCore1;
    pthread_t threadCore2;
    pthread_t threadCore3;
#else
#ifdef X8_CORES
    pthread_t threadCore0;
    pthread_t threadCore1;
    pthread_t threadCore2;
    pthread_t threadCore3;
    pthread_t threadCore4;
    pthread_t threadCore5;
    pthread_t threadCore6;
    pthread_t threadCore7;
#else
#ifdef VERBOSE
    printf("Error: no number of cores defined\n");
#endif
#endif // 8_CORES
#endif // 4_CORES
#endif // 1_CORE

#ifdef VERBOSE
    printf("Launched main\n");
#endif

// Creating a synchronization barrier
    stopThreads = 0;
#ifdef X1_CORE
    pthread_barrier_init(&iter_barrier, NULL, 1);
#else
#ifdef X4_CORES
    pthread_barrier_init(&iter_barrier, NULL, 4);
#else
#ifdef X8_CORES
    pthread_barrier_init(&iter_barrier, NULL, 8);
#endif // 8_CORES
#endif // 4_CORES
#endif // 1_CORE

    // Creating threads
#ifdef X1_CORE
    pthread_create(&threadCore0, NULL, computationThread_Core0, NULL);
#else
#ifdef X4_CORES
    pthread_create(&threadCore0, NULL, computationThread_Core0, NULL);
    pthread_create(&threadCore1, NULL, computationThread_Core1, NULL);
    pthread_create(&threadCore2, NULL, computationThread_Core2, NULL);
    pthread_create(&threadCore3, NULL, computationThread_Core3, NULL);
#else
#ifdef X8_CORES
    pthread_create(&threadCore0, NULL, computationThread_Core0, NULL);
    pthread_create(&threadCore1, NULL, computationThread_Core1, NULL);
    pthread_create(&threadCore2, NULL, computationThread_Core2, NULL);
    pthread_create(&threadCore3, NULL, computationThread_Core3, NULL);
    pthread_create(&threadCore4, NULL, computationThread_Core4, NULL);
    pthread_create(&threadCore5, NULL, computationThread_Core5, NULL);
    pthread_create(&threadCore6, NULL, computationThread_Core6, NULL);
    pthread_create(&threadCore7, NULL, computationThread_Core7, NULL);
#endif // 8_CORES
#endif // 4_CORES
#endif // 1_CORE

// Waiting for thread terminations
#ifdef X1_CORE
    pthread_join(threadCore0,NULL);
#else
#ifdef X4_CORES
    pthread_join(threadCore0,NULL);
    pthread_join(threadCore1,NULL);
    pthread_join(threadCore2,NULL);
    pthread_join(threadCore3,NULL);
#else
#ifdef X8_CORES
    pthread_join(threadCore0,NULL);
    pthread_join(threadCore1,NULL);
    pthread_join(threadCore2,NULL);
    pthread_join(threadCore3,NULL);
    pthread_join(threadCore4,NULL);
    pthread_join(threadCore5,NULL);
    pthread_join(threadCore6,NULL);
    pthread_join(threadCore7,NULL);
#endif // 8_CORES
#endif // 4_CORES
#endif // 1_CORE



#ifdef VERBOSE
    printf("Press any key to stop application\n");
#endif

    // Waiting for the user to end the procedure
    getchar();
    exit(0);
}
