/*
	============================================================================
	Name        : clock.c
	Author      : mpelcat
	Version     : 1.0
	Copyright   : CECILL-C
	Description : Timing primitive for Preesm Codegen.
	============================================================================
*/

#include "clock.h"


#ifdef _WIN32
// clock is not precise at all
#include <windows.h>                // for Windows APIs
#include <stdio.h>
#else
#include <stdio.h>
#include <sys/time.h>                // for gettimeofday()
#endif


#ifdef _WIN32
LARGE_INTEGER startTimes[MAX_STAMPS];
#else
struct timeval startTimes[MAX_STAMPS];
#endif

double elapsedTimes[MAX_STAMPS];

// Starting to record time for a given stamp
void startTiming(int stamp){
#ifdef _WIN32
	QueryPerformanceCounter(&startTimes[stamp]);
#else 
    gettimeofday(&startTimes[stamp], NULL);
#endif
}

// Stoping to record time for a given stamp. Returns the time in us
unsigned int stopTiming(int stamp){
	unsigned int elapsedus = 0;
#ifdef _WIN32
	LARGE_INTEGER frequency;
	LARGE_INTEGER t2;
	QueryPerformanceCounter(&t2);

    // get ticks per second
    QueryPerformanceFrequency(&frequency);

    // compute and print the elapsed time in millisec
    elapsedTimes[stamp] = (t2.QuadPart - startTimes[stamp].QuadPart) * 1000.0 / frequency.QuadPart;
#else
	struct timeval t2;

	gettimeofday(&t2, NULL);

    // compute and print the elapsed time in millisec
    elapsedTimes[stamp] = (t2.tv_sec - startTimes[stamp].tv_sec) * 1000.0;      // sec to ms
    elapsedTimes[stamp] += (t2.tv_usec - startTimes[stamp].tv_usec) / 1000.0;   // us to ms
#endif

    elapsedus = (int)(elapsedTimes[stamp]*1000);
    return elapsedus;
}
