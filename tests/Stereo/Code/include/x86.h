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
#include "dump.h"

#include "ppm.h"
#include "displayRGB.h"
#include "rgb2Gray.h"
#include "census.h"
#include "costConstruction.h"
#include "disparityGen.h"
#include "offsetGen.h"
#include "aggregateCost.h"
#include "computeWeights.h"
#include "disparitySelect.h"
#include "medianFilter.h"
#include "splitMerge.h"

#include "sink.h"

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
