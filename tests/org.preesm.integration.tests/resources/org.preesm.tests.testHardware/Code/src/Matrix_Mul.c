#include "Matrix_Mul.h"


//void matmul_sw(int tile_dim_size, int n_rows, int n_cols, IN int* matrix_in1, IN int* matrix_in2, OUT int* matrix_out){
void matmul(int tile_dim_size, IN int* matrix_in1, IN int* matrix_in2, OUT int* matrix_out){
	int n_rows = tile_dim_size;
	int n_cols = tile_dim_size;
    unsigned int i, j, k;

    for (i = 0; i < tile_dim_size; i++) {
        for (j = 0; j < tile_dim_size; j++) {
        	matrix_out[(i * tile_dim_size) + j] = 0;
            for (k = 0; k < tile_dim_size; k++) {
            	matrix_out[(i * tile_dim_size) + j] += matrix_in1[(i * tile_dim_size) + k] * matrix_in2[(k * tile_dim_size) + j];
            }
        }
    }
    return;
}
