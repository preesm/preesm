/*
 ============================================================================
 Name        : cores.h
 Author      : kdesnos
 Version     : 1.0
 Copyright   : CeCILL-C, IETR, INSA Rennes
 Description : Declaration of the core-specific methods.
 ============================================================================
 */

#ifndef CORES_H_
#define CORES_H_

#include "../src/c6678.h"

typedef unsigned char uchar;

#define CORE0
void core0(void);
#define CORE1
void core1(void);
#define CORE2
void core2(void);
#define CORE3
void core3(void);
#define CORE4
void core4(void);
#define CORE5
void core5(void);
#define CORE6
void core6(void);
#define CORE7
void core7(void);

#endif /* CORES_H_ */
