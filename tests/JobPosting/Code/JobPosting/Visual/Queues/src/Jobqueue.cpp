/*******************************************************************************
 * Copyright or © or Copr. 2009 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2009 - 2010)
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
#include "jobQueue.h"

#define MAX_JOB_NUMBER 20

/**
 Constructor

 @param create: 1 if we need to create the shared mem. 0 if we connect to it.
 @param id: the id of the queue
*/
JobQueue::JobQueue(int create, int id)
{
	memory = new ProtectedSharedMemory(create,MAX_JOB_NUMBER*sizeof(job_descriptor) + sizeof(queue_properties),id);

	if(create){
		queue_properties props = {0,0,0};
		writeProperties(props);
	}
}

/**
 Destructor
*/
JobQueue::~JobQueue()
{
	delete memory;
}

/**
 Pushes a job in the queue

 @param job: the input job being pushed
 @return: 1 if it worked; 0 otherwise
*/
int JobQueue::pushJob(job_descriptor* job)
{
	queue_properties props = readProperties();

	if(props.jobCount < MAX_JOB_NUMBER){
		int charIndex = props.headIndex*sizeof(job_descriptor) + sizeof(queue_properties);
		memory->write((void*)job,charIndex,sizeof(job_descriptor));
		props.headIndex = props.headIndex + 1;
		props.jobCount = props.jobCount + 1;
		writeProperties(props);
		return 1;
	}

	return 0;
}

/**
 Pops a job from the queue

 @param job: the output job being popped
 @return: 1 if it worked; 0 otherwise
*/
int JobQueue::popJob(job_descriptor* job)
{
	queue_properties props = readProperties();

	if(props.jobCount > 0){
		int charIndex = props.tailIndex*sizeof(job_descriptor) + sizeof(queue_properties);
		memory->read((void*)job,charIndex,sizeof(job_descriptor));
		props.tailIndex = props.tailIndex + 1;
		props.jobCount = props.jobCount - 1;
		writeProperties(props);
		return 1;
	}

	return 0;
}

/**
 Getting the number of jobs in the queue

 @return: the number of jobs in the queue
*/
int JobQueue::getJobCount()
{
	queue_properties props = readProperties();
	return props.jobCount;
}

/**
 Reads the properties of the queue from the shared memory

 @param props: properties
*/
queue_properties JobQueue::readProperties()
{
	queue_properties props;
	memory->read((void*)&props,0,sizeof(queue_properties));
	return props;
}

/**
 Writes the properties of the queue in the shared memory

 @param props: properties
*/
void JobQueue::writeProperties(queue_properties props)
{
	memory->write((void*)&props,0,sizeof(queue_properties));
}
