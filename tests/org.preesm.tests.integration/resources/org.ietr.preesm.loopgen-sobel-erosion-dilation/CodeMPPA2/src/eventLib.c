#include "eventLib.h"
#include "papi.h"

#include <time.h>

pthread_mutex_t lock;

unsigned long long time_zero;

//Variables required to support dynamic reconfiguration

papify_PE_s papify_PEs_info[MAX_PES];
int papify_eventSet_actor[MAX_PES];
long long counterValuesTemp[MAX_CONFIGS];

int multiplexing = 0;
 
/* 
* This function configures the actor monitoring environment (initialization, file creation, event translation and eventSet creation) taking into account dynamic executions.
*/
void configure_papify_actor(papify_action_s* papify_action, char* componentName, char* actorName, int num_events, char* all_events_name, char* actor_ids, int eventSets){

	pthread_mutex_lock(&lock);
	event_init_papify_actions(papify_action, componentName, actorName, num_events, eventSets);
	event_init_output_file(papify_action, actorName, all_events_name);

	char* all_actor_ids_local = malloc(strlen(actor_ids)+1);
	int ret = snprintf(all_actor_ids_local, (strlen(actor_ids)+1) * sizeof(char), "%s", actor_ids);
	assert(ret < (int)((strlen(actor_ids)+1) * sizeof(char)) && "potential stack corruption\n");
	char* actor_id;
	int i = 0;
	int * actorIDs = malloc(eventSets * sizeof(int));

	if(strcmp(componentName, "") != 0){
		actor_id = strtok(all_actor_ids_local, ",");
		for(i = 0; i < eventSets; i++){
			actorIDs[i] = atoi(actor_id);	
			actor_id = strtok(NULL, ",");		
		}
		for(i = 0; i < eventSets; i++){
			event_init_event_code_set(papify_action, num_events, all_events_name);
			if(papify_eventSet_actor[actorIDs[i]] == -1){	
				event_create_eventSet(papify_action, actorIDs[i], i, 0);
			}
			else{				
				papify_action->papify_eventSets[i] = papify_eventSet_actor[actorIDs[i]];
			}
		}
		free(actor_id);
	}
	free(all_actor_ids_local);
	pthread_mutex_unlock(&lock);
}

/* 
* This function configures the PE monitoring environment (initialization, file creation, event translation and eventSet creation) taking into account dynamic executions.
*/
void configure_papify_PE(char* PEName, char* PEType, int PE_id){

	papify_PEs_info[PE_id].PE_id = malloc(strlen(PEName)+1);
	snprintf(papify_PEs_info[PE_id].PE_id, (strlen(PEName)+1) * sizeof(char), "%s", PEName);
	papify_PEs_info[PE_id].PE_type = malloc(strlen(PEType)+1);
	snprintf(papify_PEs_info[PE_id].PE_type, (strlen(PEType)+1) * sizeof(char), "%s", PEType);

}

