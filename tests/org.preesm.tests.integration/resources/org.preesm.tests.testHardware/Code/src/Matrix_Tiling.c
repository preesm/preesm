#include "Matrix_Tiling.h"


// n_cols ---> n_cols in my case
// tile_dim_size ---> tile_dim_size in my case


void matrix_tiling_left(int n_cols, int n_rows, int tile_dim_size, IN int* matrix_in, OUT int* matrix_out){
#ifdef VERBOSE
	printf("tiling left\n");
#endif
	unsigned int i, j, k, i2, j2, counter1=0;
	for (i = 0; i < n_rows; i += tile_dim_size) {
			for (j = 0; j < n_rows; j += tile_dim_size) {
					// Copy partial inputs
					for (k = 0; k < n_rows; k+=tile_dim_size) {
							for (i2 = 0; i2 < tile_dim_size; i2++) {
									for (j2 = 0; j2 < tile_dim_size; j2++) {
											matrix_out[counter1]=matrix_in[((i + i2) * n_rows) + (k + j2)];
											counter1++;
									}
							}
					}
			}
	}
	return;
}


void matrix_tiling_right(int n_cols, int n_rows, int tile_dim_size, IN int* matrix_in, OUT int* matrix_out){
#ifdef VERBOSE
	printf("tiling right\n");
#endif
	unsigned int i, j, k, i2, j2, counter1=0;
	for (i = 0; i < n_rows; i += tile_dim_size) {
			for (j = 0; j < n_rows; j += tile_dim_size) {
					// Copy partial inputs
					for (k = 0; k < n_rows; k+=tile_dim_size) {
							for (i2 = 0; i2 < tile_dim_size; i2++) {
									for (j2 = 0; j2 < tile_dim_size; j2++) {
											matrix_out[counter1]=matrix_in[((k + i2) * n_rows) + (j + j2)];
											counter1++;
									}
							}
					}
			}
	}

	return;
}
