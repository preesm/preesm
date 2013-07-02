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

pthread_barrier_t init_barrier;
pthread_barrier_t iter_barrier;

int main(void)
{
    pthread_t threadCore1;
    pthread_t threadCore2;
    pthread_t threadCore3;

    pthread_barrier_init(&init_barrier, NULL, 4);
    pthread_barrier_init(&iter_barrier, NULL, 4);


    pthread_create(&threadCore1, NULL, computationThread_Core1, NULL);
    pthread_create(&threadCore2, NULL, computationThread_Core2, NULL);
    pthread_create(&threadCore3, NULL, computationThread_Core3, NULL);

    (*computationThread_Core0)((void*)NULL);
}