void event_create_eventSet(papify_action_s* papify_action, int element_id, int eventSetPosition, int dynamic) {

	int retval, i, eventCodeSetMaxSize, maxNumberHwCounters;
	PAPI_event_info_t info;
	int papify_eventSet;
	PAPI_option_t opt;

	if(dynamic == 0){
		papify_eventSet = papify_action->papify_eventSets[eventSetPosition];
	}
	else{
		papify_eventSet = papify_PEs_info[element_id].papify_eventSet_ID_original[papify_action->papify_eventSets[eventSetPosition]];
	}

	maxNumberHwCounters = PAPI_get_opt( PAPI_MAX_HWCTRS, NULL );
	eventCodeSetMaxSize = PAPI_get_opt( PAPI_MAX_MPX_CTRS, NULL );

	if ( eventCodeSetMaxSize < papify_action->num_counters)
		test_fail( __FILE__, __LINE__, "eventCodeSetMaxSize < eventCodeSetSize, too many performance events defined! ", 1 );


	retval = PAPI_create_eventset( &papify_eventSet );
	if ( retval != PAPI_OK )
		test_fail( __FILE__, __LINE__, "PAPI_create_eventset", retval );

	retval = PAPI_assign_eventset_component( papify_eventSet, PAPI_get_component_index(papify_action->component_id[eventSetPosition]));
	if ( retval == PAPI_ENOCMP )
   		retval = PAPI_assign_eventset_component( papify_eventSet, 0 );

	if(maxNumberHwCounters < papify_action->num_counters && multiplexing == 1){
		opt.multiplex.eventset = papify_eventSet;
		opt.multiplex.ns = 100;
		opt.itimer.ns = 100;
		retval = PAPI_set_opt( PAPI_DEF_ITIMER_NS, &opt );
		if ( retval != PAPI_OK ) {
			test_fail( __FILE__, __LINE__, "Can't PAPI_DEF_ITIMER_NS", retval );
		}
		retval = PAPI_set_opt( PAPI_MULTIPLEX, &opt );
		if ( retval != PAPI_OK ) {
			test_fail( __FILE__, __LINE__, "Can't PAPI_MULTIPLEX", retval );
		}
		#ifdef VERBOSE
			printf("For %s monitoring %d events --> Multiplexing (Max HW counters = %d)\n", papify_action->actor_id, papify_action->num_counters, maxNumberHwCounters);
		#endif
	}
	else{
		#ifdef VERBOSE
		printf("For %s monitoring %d events --> No multiplexing\n", papify_action->actor_id, papify_action->num_counters);
		#endif
	}

	for (i = 0; i < papify_action->num_counters; i++) {
		retval = PAPI_get_event_info(papify_action->papify_eventCodeSet[i], &info);

		if ( retval != PAPI_OK )
			test_fail( __FILE__, __LINE__, "PAPI_get_event_info", retval );
		if (info.component_index == PAPI_get_component_index(papify_action->component_id[eventSetPosition])){
			retval = PAPI_add_event( papify_eventSet, info.event_code);
			if ( retval != PAPI_OK )
				test_fail( __FILE__, __LINE__, "PAPI_add_event", retval );
		}
	}	

	if(dynamic == 0){
		papify_eventSet_actor[element_id] = papify_eventSet;
		papify_action->papify_eventSets[eventSetPosition] = papify_eventSet;
	}
	else{
		papify_PEs_info[element_id].papify_eventSet_ID_original[papify_action->papify_eventSets[eventSetPosition]] = papify_eventSet;
	}
	
	
}
/* 
* Cleanup and destroy of every variable created to monitor with Papify (this function should be called when the application is going to finish)
*/
void event_destroy(void) {

	int retval;
	int i;
	int j;

	for(i = 0; i < MAX_PES; i++){
		if(papify_PEs_info[i].papify_eventSet_ID != -1){
			retval = PAPI_stop( papify_PEs_info[i].papify_eventSet_ID, counterValuesTemp );
		}
		if(papify_eventSet_actor[i] != -1){
			// cleanup the eventSet
			retval = PAPI_cleanup_eventset(papify_eventSet_actor[i]);
			if ( retval != PAPI_OK )
				test_fail( __FILE__, __LINE__, "PAPI_cleanup_eventSet", retval );

			// destroy the eventSet
			retval = PAPI_destroy_eventset(&papify_eventSet_actor[i]);
			if ( retval != PAPI_OK )
				test_fail( __FILE__, __LINE__, "PAPI_destroy_eventSet", retval );
		}
		for(j = 0; j < MAX_CONFIGS; j++){
			if(papify_PEs_info[i].papify_eventSet_ID_original[j] != -1){
				// cleanup the eventSet
				retval = PAPI_cleanup_eventset(papify_PEs_info[i].papify_eventSet_ID_original[j]);
				if ( retval != PAPI_OK )
					test_fail( __FILE__, __LINE__, "PAPI_cleanup_eventSet", retval );
	
				// destroy the eventSet
				retval = PAPI_destroy_eventset(&papify_PEs_info[i].papify_eventSet_ID_original[j]);
				if ( retval != PAPI_OK )
				test_fail( __FILE__, __LINE__, "PAPI_destroy_eventSet", retval );
			
			}
		}
	}

	// cleanup PAPI library
	PAPI_shutdown();

	#ifdef VERBOSE
		printf("event_destroy done \n");
	#endif
}
/*
* Initialize papify_eventSet_actor and papify_PEs_info
*/
static void setUpInternalVariables(){

	int i;
	int j;
	for(i = 0; i < MAX_PES; i++){
		papify_eventSet_actor[i] = -1;
		papify_PEs_info[i].papify_eventSet_ID = -1;
		for(j = 0; j < MAX_CONFIGS; j++){
			papify_PEs_info[i].papify_eventSet_ID_original[j] = -1;
		}
		papify_PEs_info[i].counterValuesStart = malloc(16 * sizeof(long long));
		papify_PEs_info[i].counterValuesStop = malloc(16 * sizeof(long long));
		papify_PEs_info[i].counterValues = malloc(16 * sizeof(long long));
	}	
	time_zero = PAPI_get_real_usec();

	if (pthread_mutex_init(&lock, NULL) != 0)
	{
		#ifdef VERBOSE
		printf("\n mutex init failed\n");
		#endif		
	}

}

