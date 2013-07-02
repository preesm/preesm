/*
	============================================================================
	Name        : communication.c
	Author      : kdesnos
	Version     :
	Copyright   :
	Description :
	============================================================================
*/


#include "../include/communication.h"
#include <stdio.h>

void sendStart(sem_t* sem){
    sem_post(sem);
}

void sendEnd(){}

void sendReserve(sem_t* sem){
    int err = sem_wait(sem);
    if(err == -1){
        printf("error SendReserve\n");
        //switch(err){

         //   case EAGAIN
        //}
    }
}

void receiveStart(){}

void receiveEnd(sem_t* sem){
    sem_wait(sem);
}

void receiveRelease(sem_t* sem){
    sem_post(sem);
}

