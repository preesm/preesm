/*******************************************************************************
 * Copyright or © or Copr. %%LOWERDATE%% - %%UPPERDATE%% IETR/INSA:
 *
 * %%AUTHORS%%
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
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
	int isMaster = 0;
	int slaveId = 0;
	int nbSlaves = 1;

	for(int i = 0;i<argc;i++){
		string option (argv[i]);
		// The master distributes the jobs to the slaves 
		if(option.compare("-master") == 0){
			isMaster = 1;
		}
		// The slave executes the jobs 
		else if(option.compare("-slave") == 0){
			isMaster = 0;
		}
		// retrieving the number of the executable
		else if(option.compare("-id") == 0){
			slaveId = atoi(argv[i+1]);
		}
		// retrieving the number of the executable
		else if(option.compare("-nbSlaves") == 0){
			nbSlaves = atoi(argv[i+1]);
		}
	}

	if(isMaster){
		printf("Started master\n");
		master = new Master(nbSlaves, jobs);
	}
	else{
		slave = new Slave(slaveId);
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

