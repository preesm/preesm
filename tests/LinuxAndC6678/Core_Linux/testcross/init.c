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
