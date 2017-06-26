/*
	============================================================================
	Name        : datCreate.h
	Author      : mpelcat
	Version     : 1.0
	Copyright   : CECILL-C
	Description : creating a data file to load in CCS
	============================================================================
*/

#ifndef DATCREATE_H
#define DATCREATE_H

#define SUCCESS 0
#define FAILURE -1

/**
* Initialize the dat file.
*
* @param fileName
*        The path of the created Dat file
* @param size
*        The number of bytes to store in total
*/
int createDatFile(char* filePath, int size);

/**
* Writing a buffer in the dat file.
*
* @param buffer
*        the buffer to write
* @param size
*        The number of bytes to store
*/
void writeData(unsigned char* buffer, int size);

void closeDatFile();

#endif