/* 
* Initialize PAPI library and get the init time (this function should be called before any other monitoring function)
* It also stores the value of time_zero, which should be the starting point of the program
*/
void event_init(void) {

	int retval;

	// papify initialization
	setUpInternalVariables();
	
	// library initialization
	retval = PAPI_library_init( PAPI_VER_CURRENT );
	if ( retval != PAPI_VER_CURRENT )
		test_fail( __FILE__, __LINE__, "PAPI_library_init", retval );

	// place for initialization in case one makes use of threads
	#if !defined(__k1__)
	retval = PAPI_thread_init((unsigned long (*)(void))(pthread_self));
	if ( retval != PAPI_OK )
		test_fail( __FILE__, __LINE__, "PAPI_thread_init", retval );
	#endif //skip for MPPPA
	#ifdef VERBOSE
	printf("event_init done \n");
	#endif
}

/* 
* This function translates the name of the events to be monitored to the PAPI codes associated to each event
*/
void event_init_event_code_set(papify_action_s* papify_action, int code_set_size, char* all_events_name) {
	
	int i = 0;
	int event_code = 0;
	int retval;
	char* all_events_name_local = malloc(strlen(all_events_name)+1);
	snprintf(all_events_name_local, (strlen(all_events_name)+1) * sizeof(char), "%s", all_events_name);
	char* event_name;

	event_name = strtok(all_events_name_local, ",");

	for(i = 0; i < code_set_size; i++){
		
		retval = PAPI_event_name_to_code(event_name, &event_code);
		if ( retval != PAPI_OK ) {
			#ifdef VERBOSE
				printf("Translation of event %s not found\n", event_name);
			#endif
	  		test_fail( __FILE__, __LINE__, 
			  "Translation of event not found\n", retval );
		}

		event_name = strtok(NULL, ",");
		papify_action->papify_eventCodeSet[i] = event_code;
	}
	free(all_events_name_local);
	free(event_name);
}

/* 
* Initialize PAPI library and multiplex functionalities
*/
void event_init_multiplex(void) {

	int retval;

	// papify initialization
	setUpInternalVariables();

	// library initialization
	retval = PAPI_library_init( PAPI_VER_CURRENT );
	if ( retval != PAPI_VER_CURRENT )
		test_fail( __FILE__, __LINE__, "PAPI_library_init", retval );

	// place for initialization in case one makes use of threads
	#if !defined(__k1__)
	retval = PAPI_thread_init((unsigned long (*)(void))(pthread_self));
	if ( retval != PAPI_OK )
		test_fail( __FILE__, __LINE__, "PAPI_thread_init", retval );
	#endif // Skip for MPPA

	// multiplex initialization
	init_multiplex(  );

	#ifdef VERBOSE
		printf("event_init done \n");
	#endif

	multiplexing = 1;
}

