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

#include<time.h>


void mult(char * op1, char * op2, char * result){
	*result = (*op1) * (*op2) ;
	//printf("%d x %d = %d \n", (*op1), (*op2), (*result));
}

void add(char * op1, char * op2, char * result){
	*result = (*op1) + (*op2) ;
	//printf("%d + %d = %d \n", (*op1), (*op2), (*result));
}

void copyData(char * in, char * out1, char * out2){
	*out1 = *in ;
	*out2 = *in ;
}
void display(char * input, char size){
	int i, j ;
	printf("[");
	for(j=0; j < size ; j ++){
		printf("%d,", input[j]);
	}
	printf("] \n");
}

void generateMatrix(char * matOut, char size){
	int i, j ;
	srand(time(NULL)); 
	for(i = 0 ; i < size ; i ++){
			matOut[i] = 5 ;//rand();
	}
	display(matOut, size);
}

void generateVect(char * matVect, char size){
	int i;
	srand(time(NULL)); 
	for(i = 0 ; i < size ; i ++){
			matVect[i] = 1 ;//rand();
	}
	display(matVect, size);
}

void init_accIn(char * data, char size){
	int i;
	for(i = 0 ; i < size ; i ++){
			data[i] = 0;
	}
}

void init_inLoopPort_0(char * data, char size){
	int i;
	for(i = 0 ; i < size ; i ++){
			data[i] = 0;
	}
}