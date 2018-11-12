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


#include <stdio.h>
#include <sys/time.h>                // for gettimeofday()


struct timeval startTimes[MAX_STAMPS];
double elapsedTimes[MAX_STAMPS];

// Starting to record time for a given stamp
void startTiming(int stamp){
    gettimeofday(&startTimes[stamp], NULL);
}

// Stoping to record time for a given stamp. Returns the time in us
unsigned int stopTiming(int stamp){
  unsigned int elapsedus = 0;
  struct timeval t2;

  gettimeofday(&t2, NULL);

    // compute and print the elapsed time in millisec
    elapsedTimes[stamp] = (t2.tv_sec - startTimes[stamp].tv_sec) * 1000.0;      // sec to ms
    elapsedTimes[stamp] += (t2.tv_usec - startTimes[stamp].tv_usec) / 1000.0;   // us to ms

    elapsedus = (int)(elapsedTimes[stamp]*1000);
    return elapsedus;
}
