#include "Slave.h"

Slave::Slave(){
	string type = "slave";
	jobQueue = new JobQueue(type);
}

void Slave::launch(){
}

void Slave::setId(int inputId){

	this->id = inputId;
	printf("Started slave number %d\n",this->id);

}

Slave::~Slave(){
	//delete jobQueue;
}