#ifndef MATRIX_ACCUMULATOR_H
#define MATRIX_ACCUMULATOR_H

#include "preesm.h"
#include <stdio.h>
#include <stdlib.h>

void matrix_acculmulator(int tile_dim_size,
                         int n_cols,
                         int n_rows,
                         IN int* partial,
                         OUT int* matrix_out);

#endif //MATRIX_ACCUMULATOR_H
