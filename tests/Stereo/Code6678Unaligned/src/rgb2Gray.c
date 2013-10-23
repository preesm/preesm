/*
 ============================================================================
 Name        : rgb2Gray.c
 Author      : kdesnos
 Version     : 1.0
 Copyright   : CECILL-C, IETR, INSA Rennes
 Description : Transformation of an RGB image into a gray-level image.
 ============================================================================
 */

#include "utils.h"
#include "rgb2Gray.h"

#define RGB2GRAY_COEF_R 0.29893602129378
#define RGB2GRAY_COEF_G 0.58704307445112
#define RGB2GRAY_COEF_B 0.11402090425510

void rgb2Gray(int size, unsigned char *r, unsigned char *g, unsigned char *b,
		float *gray) {
	int idx;
	for (idx = 0; idx < size; idx++) {
		float res = RGB2GRAY_COEF_R * (float) r[idx]
				+ RGB2GRAY_COEF_G * (float) g[idx]
				+ RGB2GRAY_COEF_B * (float) b[idx];

		STORE_FLOAT(&gray[idx], &res);
	}
}
