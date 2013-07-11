/*
	============================================================================
	Name        : dump.h
	Author      : kdesnos
	Version     :
	Copyright   :
	Description :
	============================================================================
*/

#ifndef DUMP_H
#define DUMP_H

#define DUMP_FILE "D:/SVN/preesm/trunk/tutorials/tutorial-sobel/Code/generated/analysis.csv"
void dumpTime(int id,long* dumpBuffer);

void writeTime(long* dumpBuffer, int nbDump, int* nbExec);

void initNbExec(int* nbExec, int nbDump);

#endif