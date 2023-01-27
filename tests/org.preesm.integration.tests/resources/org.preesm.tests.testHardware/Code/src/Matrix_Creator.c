#include "Matrix_Creator.h"





void Matrix_Creator(int matrix_size, int n_cols, int n_rows, OUT int *matrix){
#ifdef VERBOSE
    printf("Initializing Matrix...\n");
#endif
    unsigned int i;
    srand(time(NULL));
    for (i = 0; i < (n_cols * n_rows); i++) {
        // matrix[i] = rand();
        matrix[i] = i;
    }
    return;
}
