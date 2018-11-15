/*
	============================================================================
	Name        : medianFilter.h
	Author      : kdesnos
	Version     : 1.0
	Copyright   : CeCILL-C, IETR, INSA Rennes
	Description : Application of a 3x3 median filter to an image.
	============================================================================
*/

#ifndef MEDIAN_FILTER_H
#define MEDIAN_FILTER_H

/**
* This function applies a median filter to the input image. 
* This filter consists of replacing each pixel with the median value of its
* 3x3 neighborhood. For example:
* |18|21|13|  => {13, 13, 16, 15, 18, 18, 19, 20, 21}
* |15|20|19|  => 20 is replaced with 18. 
* |13|18|16|
*
* @param height
*        The height of the filtered image
* @param width
*        The width of the filtered image
* @param topDownBorderSize
*        Number of unfiltered lines at the top and bottom of the image
* @param rawDisparity
*        The height*width input image
* @param filteredDisparity
*        The (height-topDownBorderSize)*width output image
*/
void medianFilter (int height , int width, int topDownBorderSize, 
                   unsigned char *rawDisparity,
				   unsigned char *filteredDisparity);

/**
* Recursive quicksort implementation.
* This function sorts the values comprised between the startIdx and the endIdx 
* in the input array.
*
* @param startIdx
*        Index of the first value to sort in the input array.
* @param endIdx
*        Index of the last value to sort in the input array (inclusive).
* @param values 
*        Array containing the values to sort.
*/
void quickSort(int startIdx, int endIdx, unsigned char *values);

/**
* This function partition the values between the start and end indexes in two
* groups. Values at the left of the pivot index have a smaller value than the 
* pivot. Values at the right of the pivot index have a larger value than the 
* pivot.
* @param startIdx
*        Index of the first value to partition in the input array.
* @param endIdx
*        Index of the last value to partition in the input array (inclusive).
* @param pivotIdx
*        Position of the pivot after the partitionning
* @param values
*        Array containing the values to partition.
*/
void quickSortPartition(int startIdx, int endIdx, int *pivotIdx, unsigned char *values);

/**
* This function swap two values.
* Example: a:=3, b:=8 => swap(&a,&b) =>  a:=8, b:=3
*
* @param a 
*        The first value to swap.
* @param b 
*        The second value to swap.
*/
void swap(unsigned char *a, unsigned char *b);

#endif