/* 
* Create the .csv file associated to each function and prints the header of the file
*/
void event_init_output_file(papify_action_s* papify_action, char* actorName, char* all_events_name) {
	
	int i, j = 0;
	int event_code = 0;
	char* all_events_name_local = malloc(strlen(all_events_name)+1);
	char* event_name;
	PAPI_event_info_t info;
	char* file_name = malloc(256 * sizeof(char));
	char* output_string = malloc(256 * sizeof(char));
	int monitoring = 0;
	int retval;

	int ret;
	for(i = 0; i < papify_action->num_eventSets; i++){
		ret = snprintf(all_events_name_local, (strlen(all_events_name)+1) * sizeof(char), "%s", all_events_name);
		assert(ret < (int)((strlen(all_events_name)+1) * sizeof(char)) && "potential stack corruption\n");
		event_name = strtok(all_events_name_local, ",");

		ret = snprintf(file_name, 256 * sizeof(char), "%s%s%s%s%s", "papify-output/papify_output_", actorName, "_", papify_action->component_id[i], ".csv");
		assert(ret < (int)(256 * sizeof(char)) && "potential stack corruption\n");
		ret = snprintf(output_string, 256 * sizeof(char), "%s", "PE,Actor,tini,tend");
		assert(ret < (int)(256 * sizeof(char)) && "potential stack corruption\n");
		monitoring = 0;
		papify_action->num_counters_eventSet[i] = 0;
					
		for(j = 0; j < papify_action->num_counters; j++){
		
			retval = PAPI_event_name_to_code(event_name, &event_code);
			if ( retval != PAPI_OK ) {
		  		test_fail( __FILE__, __LINE__, 
				  "Translation of event not found\n", retval );
			}
			retval = PAPI_get_event_info(event_code, &info);
			if ( retval != PAPI_OK )
				test_fail( __FILE__, __LINE__, "PAPI_get_event_info", retval );

			if (info.component_index == PAPI_get_component_index(papify_action->component_id[i])){			
				strcat(output_string, ",");			
				strcat(output_string, event_name);
				monitoring = 1;
				papify_action->num_counters_eventSet[i] = papify_action->num_counters_eventSet[i] + 1;
			}
			event_name = strtok(NULL, ",");
		}	
		if(monitoring == 1){	
			strcat(output_string, "\n");
			papify_action->papify_output_file = fopen(file_name,"w");
			fprintf(papify_action->papify_output_file, "%s", output_string);
			fclose(papify_action->papify_output_file);
		}
	}
	if(papify_action->num_eventSets == 0){		
		ret = snprintf(file_name, 256 * sizeof(char), "%s%s%s", "papify-output/papify_output_", actorName, ".csv");
		assert(ret < (int)(256 * sizeof(char)) && "potential stack corruption\n");
		ret = snprintf(output_string, 256 * sizeof(char), "%s", "PE,Actor,tini,tend\n");
		assert(ret < (int)(256 * sizeof(char)) && "potential stack corruption\n");
		papify_action->papify_output_file = fopen(file_name,"w");
		fprintf(papify_action->papify_output_file, "%s", output_string);
		fclose(papify_action->papify_output_file);
	}
	free(all_events_name_local);
	free(file_name);
	free(output_string);
}

/* 
* This function initializes all the variables of the papify_action_s
*/
void event_init_papify_actions(papify_action_s* papify_action, char* componentName, char* actorName, int num_events, int eventSets) {

	int i = 0;

	papify_action->actor_id = malloc(strlen(actorName)+1);
	int ret = snprintf(papify_action->actor_id, (strlen(actorName)+1) * sizeof(char), "%s", actorName);
	assert(ret < (int)((strlen(actorName)+1) * sizeof(char)) && "potential stack corruption\n");

	char* all_components_local = malloc(strlen(componentName)+1);
	char* component;
	ret = snprintf(all_components_local, (strlen(componentName)+1) * sizeof(char), "%s", componentName);
	assert(ret < (int)((strlen(componentName)+1)) && "potential stack corruption\n");
	component = strtok(all_components_local, ",");

	papify_action->component_id = (char**)malloc(eventSets * sizeof(char*));
	papify_action->papify_eventSets = malloc(sizeof(int) * eventSets);
	for(i = 0; i < eventSets; i++){
		papify_action->component_id[i] = (char*)malloc((strlen(component)+1) * sizeof(char));
		ret = snprintf(papify_action->component_id[i], (strlen(component)+1) * sizeof(char), "%s", component);
		assert(ret < (int)((strlen(component)+1) * sizeof(char)) && "potential stack corruption\n");
		component = strtok(NULL, ",");
		papify_action->papify_eventSets[i] = PAPI_NULL;
	}

	papify_action->PE_id = malloc(128 * sizeof(char));

	papify_action->num_counters = num_events;
	papify_action->num_eventSets = eventSets;

	papify_action->num_counters_eventSet = malloc(sizeof(int) * num_events);

	papify_action->papify_eventCodeSet = malloc(sizeof(int) * num_events);
	papify_action->papify_output_file = malloc(sizeof(FILE) * 1);

	free(all_components_local);
	free(component);
}

