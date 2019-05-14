#ifndef MATRIX_MUL_H
#define MATRIX_MUL_H

#include "preesm.h"
#include <stdio.h>
#include <stdlib.h>


//void matmul_sw(int tile_dim_size, int n_rows, int n_cols, IN int* matrix_in1, IN int* matrix_in2, OUT int* matrix_out);
void matmul_sw(int tile_dim_size,
			   int n_rows,
			   int n_cols,
			   IN int* matrix_in1,
			   IN int* matrix_in2,
			   OUT int* matrix_out);

//void matmul(int tile_dim_size,
//			int n_rows,
//			int n_cols,
//			IN int* matrix_in1,
//			IN int* matrix_in2,
//			OUT int* matrix_out);

void matmul(int tile_dim_size,
		IN int* matrix_in1,
		IN int* matrix_in2,
		OUT int* matrix_out);

#endif //MATRIX_MUL_H
