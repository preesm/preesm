#ifndef MATRIX_VERIFICATION_H
#define MATRIX_VERIFICATION_H

#include "preesm.h"
#include <stdio.h>
#include <stdlib.h>
#include <time.h>     // time()

#define MEAN_LOOP_SIZE (1)
#define VERIFY_RESULTS
//#define VERBOSE
//int matrix_verification_of2(int n_rows, int n_cols, IN int* A, OUT int* B);
void matrix_verification(int n_rows, int n_cols, IN int* matrix_hw);

#endif //MATRIX_VERIFICATION_H
