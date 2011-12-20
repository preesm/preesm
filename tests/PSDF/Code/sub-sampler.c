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
