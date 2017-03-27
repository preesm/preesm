#ifndef X86SETUP
#define X86SETUP

#include <windows.h>
#include "../Visual/Lib_com/include/OS_Com.h"
#include "../Visual/Lib_com/include/PC_x86_SEM.h"

//#include <stdio.h>
//#include <stdlib.h>
#include <windows.h>

#define semaphore HANDLE

#define MEDIUM_SEND 0
#define MEDIUM_RCV 1
#define TCP 100

/* ID of the core for PC */
#define Core0 	0
#define Core1 	1
#define Core2	2
#define Core3 	3
#define Core4 	4
#define Core5 	5
#define Core6 	6
#define Core7 	7

#define MEDIA_NR 	8
#define CORE_NUMBER 8

Medium Media[MEDIA_NR][MEDIA_NR];
semaphore sem_init[MEDIA_NR][MEDIA_NR];

#endif