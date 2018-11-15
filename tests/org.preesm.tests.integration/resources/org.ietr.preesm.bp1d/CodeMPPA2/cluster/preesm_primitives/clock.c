/*
	============================================================================
	Name        : clock.c
	Author      : mpelcat
	Version     : 1.0
	Copyright   : CECILL-C
	Description : Timing primitive for Preesm Codegen.
	============================================================================
*/

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <HAL/hal/hal_ext.h>
#include <HAL/hal/cluster/dsu.h>

#include "clock.h"

uint64_t startTimes[MAX_STAMPS]; 	/* cycle */
uint64_t elapsedTimes[MAX_STAMPS];	/* cycle */

// Starting to record time for a given stamp
void startTiming(int stamp){
    startTimes[stamp] = __k1_read_dsu_timestamp();
}

// Stoping to record time for a given stamp. Returns the time in us
unsigned int stopTiming(int stamp){
	uint64_t t2 = __k1_read_dsu_timestamp();
    elapsedTimes[stamp] = t2 - startTimes[stamp];
    return (unsigned int)(elapsedTimes[stamp]/(__bsp_frequency/1000000));
}
