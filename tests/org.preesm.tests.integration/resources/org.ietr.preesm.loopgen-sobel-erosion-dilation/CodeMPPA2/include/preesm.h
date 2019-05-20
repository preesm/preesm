/*
	============================================================================
	Name        : mppa_preesm.h
	Author      : kdesnos, jhascoet
	Version     :
	Copyright   :
	Description :
	============================================================================
*/

#ifndef MPPA_PREESM_H
#define MPPA_PREESM_H


#include <stdlib.h>
#include <stdio.h>

// Using nodeos POSIX pthread are available 
#include <pthread.h>

#if 1
#define GENERATE_FILE
#define PREESM_LOOP_SIZE 300
#endif

#if 0
//d#define GENERATE_FILE
#define PREESM_LOOP_SIZE 3
#endif

#define _PREESM_PAPIFY_MONITOR

//#define DISABLE_IO

#define IN
#define OUT

//#define PROFILE
#define PREESM_NB_CORES_CC 10
#define PREESM_NB_CORES_IO 3

/* user include */
#include "sobel.h"
#include "morph.h"
#include "medianFilter.h"
#include "yuvRead.h"
#include "yuvDisplay.h"

/* user defined definition */
//#define Shared ((char*)0x90000000ULL) 	/* Shared buffer in DDR */

typedef unsigned char uchar;

#endif