/* 
* Launch the monitoring of the eventSet. This eventSet will be counting from the beginning and the actual values will
* be computed by differences with event_start and event_stop functions
*/
void event_launch(papify_action_s* papify_action, int PE_id){
	int retval;
	(void) papify_action;
	retval = PAPI_start( papify_PEs_info[PE_id].papify_eventSet_ID );
	if ( retval != PAPI_OK ){
		test_fail( __FILE__, __LINE__, "PAPI_start",retval );
	}

	retval = PAPI_read( papify_PEs_info[PE_id].papify_eventSet_ID, papify_PEs_info[PE_id].counterValuesStart );
	if ( retval != PAPI_OK )
	test_fail( __FILE__, __LINE__, "PAPI_read",retval );
}

#if !defined(__k1__)
/* 
* Read all the .csv files and generates a new one with a profiling associated to each actor
*/
void event_profiling() {
	
	DIR* FD;
	struct dirent* in_file;
	FILE *common_file;
	FILE *entry_file;
	char buffer[BUFSIZ];
	char input_file_name[300];
	unsigned long long averageValues[50];
	char* cellValue;
	char* actorName;
	int totalCells = 0;
	int i;
	int counterCells = 0;
	int totalExecutions = 0;
	unsigned long long initTime;
	unsigned long long endTime;
	unsigned long long executionTime;
	int maxNumberHwCounters;

	maxNumberHwCounters = PAPI_get_opt( PAPI_MAX_HWCTRS, NULL );
    	/* Opening common file for writing */
    	common_file = fopen("papify-output/papify_output_profiling.csv", "w");
    	if (common_file == NULL){
        	fprintf(stderr, "Error : Failed to open common_file - %s\n", strerror(errno));
        	return;
    	}
	fprintf(common_file, "Application profiling -- Actor,NB of events being monitored,NB of executions,Average values\n");
    	/* Scanning the in directory */
    	if (NULL == (FD = opendir ("./papify-output"))){
        	fprintf(stderr, "Error : Failed to open input directory - %s\n", strerror(errno));
        	fclose(common_file);
        	return;
    	}
    	while ((in_file = readdir(FD)) != NULL){
		/* Avoid opening wrong files
	 	*/
		if (!strcmp (in_file->d_name, "."))
	    		continue;
		if (!strcmp (in_file->d_name, ".."))    
	    		continue;
		if (!strcmp (in_file->d_name, "papify_output_profiling.csv"))    
	    		continue;

		/* Open directory entry file for common operation */
		snprintf(input_file_name, sizeof input_file_name, "%s%s", "papify-output/", in_file->d_name);
		entry_file = fopen(input_file_name, "r");
		if (entry_file == NULL){
	    		fprintf(stderr, "Error : Failed to open entry file - %s\n", strerror(errno));
	    		fclose(common_file);
	    		return;
		}
		/* Doing some struf with entry_file : */
		/* For example use fgets */
		if (fgets(buffer, BUFSIZ, entry_file) != NULL){
            		cellValue = strtok(buffer, ","); // PE
            		cellValue = strtok(NULL, ","); // Actor
			fprintf(common_file, "\n%s,totalEvents,totalExecutions", cellValue);
			totalCells = 0;
            		cellValue = strtok(NULL, ","); // tini
            		cellValue = strtok(NULL, ","); // tfin
            		cellValue = strtok(NULL, ","); // First event
			while(cellValue != NULL){
				averageValues[totalCells] = 0;
				totalCells++;
				fprintf(common_file, ",%s", cellValue);
            			cellValue = strtok(NULL, ",");				
			}
		}
		totalExecutions = 0;
		executionTime = 0;
		counterCells = 0;
		while (fgets(buffer, BUFSIZ, entry_file) != NULL){
			totalExecutions++;
			counterCells = 0;
			buffer[strlen(buffer)-1] = 0;
			cellValue = strtok(buffer, ","); // PE
            		actorName = strtok(NULL, ","); // Actor
            		cellValue = strtok(NULL, ","); // tini
            		initTime = strtoull(cellValue, NULL, 10);
            		cellValue = strtok(NULL, ","); // tfin
            		endTime = strtoull(cellValue, NULL, 10);
			executionTime = executionTime + (endTime - initTime);
            		cellValue = strtok(NULL, ",");
			while(cellValue != NULL){
				averageValues[counterCells] = averageValues[counterCells] + strtoull(cellValue, NULL, 10);
				counterCells++;
            			cellValue = strtok(NULL, ",");
			}
		}
		if(maxNumberHwCounters<totalCells){
			#ifdef VERBOSE
				printf("WARNING: %s monitoring needed multiplexing --> Some values could have been corrupted by outliers\n", actorName);
				printf("TIP: please, consider monitoring up to %d events at the same time to avoid multiplexing\n", maxNumberHwCounters);
			#endif
		}
		fprintf(common_file, "%s", actorName);
		fprintf(common_file, ",%d", totalCells);
		fprintf(common_file, ",%d", totalExecutions);
		fprintf(common_file, ",%llu", executionTime/totalExecutions);
		for(i = 0; i < totalCells; i++){
			fprintf(common_file, ",%llu", averageValues[i]/totalExecutions);			
		}
		/* When you finish with the file, close it */
		fprintf(common_file, "\n");
		fclose(entry_file);
    	}
    	/* Don't forget to close common file before leaving */
    	fclose(common_file);

    	return;
}
#endif // Avoid this for the MPPA


