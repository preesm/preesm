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
    LARGE_INTEGER time;
    pthread_t threadCore0;
    pthread_t threadCore1;
    pthread_t threadCore2;
    pthread_t threadCore3;
    int i = 0;
/*
    for(i=0; i<1000;i++){
        QueryPerformanceCounter(&time);
        printf("%ld ", time );
    }
*/
    system("PAUSE");
    pthread_barrier_init(&iter_barrier, NULL, 4);


    pthread_create(&threadCore0, NULL, computationThread_Core0, NULL);
    pthread_create(&threadCore1, NULL, computationThread_Core1, NULL);
    pthread_create(&threadCore2, NULL, computationThread_Core2, NULL);
    pthread_create(&threadCore3, NULL, computationThread_Core3, NULL);

    pthread_join(threadCore0,NULL);
    exit(0);
}
