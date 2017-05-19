/*
	============================================================================
	Name        : communication.c
	Author      : kdesnos
	Version     : 1.0
	Copyright   : CECILL-C
	Description : 
	============================================================================
*/


#include "../include/communication.h"
#include <stdio.h>

void sendStart(sem_t* sem){
    sem_post(sem);
}

void sendEnd(){}

void receiveStart(){}

void receiveEnd(sem_t* sem){
    sem_wait(sem);
}
