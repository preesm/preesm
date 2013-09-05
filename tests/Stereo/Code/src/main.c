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


int main(void)
{
    pthread_t threadCore0;
    pthread_t threadCore1;
    pthread_t threadCore2;
    pthread_t threadCore3;
    pthread_t threadCore4;
    pthread_t threadCore5;
    pthread_t threadCore6;
    pthread_t threadCore7;

    pthread_barrier_init(&iter_barrier, NULL, 1);

    pthread_create(&threadCore0, NULL, computationThread_Core0, NULL);
    //pthread_create(&threadCore1, NULL, computationThread_Core1, NULL);
    //pthread_create(&threadCore2, NULL, computationThread_Core2, NULL);
    //pthread_create(&threadCore3, NULL, computationThread_Core3, NULL);
    //pthread_create(&threadCore4, NULL, computationThread_Core4, NULL);
    //pthread_create(&threadCore5, NULL, computationThread_Core5, NULL);
    //pthread_create(&threadCore6, NULL, computationThread_Core6, NULL);
    //pthread_create(&threadCore7, NULL, computationThread_Core7, NULL);

    pthread_join(threadCore0,NULL);
    system("PAUSE");
    exit(0);
}
