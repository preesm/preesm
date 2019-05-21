/* Standard headers for Papify test applications.
    This file is customized to hide Windows / Unix differences.
*/

#ifndef EVENT_LIB
#define EVENT_LIB
#endif

#include <stdlib.h>
#include <stdio.h>

#include <unistd.h>
#include <sys/wait.h>
#if (!defined(NO_DLFCN) && !defined(_BGL) && !defined(_BGP) && !defined(__k1__))
#include <dlfcn.h>
#endif

#include <errno.h>
#if !defined(__FreeBSD__) && !defined(__APPLE__)
#include <malloc.h>
#endif
#include <assert.h>
#include <string.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/time.h>
#include <pthread.h>
#if !defined(__k1__)
#include <dirent.h>
#endif
#include "papiStdEventDefs.h"

/*
* Papify_action_s structure stores the information related to the monitoring of the function being monitored
*
*/
#if defined(__k1__)
#define MAX_PES 10
#define MAX_CONFIGS 5
#else
#define MAX_PES 128
#define MAX_CONFIGS 32
#endif

typedef struct papify_action_s {
	char *actor_id;				// Name of the function being monitored
	char **component_id;			// PAPI components associated to the eventSet of the function
	char *PE_id;				// ID associated to the eventSet to be monitored. This ID needs to be different for functions executed in parallel, as the eventSets are associated to specific threads
	int num_counters;			// Number of events being monitored
	FILE* papify_output_file;		// File where the monitoring data will be stored
	int* papify_eventCodeSet;		// Code of the events that are being monitored
	int* papify_eventSets;			// EventSets associated to the monitoring of the function
	int num_eventSets;			// Number of eventSets associated to the monitoring of the function
	int* num_counters_eventSet;		// Number of events being monitored for each eventSet
	
} papify_action_s;

/*
* PE information required for monitoring dinamically
*
*/ 

typedef struct papify_PE_s {
	char *PE_id;				// ID associated to the eventSet to be monitored.
	char *PE_type;				// Type of component associated to the PE.
	long long *counterValuesStart; 		// Backup for the Starting point
	long long *counterValuesStop; 		// Backup for End point (required to measure events by differences)
	long long *counterValues; 		// Backup for the Total number of events associated to the function execution
	unsigned long long time_init_action;	// Starting time of the function
	unsigned long long time_end_action;	// Ending time of the function
	int papify_eventSet_ID;			// ID of the eventSet running in the current thread
	int papify_eventSet_ID_original[MAX_CONFIGS];	// ID of the original eventSet associated to the monitoring of the function
	
} papify_PE_s;

void configure_papify_actor(papify_action_s* papify_action, char* componentName, char* actorName, int num_events, char* all_events_name, char* actor_ids, int eventSets);
void configure_papify_PE(char* PEName, char* PEType, int PE_id);
void event_create_eventSet(papify_action_s* papify_action, int element_id, int eventSetPosition, int dynamic);
void event_destroy();
void event_init();
void event_init_event_code_set(papify_action_s* papify_action, int code_set_size, char* all_events_name);
void event_init_multiplex();
void event_init_output_file(papify_action_s* papify_action, char* actorName, char* all_events_name);
void event_init_papify_actions(papify_action_s* papify_action, char* componentName, char* actorName, int num_events, int eventSets);
void event_launch(papify_action_s* papify_action, int PE_id);
#if !defined(__k1__)
void event_profiling();
#endif
void event_start(papify_action_s* papify_action, int PE_id);
void event_start_papify_timing(papify_action_s* papify_action, int PE_id);
void event_stop(papify_action_s* papify_action, int PE_id);
void event_stop_papify_timing(papify_action_s* papify_action, int PE_id);
void event_write_file(papify_action_s* papify_action, int PE_id);
void eventSet_set_multiplex(papify_action_s* papify_action);
void init_multiplex();
void structure_test(papify_action_s *someAction, int eventCodeSetSize, int *eventCodeSet);
void test_fail(char *file, int line, char *call, int retval);

