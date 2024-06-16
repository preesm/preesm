/**
* Matrix and vectors related functions
*
* @file matrix.h
* @author kdesnos
* @date 2016.09.01
* @version 1.0
* @copyright CECILL-C
*/

#ifndef MATRIX_H
#define MATRIX_H

/**
* Structure used to store 2D coordinates.
*/
typedef struct coord {
	int x; //!< Abscissa
	int y; //!< Ordinate
} coord;

/**
* Structure used to store 2D coordinates as floats.
* These coordinates are stored as float to avoid accumlating rounding errors
* when computing cumulated motion and average motion vectors.
*/
typedef struct coordf {
	float x; //!< Abscissa
	float y; //!< Ordinate
} coordf;

/*
* Structure used to store two by two matrices with float coefficients.
*/
typedef struct matrix {
	float coeffs[2 * 2];
} matrix;

/**
* Computes the mean vector of an array.
*
* @param nbVector
*		number of vectors in the array
* @param vectors
*       array of vectors.
* @param mean
*       computed mean vectors (with float coefficients).
*/
void meanVector(const unsigned int nbVector, const coord * const vectors,
				coordf * const mean);

/**
* Computes the covariance matrix of an array of vectors.
*
* @param nbVector
*		number of vectors in the array
* @param vectors
*       array of vectors.
* @param mean
*       pre-computed mean of the array of vectors.
* @param sigma
*       computed covariance matrix.
*/
void covarianceMatrix2D(const unsigned int nbVector, const coord * const vectors,
						const coordf * const mean, matrix * const sigma);


/**
* Computes the probability of the given coordinates according to the
* multivariate gaussian distribution.
*
*
* @param nbVector
*		number of vectors in the array
* @param vectors
*       array of vectors.
* @param mean
*       pre-computed mean for the distribution.
* @param sigma
*       pre-computed co-variance matrix for the distribution
* @param proba
*       resulting probabilities for all vectors in the input array.
*/
void getProbabilities(const unsigned int nbVector, const coord * const vectors,
					  const coordf * const mean, matrix * const sigma, float * proba);

/**
* Computes the inverse matrix.
*
* Invertability of the matrix is not verified in this function. a non-
* invertible matrix will cause a division by 0;
*
* @param mat
*       the 2-by-2 matrix to invert.
* @param invMat
*       inversion result.
*/
void inverseMatrix2D(const matrix * const mat, matrix * const invMat);
#endif