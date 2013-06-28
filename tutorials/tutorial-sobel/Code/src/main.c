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

int main(void)
{
    //pthread_t threadCore0;
    //pthread_create(&threadCore0, NULL, computationThread_Core0, NULL);
    
    (*computationThread_Core0)((void*)NULL);
    while(1){


    }
}
