/*
	============================================================================
	Name        : clock.h
	Author      : mpelcat
	Version     : 1.0
	Copyright   : CECILL-C
	Description : Timing primitive for Preesm Codegen.
	============================================================================
*/

#ifndef CLOCK_H
#define CLOCK_H

// stamps used in clock functions to store data
#define MAX_STAMPS 50
#define CLOCK_STAMP_GENERAL 0

// Starting to record time for a given stamp
void startTiming(int stamp);

// Stoping to record time for a given stamp. Returns the time in us
unsigned int stopTiming(int stamp);

#endif
