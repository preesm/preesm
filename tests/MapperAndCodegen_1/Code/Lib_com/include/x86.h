/*
 * x86.h
 *
 *  Created on: 4 mars 2009
 *      Author: mpelcat
 */

#ifndef X86_H_
#define X86_H_

//#include <stdio.h>
//#include <stdlib.h>
#include <windows.h>

#define semaphore HANDLE

#define MEDIUM_SEND 0
#define MEDIUM_RCV 1
#define TCP 100



#define Core0 0
#define Core1 1
#define Core2 2
#define Core3 3
#define Core4 4
#define Core5 5
#define Core6 6
#define Core7 7

#include "OS_Com.h"
#include "PC_x86_SEM.h"

#define CORE_NUMBER 8


#endif /* X86_H_ */
