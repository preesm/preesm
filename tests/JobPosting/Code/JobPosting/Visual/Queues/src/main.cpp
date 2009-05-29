// main.cpp: calls the Scheduler to dispatch the jobs corresponding to the application
//
#include "jobHeader.h"

#include "Master.h"
#include "Slave.h"


// Code prototypes
#include "timedSimulation.h"
#include "testComSources.h"

// Buffer declarations
#include "jobBuffers.h"
// table with the job descriptors
#include "jobList.h"


int main(int argc, char* argv[])
{
	Master* master = NULL;
	Slave* slave = NULL;

	for(int i = 0;i<argc;i++){
		string option (argv[i]);
		// The master distributes the jobs to the slaves 
		if(option.compare("-master") == 0){
			printf("Started master\n");
			master = new Master(jobs);
		}
		// The slave executes the jobs 
		else if(option.compare("-slave") == 0){
			slave = new Slave();
		}
		// retrieving the number of the executable
		else if(option.compare("-id") == 0){
			int processId = atoi(argv[i+1]);

			if(slave != NULL){
				slave->setId(processId);
			}
		}
	}

	if(slave != NULL){
		slave->launch();
		delete slave;
	}
	else if(master != NULL){
		master->launch();
		delete master;
	}
}

#if 0
	int nbSlaves = 2;
	cout << "starting jobs";

	if(CreateThread(NULL,8000,masterThread,NULL,0,NULL) == NULL){
		cout << "error creating master\n";
	}

	for(int i = 0;i<nbSlaves;i++)
	{
		if(CreateThread(NULL,8000,slaveThread,NULL,0,NULL) == NULL){
			cout << "error creating slave " << i << "\n";
		}
	}
#endif

#if 0
DWORD WINAPI masterThread( LPVOID lpParam ){

	cout << "starting master";

(	Master* master = new Master(jobs);
	master->launch();

	return 0;
}

DWORD WINAPI slaveThread( LPVOID lpParam ){

	cout << "starting slave";

	Slave* slave = new Slave();
	slave->launch();

	return 0;
}
#endif

