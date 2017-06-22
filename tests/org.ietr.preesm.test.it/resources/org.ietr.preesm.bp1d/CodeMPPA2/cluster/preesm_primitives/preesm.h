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
#include <string.h>

// Using nodeos POSIX pthread are available 
#include <pthread.h>
#include <semaphore.h>

#ifndef __k1io__
#include "communication.h"
#include "fifo.h"
#include "clock.h"
#include "splitMerge.h"
#endif

#define SHARED_SEGMENT_ID (10)

#ifdef __bare__
#include "utask.h"
#else
#endif
#define NB_OMP_CORE (16)

#define GRAPH_ITERATION 1

#define VERBOSE

#include "../define.h"

//#define DISABLE_IO

//#define PATH "/home/user/Temp/akiyo_cif.yuv"
//#define PATH "/home/accesscore/jhascoet/akiyo_cif.yuv"
#define PATH "/home/accesscore/jhascoet/akiyo_cif_640x480.yuv"
#if 0
#define VIDEO_WIDTH 352
#define VIDEO_HEIGHT 288
#endif
#if 1
#define VIDEO_WIDTH 640
#define VIDEO_HEIGHT 480
#endif

#define IN
#define OUT

//#define PROFILE
#define NB_CORE 1

/* user include */
#ifndef __k1io__
#include "sobel.h"
#include "morph.h"
#include "medianFilter.h"
#include "yuvRead.h"
#include "yuvDisplay.h"
#endif

/* user defined definition */
//#define Shared ((char*)0x90000000ULL) 	/* Shared buffer in DDR */
#define Shared (0x90000000ULL) 	/* Shared buffer in DDR */

typedef unsigned char uchar;

#endif
