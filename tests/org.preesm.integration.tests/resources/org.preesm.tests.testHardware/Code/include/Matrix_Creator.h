/*
	============================================================================
	Name        : Matrix_Creator.h
	Author      : Leonardo Suriano
	Version     : 1.0
	Copyright   : UPM
	Description :
	============================================================================
*/

#ifndef MATRIX_CREATOR_H
#define MATRIX_CREATOR_H

#include "preesm.h"
#include <stdio.h>
#include <stdlib.h>
#include <time.h>     // time()



/**
* It fills the matrix with random number.
*
*/
void Matrix_Creator(int matrix_size, int n_cols, int n_rows, OUT int *matrix);

#endif //MATRIX_CREATOR_H
