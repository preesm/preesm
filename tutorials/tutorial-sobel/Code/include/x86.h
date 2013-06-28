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

#include "communication.h"
#include "fifo.h"

#include "readYUV.h"
#include "displayYUV.h"
#include "sobel.h"
#include "splitMerge.h"

typedef unsigned char uchar;

void *computationThread_Core0(void *arg);

#endif
