/*
	============================================================================
	Name        : sobel.h
	Author      : orenaud
	Version     : 1.1
	Copyright   : CeCILL-C
	Description :
	============================================================================
*/

#ifndef CODE_H
#define CODE_H

#include "preesm.h"


__global__ void A( OUT  char *out,OUT  char *out2);

__global__ void sink( IN  char *in);

#endif