/* 
* Read the current values of the event counters and stores them as the starting point
*/
void event_start(papify_action_s* papify_action, int PE_id){
	int retval;
	// Check corresponding PE type and eventSet
	int i;
	int eventSet_position = -1;
	for(i = 0; i < papify_action->num_eventSets; i++){
		if(strcmp(papify_PEs_info[PE_id].PE_type, papify_action->component_id[i]) == 0){
			eventSet_position = i;
		}
	}
	if(eventSet_position != -1){
		if(papify_PEs_info[PE_id].papify_eventSet_ID_original[papify_action->papify_eventSets[eventSet_position]] == papify_PEs_info[PE_id].papify_eventSet_ID && papify_PEs_info[PE_id].papify_eventSet_ID != -1){
			if(papify_action->num_counters != 0){
				retval = PAPI_read( papify_PEs_info[PE_id].papify_eventSet_ID, papify_PEs_info[PE_id].counterValuesStart );
			if ( retval != PAPI_OK )
				test_fail( __FILE__, __LINE__, "PAPI_read",retval );
			}
		}
		else if(papify_PEs_info[PE_id].papify_eventSet_ID_original[papify_action->papify_eventSets[eventSet_position]] == -1){
			if(papify_PEs_info[PE_id].papify_eventSet_ID != -1){
				retval = PAPI_stop( papify_PEs_info[PE_id].papify_eventSet_ID, counterValuesTemp );
				if ( retval != PAPI_OK )
					test_fail( __FILE__, __LINE__, "PAPI_stop",retval );
			}		
			pthread_mutex_lock(&lock);
			event_create_eventSet(papify_action, PE_id, eventSet_position, 1);
			pthread_mutex_unlock(&lock);
			papify_PEs_info[PE_id].papify_eventSet_ID = papify_PEs_info[PE_id].papify_eventSet_ID_original[papify_action->papify_eventSets[eventSet_position]];
			event_launch(papify_action, PE_id);
		}
		else{
			if(papify_PEs_info[PE_id].papify_eventSet_ID != -1){
				retval = PAPI_stop( papify_PEs_info[PE_id].papify_eventSet_ID, counterValuesTemp );
				if ( retval != PAPI_OK )
					test_fail( __FILE__, __LINE__, "PAPI_stop",retval );
			}
			papify_PEs_info[PE_id].papify_eventSet_ID = papify_PEs_info[PE_id].papify_eventSet_ID_original[papify_action->papify_eventSets[eventSet_position]];
			event_launch(papify_action, PE_id);	
		}
	}
}

