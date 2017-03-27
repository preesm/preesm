#include "windows.h"
#include "timedSimulation.h"

/**
 This function executes an active wait of a time given in job->time
*/
void simulation(job_descriptor* job){
	
	int time = (job->time)/100;
	printf("id=%d\n",job->id);
	
	DWORD currentTime = GetTickCount() + time;
	int i=1;
	while(GetTickCount() < currentTime && i > 0){
		i++;
		i--;
	}
}