/*
 * x86.h
 *
 *  Created on: 4 mars 2009
 *      Author: mpelcat
 */

#ifndef X86_H_
#define X86_H_

#include <stdio.h>
#include <stdlib.h>
#include <windows.h>

#define semaphore HANDLE

#define x86SharedRAM 0
#define Core1 1
#define Core2 2
#define Core3 3
#define Core4 4
#define Core5 5
#define Core6 6
#define Core7 7
#define Core8 8

#include "testcomSources.h"

// Necessary for semaphore release
LONG previous ;

#endif /* X86_H_ */
