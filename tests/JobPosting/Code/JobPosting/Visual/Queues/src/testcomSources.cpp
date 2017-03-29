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
#include "testcomSources.h"
#include <stdio.h>

#define TESTVALUE 10

void sensor(job_descriptor* job){

	char* o1 = (char*)job->buffers[0];
	char* o2 = (char*)job->buffers[1];
	char* o3 = (char*)job->buffers[2];
	int i = 0;

	for(i=0;i<1000;i++){
		o3[i] = o2[i] = o1[i] = i%128;
	}
}

void sensor2(job_descriptor* job){

	char* o1 = (char*)job->buffers[0];

	int i = 0;

	for(i=0;i<1000;i++){
		o1[i] = i%128;
	}
}

void parallel(job_descriptor* job){

	char* i1 = (char*)job->buffers[0];
	char* o1 = (char*)job->buffers[1];

	int i = 0;

	for(i=0;i<1000;i++){
		o1[i] = i1[i];
	}
}

void gen_int(job_descriptor* job){

	char* i1 = (char*)job->buffers[0];
	char* i2 = (char*)job->buffers[1];
	char* o1 = (char*)job->buffers[2];
	char* o2 = (char*)job->buffers[3];

	int i = 0;

	for(i=0;i<1000;i++){
		o1[i] = i1[i];
		o2[i] = i2[i];
	}
}

void copy(job_descriptor* job){
	
	char* i1 = (char*)job->buffers[0];
	char* o1 = (char*)job->buffers[1];
	int size = job->params[0];

	int i = 0;

	for(i=0;i<1000;i++){
		o1[i] = i1[i];
	}
}

void actuator(job_descriptor* job){

	char* i1 = (char*)job->buffers[0];
	char* i2 = (char*)job->buffers[1];
	char* i3 = (char*)job->buffers[2];
	int size = job->params[0];

	int i = 0;
	int bSuccess = 1;

	for(i=0;i<size;i++){
		if(i1[i] != i%128){
			bSuccess = 0;
			break;
		}
		if(i2[i] != i%128){
			bSuccess = 0;
			break;
		}
		if(i3[i] != i%128){
			bSuccess = 0;
			break;
		}
	}

	if(bSuccess){
		printf("success\n");
	}
	else{
		printf("failure\n");
	}
}


