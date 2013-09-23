/*
	============================================================================
	Name        : main.c
	Author      : kdesnos
	Version     :
	Copyright   :
	Description :
	============================================================================
*/

#include "x86.h"
#include <windows.h>

pthread_barrier_t iter_barrier;
int stopThreads;


int main(void)
{
    pthread_t threadCore0;
    pthread_t threadCore1;
    pthread_t threadCore2;
    pthread_t threadCore3;

    stopThreads = 0;
    pthread_barrier_init(&iter_barrier, NULL, 4);

    pthread_create(&threadCore0, NULL, computationThread_Core0, NULL);
    pthread_create(&threadCore1, NULL, computationThread_Core1, NULL);
    pthread_create(&threadCore2, NULL, computationThread_Core2, NULL);
    pthread_create(&threadCore3, NULL, computationThread_Core3, NULL);

    pthread_join(threadCore0,NULL);
    pthread_join(threadCore1,NULL);
    pthread_join(threadCore2,NULL);
    pthread_join(threadCore3,NULL);
    system("PAUSE");
    exit(0);
}