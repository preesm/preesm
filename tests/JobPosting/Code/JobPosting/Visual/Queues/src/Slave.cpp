#include "Slave.h"

/**
 Constructor

 @param inputId: Id of the slave
*/
Slave::Slave(int inputId){
	this->id = inputId;
	jobQueue = new JobQueue(0,inputId);
}

/**
 Destructor
*/
Slave::~Slave(){
	//delete jobQueue;
}

/**
 Launches the slave. Runs until ESC is pressed
*/
void Slave::launch(){
	job_descriptor job;

	while( GetAsyncKeyState(VK_ESCAPE) == 0){
		if(jobQueue->popJob(&job)){
			job.fct_pointer(&job);
		}
	}
}