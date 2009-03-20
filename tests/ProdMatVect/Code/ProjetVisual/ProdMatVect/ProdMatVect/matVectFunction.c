
#include<time.h>


void mult(int * op1, int * op2, int * result){
	*result = (*op1) * (*op2) ;
	//printf("%d x %d = %d \n", (*op1), (*op2), (*result));
}

void add(int * op1, int * op2, int * result){
	*result = (*op1) + (*op2) ;
	//printf("%d + %d = %d \n", (*op1), (*op2), (*result));
}

void copyData(int * in, int * out1, int * out2){
	*out1 = *in ;
	*out2 = *in ;
}
void display(int * input, int size){
	int i, j ;
	printf("[");
	for(j=0; j < size ; j ++){
		printf("%d,", input[j]);
	}
	printf("] \n");
}

void generateMatrix(int * matOut, int size){
	int i, j ;
	srand(time(NULL)); 
	for(i = 0 ; i < size*size ; i ++){
			matOut[i] = 2 ;//rand();
	}
	display(matOut, size*size);
}

void generateVect(int * matVect, int size){
	int i;
	srand(time(NULL)); 
	for(i = 0 ; i < size ; i ++){
			matVect[i] = 1 ;//rand();
	}
	display(matVect, size);
}

void init_accIn(int * data, int size){
	int i;
	for(i = 0 ; i < size ; i ++){
			data[i] = 0;
	}
}

void init_in(int * data, int size){
	int i;
	for(i = 0 ; i < size ; i ++){
			data[i] = 0;
	}
}