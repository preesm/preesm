#ifndef MATRIX_TILING_H
#define MATRIX_TILING_H

#include "preesm.h"
#include <stdio.h>
#include <stdlib.h>

void matrix_tiling_left(int n_cols,
                        int n_rows,
                        int tile_dim_size,
                        IN int* matrix_in,
                        OUT int* matrix_out);
void matrix_tiling_right(int n_cols,
                         int n_rows,
                         int tile_dim_size,
                         IN int* matrix_in,
                         OUT int* matrix_out);

#endif //MATRIX_TILING_H
