/*******************************************************************************
 * Copyright or © or Copr. %%LOWERDATE%% - %%UPPERDATE%% IETR/INSA:
 *
 * %%AUTHORS%%
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
/*
 * init.c
 *
 *  Created on: July 17, 2013
 *      Author: rparois
 */

#include "init.h"


#ifndef NULL
#define NULL 0
#endif

int readNbRepeat(FILE* f) {
	int readChar = 0;
	int result = 0;
//    int i = 0;
	char tmp[100];
	rewind(f);
	while (readChar != EOF) {
		readChar = fgetc(f);
		if (readChar == ';')
			fgets(tmp, 100, f);
		if (readChar == 'n') {
			fgets(tmp, 8, f);
			if (strcmp(tmp, "bRepeat") == 0) {
				while (readChar != '=') {
					readChar = fgetc(f);
				}
				fgets(tmp, 100, f);
				result = strtol(tmp, NULL, 10);

			}
		}
	}
	return result;
}

int readSize(FILE* f) {
	int readChar = 0;
	int Size = 1;
	int res = 0;
	char tmp[100];
	rewind(f);
	while (readChar != EOF) {
		readChar = fgetc(f);
		if (readChar == ';')
			fgets(tmp, 100, f);
		if (readChar == 'i') {
			fgets(tmp, 4, f);
			if (strcmp(tmp, "mage")) {
				while (readChar != '=') {
					readChar = fgetc(f);
				}
				fgets(tmp, 100, f);
				Size *= strtol(tmp, NULL, 10);
			}
		}
		if (readChar == 'c') {
			fgets(tmp, 6, f);
			if (strcmp(tmp, "olored")) {
				while (readChar != '=') {
					readChar = fgetc(f);
				}
				fgets(tmp, 100, f);
				if (strcmp(tmp, "yes"))
					Size *= 3;
				res = Size;
			}
		}
	}
	return res;
}

int readHeap(FILE* f) {
	int readChar = 0;
	int Size = 0;
	int res = 0;
	char tmp[100];
	rewind(f);
	while (readChar != EOF) {
		readChar = fgetc(f);
		if (readChar == ';')
			fgets(tmp, 100, f);
		if (readChar == 'h') {
			fgets(tmp, 3, f);
			if (strcmp(tmp, "eap")) {
				while (readChar != '=') {
					readChar = fgetc(f);
				}
				fgets(tmp, 100, f);
				Size = strtol(tmp, NULL, 16);
				res = Size;
			}
		}
	}
	return res;
}


int readAddress(char * res, FILE* f) {
	int readChar = 0;
//    int i = 0;
	char tmp[100];
	rewind(f);
	while (readChar != EOF) {
		readChar = fgetc(f);
		if (readChar == ';')
			fgets(tmp, 100, f);
		if (readChar == 'a') {
			fgets(tmp, 7, f);
			if (strcmp(tmp, "ddress") == 0) {
				while (readChar != '=') {
					readChar = fgetc(f);
				}
				fgets(res, 15, f);
				res[15] = '\0';
			}
		}
	}
	return 1;
}
