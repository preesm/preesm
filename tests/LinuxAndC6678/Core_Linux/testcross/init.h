/*
 * init.h
 *
 *  Created on: July 17, 2013
 *      Author: rparois
 */

#ifndef INIT_H_INCLUDED
#define INIT_H_INCLUDED


#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/* Read the size of image in file .ini */
int readSize(FILE* f);

/* Read heap size in file .ini */
int readHeap(FILE* f);

/* Read buffer location for processing */
int readBuffer(long b, FILE* f);		//Re-written in client.c

/* Read address for socket communication */
int readAddress(char * res, FILE* f);


#endif // INIT_H_INCLUDED
