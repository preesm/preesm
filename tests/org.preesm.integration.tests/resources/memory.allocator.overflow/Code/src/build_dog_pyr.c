#include <stdlib.h>
#include <stdio.h>

#include "ezsift-common.h"

void ITERATOR_build_dog_pyr(int parallelismLevel, int nOctaves, int nDogLayers,
			    int image_width, int image_height, int imgDouble,
			    OUT int * start_octave, OUT int * stop_octave,
			    OUT int * start_layer, OUT int * stop_layer,
			    OUT int * start_line,  OUT int * stop_line,
			    OUT int * start_col, OUT int * stop_col) {

#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  ITERATOR_generic(parallelismLevel, nOctaves, nDogLayers,
		   image_width, image_height, imgDouble,
		   start_octave, stop_octave,
		   start_layer, stop_layer,
		   start_line, stop_line,
		   start_col, stop_col);  
}

// Build difference of Gaussian pyramids.
void build_dog_pyr(int nGpyrLayers, int totSizeWithoutLayers,
		   int parallelismLevel, int nDogLayers,
		   int image_width, int image_height, int imgDouble,
		   IN float * gpyr, OUT float * dogPyr, 
		   IN int * start_octave, IN int * stop_octave,
		   IN int * start_layer, IN int * stop_layer,
		   IN int * start_line, IN int * stop_line,
		   IN int * start_col, IN int * stop_col) {

#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  size_t offset_gpyr = 0;
  size_t offset_dogPyr = 0;
  size_t w = image_width;
  size_t h = image_height;
  if (imgDouble) {
    w *= 2;
    h *= 2;
  }
  size_t tot_size = w*h;


/* #ifdef SIFT_DEBUG */
/* char fdog[512]; */
/* unsigned char buffer[tot_size]; */
/* #endif */



  for (int k = 0; k < *start_octave; k++) {
    offset_gpyr +=  tot_size*nGpyrLayers;
    offset_dogPyr +=  tot_size*nDogLayers;
    w >>= 1;
    h >>= 1;
    tot_size >>= 2;
  }
  size_t layer_offset = tot_size*(*start_layer);
  size_t line_offset = w*(*start_line);
  size_t col_offset = *start_col;
  offset_gpyr += layer_offset + line_offset + col_offset;
  offset_dogPyr += layer_offset + line_offset + col_offset;
  
  float * dstData = dogPyr;// + offset_dogPyr;
  float * srcData1 = gpyr + offset_gpyr;
  float * srcData2 = gpyr + offset_gpyr + tot_size;

  size_t index = 0;

  
  /* fprintf(stderr, "ptr: %lu, %d-%d\t%d-%d\t%d-%d\t%d-%d\n", (unsigned long) dstData, *start_octave, *stop_octave, *start_layer, *stop_layer, *start_line, *stop_line, *start_col, *stop_col); */
  for (int i = *start_octave; i < *stop_octave; i ++) {
    int begin_layer = (i == *start_octave) ? *start_layer : 0;
    int end_layer = (i+1 == *stop_octave) ? *stop_layer : nDogLayers;
    for (int j = begin_layer; j < end_layer; j ++) {
      int begin_line = (i == *start_octave) && (j == *start_layer) ? *start_line : 0;
      int end_line = (i+1 == *stop_octave) && (j+1 == *stop_layer) ? *stop_line : h;
      
      int begin_col = (i == *start_octave) && (j == *start_layer) ? *start_col : 0;
      int end_col = (i+1 == *stop_octave) && (j+1 == *stop_layer) ? *stop_col : w;
      index = begin_line*w + begin_col;

      size_t maxIndex = (end_line - 1)*w + end_col;
      while (index ++ < maxIndex) {
	*(dstData ++) = *(srcData2 ++) - *(srcData1 ++);
      }
      /* fprintf(stderr, "index = %lu\toctave: %d\tlayer: %d\tline: %d-%d\tcol: %d-%d\n", (unsigned long) dstData, i, j, begin_line, end_line, begin_col, end_col); */
      
/* #ifdef SIFT_DEBUG */
/*       size_t sizeIn = tot_size; */
/*       if (begin_col == 0 && begin_line == 0 && index == sizeIn + 1) { */
/* 	sprintf(fdog, "raw_dog-%d-%d.pgm", i, j); */
/* 	write_float_pgm(fdog, dstData-sizeIn, buffer, w, h, 2); */
/*       } */
/* #endif */
    }
    w >>= 1;
    h >>= 1;
    tot_size >>= 2;
    srcData1 = srcData2;
    srcData2 += tot_size;
  }
}
