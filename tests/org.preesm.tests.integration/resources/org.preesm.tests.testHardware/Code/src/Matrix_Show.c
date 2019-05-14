#include "Matrix_Show.h"

void Matrix_Show(int n_cols, int n_rows, OUT int *matrix){

	unsigned int i,j;
	for(i=0;i<n_cols;i++){
		for(j=0;j<n_rows;j++){
			printf("\t%d",matrix[j+i*n_rows]);
		}
		printf("\n");
	}
	return;
}
