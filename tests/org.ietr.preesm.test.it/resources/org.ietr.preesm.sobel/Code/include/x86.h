/*
	============================================================================
	Name        : x86.h
	Author      : kdesnos
	Version     :
	Copyright   :
	Description :
	============================================================================
*/

#ifndef X86_H
#define X86_H

#define VERBOSE
	
#ifdef _WIN32
#include <windows.h>
#include <pthread.h>
#else
// For Linux
// Pthread barriers are defined in POSIX 2001 version
// For the 1990 revision compliance the defined value of _POSIX_VERSION should be 1.
// For the 1995 revision compliance the defined value of _POSIX_VERSION should be 199506L.
// For the 2001 revision compliance the defined value of _POSIX_VERSION should be 200112L.
#define _POSIX_C_SOURCE 200112L
#define _XOPEN_SOURCE 600
#include <pthread.h>
#endif

#include <stdlib.h>
#include <stdio.h>

#include <string.h>
#include <semaphore.h>

#include "communication.h"
#include "fifo.h"
#include "dump.h"

#include "yuvRead.h"
#include "yuvDisplay.h"
#include "sobel.h"
#include "splitMerge.h"

typedef unsigned char uchar;

void *computationThread_Core0(void *arg);
void *computationThread_Core1(void *arg);
void *computationThread_Core2(void *arg);
void *computationThread_Core3(void *arg);
void *computationThread_Core4(void *arg);
void *computationThread_Core5(void *arg);
void *computationThread_Core6(void *arg);
void *computationThread_Core7(void *arg);

#endif
