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

/* ID of the core for DSP */
#define C64_1 	0
#define C64_2 	1
#define C64_3	2
#define C64_4 	3
#define C64_5 	4
#define C64_6 	5
#define C64_7 	6
#define C64_8 	7
/* ID of the core for PC */
#define x86 	0

#define NbCore 	3	// Nb of core by dsp
#define MEDIA_NR 	10

#define CORE_NUMBER 1

#include "OS_Com.h"
#include "PC_x86_SEM.h"

#define CORE_NUMBER 1


#endif /* X86_H_ */
