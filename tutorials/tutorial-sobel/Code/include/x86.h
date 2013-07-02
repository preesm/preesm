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

#include <string.h>
#include <pthread.h>
#include <semaphore.h>

#include "communication.h"
#include "fifo.h"

#include "readYUV.h"
#include "displayYUV.h"
#include "sobel.h"
#include "splitMerge.h"

typedef unsigned char uchar;

void *computationThread_Core0(void *arg);
void *computationThread_Core1(void *arg);
void *computationThread_Core2(void *arg);
void *computationThread_Core3(void *arg);

#endif
