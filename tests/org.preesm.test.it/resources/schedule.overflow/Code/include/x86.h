/*
	============================================================================
	Name        : x86.h
	Author      : kdesnos
	Version     :
	Copyright   :
	Description :
	============================================================================
*/

#ifndef X86_H
#define X86_H

#define VERBOSE
	
// For Linux
// Pthread barriers are defined in POSIX 2001 version
// For the 1990 revision compliance the defined value of _POSIX_VERSION should be 1.
// For the 1995 revision compliance the defined value of _POSIX_VERSION should be 199506L.
// For the 2001 revision compliance the defined value of _POSIX_VERSION should be 200112L.
// #define _POSIX_C_SOURCE 200112L (already defined in my configuration)
//#define _XOPEN_SOURCE 600


#include <pthread.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#endif
