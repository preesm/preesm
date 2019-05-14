#include "Matrix_Accumulator.h"



void matrix_acculmulator(int tile_dim_size, int n_cols, int n_rows, IN int* partial, OUT int* matrix_out){
#ifdef VERBOSE
  printf("accumulator\n");
#endif //VERBOSE
  unsigned int i, j, k, i2, j2;
  unsigned int counter3=0;
  unsigned int matrix_out_size = n_cols*n_rows;
  //resetting output before accumulation
  for (i=0; i < matrix_out_size ; i++){
	  matrix_out[i]=0;
  }

  for (i = 0; i < n_cols; i += tile_dim_size) {
      for (j = 0; j < n_cols; j += tile_dim_size) {
          // Copy partial output
          for (k = 0; k < n_cols; k+=tile_dim_size) {
              for (i2 = 0; i2 < tile_dim_size; i2++) {
                  for (j2 = 0; j2 < tile_dim_size; j2++) {
                      matrix_out[((i + i2) * n_cols) + (j + j2)] += partial[counter3];
                      counter3++;
                  }
              }
          }
      }
  }
}
