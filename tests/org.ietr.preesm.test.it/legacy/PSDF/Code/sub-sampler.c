/*******************************************************************************
 * Copyright or © or Copr. 2011 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Jonathan Piat <jpiat@laas.fr> (2011)
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
#include <stdlib.h>  
#include <stdio.h> 


#define LENGTH_S 21 
#define LENGTH_NP 4 
#define LENGTH_R 3

char samples [] = {1, 2, 3, 4, 5 , 7, 8, 9, 10, 11, 12, 13, 14, 15 , 16, 17, 18, 20} ;
char phases [] = {0, 1, 2, 3} ;
char rates [] = {5, 8, 12} ;


unsigned int nr = 0 ;
unsigned int np = 0 ;
unsigned int r = 0 ;

void subSample(char * sampleIn, char * sampleOut, long rate, long phase){
	sampleOut[0] = sampleIn[phase] ;
}


void initNb(long * rate){
	sleep(3);
	*rate = rates[r];
	r = (r + 1)% LENGTH_R;
	printf("Configure to rate %d \n", *rate);
}

void initPhase(char * phase, long * p){
	* p = (char) *phase ;
	printf("Configure to phase %d \n", *p);
}

void generate_phase(char * p){
	*p = phases[np];
	np = (np + 1)% LENGTH_NP;
}

void generate_sample(char * s){
	*s = samples[nr] ;
	nr = (nr+1)%LENGTH_S ;
}

void collect_sample(char * s){
	printf("%d \n", *s);
}
