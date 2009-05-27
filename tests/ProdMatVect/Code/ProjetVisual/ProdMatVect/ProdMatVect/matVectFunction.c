
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