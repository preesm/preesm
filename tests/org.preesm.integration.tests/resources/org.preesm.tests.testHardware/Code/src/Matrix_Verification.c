#include "Matrix_Verification.h"

//int matrix_verification_of2(int n_rows, int n_cols, IN int* A, OUT int* B){
//    int i, errors=0;
//    for( i=0; i < n_rows * n_cols; i++){
//        if (A[i] != B[i]){
//            errors++;
//        }
//    }
//    if(errors==0){
//        printf("-----------TEST OK----------\n");
//    }
//    else{
//        printf("-----------ERRORS %d ----------\n",errors);
//    }
//    return errors;
//}


void matrix_verification(int n_rows, int n_cols, IN int* matrix_hw){

	static unsigned int mean_loop_size = 0;
	static struct timeval t0, tf;
	float time_one_matrix;

	if(mean_loop_size !=0 && (mean_loop_size % MEAN_LOOP_SIZE)==0 ){
		gettimeofday(&tf, NULL);
		time_one_matrix = ((tf.tv_sec - t0.tv_sec) * 1000.0) + ((tf.tv_usec - t0.tv_usec) / 1000.0);
		printf("one matrix multiplication : %.6f ms\n", time_one_matrix);
		gettimeofday(&t0, NULL);
	}
	if (mean_loop_size==0){
		gettimeofday(&t0, NULL);
	}

	mean_loop_size++;

#ifdef VERIFY_RESULTS
#ifdef VERBOSE
	printf("verification test ongoing\n");
#endif // VERBOSE
    unsigned int i;
    int size= n_cols*n_rows;
    int *A, *B, *C;
    A = (int*)malloc(size*sizeof(int));
    B = (int*)malloc(size*sizeof(int));
    C = (int*)malloc(size*sizeof(int));

    for (i = 0; i < (n_cols * n_rows); i++) {
        // matrix[i] = rand();
        A[i] = i;
        B[i] = i;
        C[i] = 0;
    }
    //matmul_sw(4/*tile_dim_size*/,4/*n_rows*/,4/*n_cols*/,A,B,C);
    matmul(n_cols,A,B,C);


//    printf("A-------->\n");
//    Matrix_Show(n_cols,n_rows, A);
//    printf("B-------->\n");
//    Matrix_Show(n_cols,n_rows, B);
//    printf("hw-------->\n");
//    Matrix_Show(n_cols,n_rows, matrix_hw);
//    printf("C-------->\n");
//    Matrix_Show(n_cols,n_rows, C);

    int errors=0;
    for(i=0; i<size; i++){
    	if(C[i] != matrix_hw[i]){
    		errors++;
    	}
    }

    if(errors==0){
    	printf("------TEST OK---------\n");
    	printf("------MATRIX %d x %d ---------\n",n_rows,n_rows);
    	//printf("------TILE   %d x %d ---------\n");
    }
    else{
    	printf("-------ERRORS %d ---------------\n", errors);
    }
#ifdef VERBOSE
    //Matrix_Show( n_cols, n_rows, C);
    //Matrix_Show( n_cols, n_rows, matrix_hw);
#endif //VERBOSE
    free(A);
    free(B);
    free(C);


#endif //VERIFY_RESULTS
    return;
}
