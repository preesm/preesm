/*******************************************************************************
 * Copyright or © or Copr. 2010 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Thanh Hai Dao <thahn-hai.dao@insa-rennes.fr> (2010)
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


#define TESTVALUE 1000

#define SIZE_LOCAL 1000

char 	nbrand;

void sensor_init(char* o1, char* o2, char* o3){

}

void sensor2_init(char* o1){
}

void parallel_init(char* i1, char* o1){

}

void gen_int_init(char* i1,char* o1,char* o2){

}

void copy_init(char* i1, char* o1){

}

void actuator_init(char* i1,char* i2,char* i3){

}

void circular_init(char* i1,char* o1){

}
void circular6_init(char* i1,char* o1){

}

void sensor(char* o1, char* o2, char* o3){
	//printf("sensor");
	int i = 0;

	nbrand=20;//rand();

	for(i=0;i<TESTVALUE;i++){
		o3[i] = o2[i] = o1[i] = nbrand;
	}
}

void sensor2(char* o1){
	//printf("sensor");
	int i = 0;

	for(i=0;i<SIZE_LOCAL;i++){
		o1[i] = nbrand;
	}
}

void parallel(char* i1, char* o1){
	//printf("parallel");
	int i = 0;

	for(i=0;i<SIZE_LOCAL;i++){
		o1[i] = i1[i];
	}
}

void gen_int(char* i1,char* i2,char* o1,char* o2){
	//printf("gen_int");
	int i = 0;

	for(i=0;i<SIZE_LOCAL;i++){
		o1[i] = i1[i];
		o2[i] = i2[i];
	}
}

void copy(char* i1, char* o1, int size){
	//printf("copy");
	int i = 0;

	for(i=0;i<TESTVALUE;i++){
		o1[i] = i1[i];
	}
}

void circular(char* i1, char* i2, char* o1, char* o2){
	//printf("circular");
	int i = 0;

	for(i=0;i<SIZE_LOCAL;i++){
		o1[i] = i1[i];
		o2[i] = i2[i];
	}
}
void circular6(char* i1, char* o1){
	//printf("circular6");
	int i = 0;

	for(i=0;i<SIZE_LOCAL;i++){
		o1[i] = i1[i];
	}
}

void actuator(char* i1, int size,char* i2,char* i3){
	//printf("actuator");
	int i = 0;
	int bSuccess = 1;

	for(i=0;i<TESTVALUE;i++){
		if(i1[i] != nbrand){
			bSuccess = 0;
			break;
		}
		if(i2[i] != nbrand){
			bSuccess = 0;
			break;
		}
		if(i3[i] != nbrand){
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