/* 
* This function stores the starting point of the timing monitoring using PAPI_get_real_usec() function
*/
void event_start_papify_timing(papify_action_s* papify_action, int PE_id){

	papify_PEs_info[PE_id].time_init_action = PAPI_get_real_usec() - time_zero;
	(void) papify_action;
}

/* 
* Read the current values of the event counters and stores them as the ending point.
* After that, the total value is computed by differences and stored as the total value
*/
void event_stop(papify_action_s* papify_action, int PE_id) {

	int retval, i;
	// Check corresponding PE type and eventSet
	int eventSet_position = -1;
	for(i = 0; i < papify_action->num_eventSets; i++){
		if(strcmp(papify_PEs_info[PE_id].PE_type, papify_action->component_id[i]) == 0){
			eventSet_position = i;
		}
	}
	if(eventSet_position != -1){
		if(papify_action->num_counters != 0){
			retval = PAPI_read( papify_PEs_info[PE_id].papify_eventSet_ID, papify_PEs_info[PE_id].counterValuesStop );
			for(i = 0; i < papify_action->num_counters; i++){
				if(papify_PEs_info[PE_id].counterValuesStop[i] < papify_PEs_info[PE_id].counterValuesStart[i]){
					#if defined(__k1__)
					papify_PEs_info[PE_id].counterValues[i] = UINT_MAX - papify_PEs_info[PE_id].counterValuesStart[i] + papify_PEs_info[PE_id].counterValuesStop[i];
					#else
					papify_PEs_info[PE_id].counterValues[i] = LONG_MAX - papify_PEs_info[PE_id].counterValuesStart[i] + papify_PEs_info[PE_id].counterValuesStop[i];
					#endif
					
				} else{
					papify_PEs_info[PE_id].counterValues[i] = papify_PEs_info[PE_id].counterValuesStop[i] - papify_PEs_info[PE_id].counterValuesStart[i];
				}				
			}
			if ( retval != PAPI_OK )
				test_fail( __FILE__, __LINE__, "PAPI_read",retval );
		}
	}
}

/* 
* This function stores the ending point of the timing monitoring using PAPI_get_real_usec() function
*/
void event_stop_papify_timing(papify_action_s* papify_action, int PE_id){
	
	papify_PEs_info[PE_id].time_end_action = PAPI_get_real_usec() - time_zero;
	(void) papify_action;
}

