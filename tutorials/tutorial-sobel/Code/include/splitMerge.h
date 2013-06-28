/*
	============================================================================
	Name        : splitMerge.h
	Author      : kdesnos
	Version     :
	Copyright   :
	Description :
	============================================================================
*/

#ifndef SPLIT_MERGE_H
#define SPLIT_MERGE_H


void split(int nbSlice, int xSize, int ySize, unsigned char *input, unsigned char *output);
void merge(int nbSlice, int xSize, int ySize, unsigned char *input, unsigned char *output);

#endif