/* 
* This function writes all the monitoring information as a new line of the .csv file
*/
void event_write_file(papify_action_s* papify_action, int PE_id){

	char* output_file_name = malloc(128 * sizeof(char));
	char* linePrinted = malloc(128 * sizeof(char));
	char* eventValue = malloc(32 * sizeof(char));
	int i;
	FILE *printingFile;

	// Check corresponding PE type and eventSet
	int eventSet_position = -1;
	for(i = 0; i < papify_action->num_eventSets; i++){	
		if(strcmp(papify_PEs_info[PE_id].PE_type, papify_action->component_id[i]) == 0){
			eventSet_position = i;
		}
	}
	if(eventSet_position != -1){
		int ret = snprintf(output_file_name, 128 * sizeof(char), "%s%s%s%s%s", "papify-output/papify_output_", papify_action->actor_id, "_", papify_PEs_info[PE_id].PE_type, ".csv");
		assert(ret < (int)(128 * sizeof(char)) && "potential stack corruption\n");
		ret = snprintf(linePrinted, 128 * sizeof(char), "%s,%s,%lld,%lld", papify_PEs_info[PE_id].PE_id, papify_action->actor_id, papify_PEs_info[PE_id].time_init_action, papify_PEs_info[PE_id].time_end_action);
		assert(ret < (int)(128 * sizeof(char)) && "potential stack corruption\n");
		for(i = 0; i < papify_action->num_counters_eventSet[eventSet_position]; i++){	
			ret = snprintf(eventValue, 32 * sizeof(char), ",%llu", papify_PEs_info[PE_id].counterValues[i]);
			assert(ret < (int)(32 * sizeof(char)) && "potential stack corruption\n");
			strcat(linePrinted, eventValue);			
		}
		strcat(linePrinted, "\n");		
		printingFile = fopen(output_file_name,"a+");
		fprintf(printingFile, linePrinted);
		fclose(printingFile);
	} else if (papify_action->num_eventSets == 0) {
		int ret = snprintf(output_file_name, 128 * sizeof(char), "%s%s%s", "papify-output/papify_output_", papify_action->actor_id, ".csv");
		assert(ret < (int)(128 * sizeof(char)) && "potential stack corruption\n");
		printingFile = fopen(output_file_name,"a+");
		fprintf(printingFile, "%s,%s,%lld,%lld\n", papify_PEs_info[PE_id].PE_id, papify_action->actor_id, papify_PEs_info[PE_id].time_init_action, papify_PEs_info[PE_id].time_end_action);
		fclose(printingFile);
	}
	free(output_file_name);
	free(linePrinted);
	free(eventValue);
}

/* 
* Initialize multiplex functionalities
*/

void init_multiplex( void ) {

	int retval;
	const PAPI_hw_info_t *hw_info;
	const PAPI_component_info_t *cmpinfo;

	/* Initialize the library */

	/* for now, assume multiplexing on CPU compnent only */
	cmpinfo = PAPI_get_component_info(0);
	if (cmpinfo == NULL) {
		test_fail(__FILE__, __LINE__, "PAPI_get_component_info", 2);
	}

	hw_info = PAPI_get_hardware_info();
	if (hw_info == NULL) {
		test_fail(__FILE__, __LINE__, "PAPI_get_hardware_info", 2);
	}

	if ((strstr(cmpinfo->name, "perfctr.c")) && (hw_info != NULL ) && strcmp(hw_info->model_string, "POWER6") == 0) {
		retval = PAPI_set_domain(PAPI_DOM_ALL);
		if (retval != PAPI_OK) {
			test_fail(__FILE__, __LINE__, "PAPI_set_domain", retval);
		}
	}
	retval = PAPI_multiplex_init();
	if (retval != PAPI_OK) {
		test_fail(__FILE__, __LINE__, "PAPI multiplex init fail\n", retval);
	}
}

/*  
* Print the structure to check whether it is configured correctly
*/
void structure_test(papify_action_s *someAction, int eventCodeSetSize, int *eventCodeSet){
	int i;
	printf("Action name: %s\n", someAction->actor_id);
	printf("Event Code Set:\n");
	for(i=0; i<eventCodeSetSize; i++){
		printf("\t-%d\n", eventCodeSet[i]);
	}
}
 
/* 
* Test function to check where the monitoring is failing
*/
void test_fail( char *file, int line, char *call, int retval ) {

	int line_pad;
	char buf[128];

	line_pad = (int) (50 - strlen(file));
	if (line_pad < 0) {
		line_pad = 0;
	}

	memset(buf, '\0', sizeof(buf));

	fprintf(stdout, "%-*s FAILED\nLine # %d\n", line_pad, file, line);

	if (retval == PAPI_ESYS) {
		sprintf(buf, "System error in %s", call);
		perror(buf);
	} else if (retval > 0) {
		fprintf(stdout, "Error: %s\n", call);
	} else if (retval == 0) {
	#if defined(sgi)
		fprintf( stdout, "SGI requires root permissions for this test\n" );
	#else
		fprintf(stdout, "Error: %s\n", call);
	#endif
	} else {
		fprintf(stdout, "Error in %s: %s\n", call, PAPI_strerror(retval));
	}

	fprintf(stdout, "\n");

	exit(1);
}


