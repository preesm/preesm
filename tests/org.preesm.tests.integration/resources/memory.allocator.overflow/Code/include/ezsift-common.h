/*	Copyright (c) 2013, Robert Wang, email: robertwgh (at) gmail.com
	All rights reserved. https://sourceforge.net/p/ezsift

	Revision history:
		September, 15, 2013: initial version.
*/

#ifndef SIFT_COMMOM_H
#define SIFT_COMMOM_H

#include <math.h>

#include "ezsift-preesm.h"

// *** Optimization options ***
#define SIFT_USE_FAST_FUNC 1

// Print out matched keypoint pairs in match_keypoints() function.
#define	PRINT_MATCH_KEYPOINTS 0


// *** Macro definition ***
// Macro definition#define PI				3.141592653589793f
#define _2PI			6.283185307179586f
#define PI_4			0.785398163397448f
#define PI_3_4			 2.356194490192345f
#define SQRT2			1.414213562373095f
#define EPSILON_F	1.19209290E-07F


void ITERATOR_generic(int parallelismLevel, int nbOctaves, int nbLayers,
		      int image_width, int image_height, int imgDouble,
		      OUT int * start_octave, OUT int * stop_octave,
		      OUT int * start_layer, OUT int * stop_layer,
		      OUT int * start_line,  OUT int * stop_line,
		      OUT int * start_col,  OUT int * stop_col);


// *** Fast math functions ***

static inline float fast_atan2_f (float y, float x)
{
  float angle, r ;
  float const c3 = 0.1821F ;
  float const c1 = 0.9675F ;
  float abs_y    = fabsf (y) + EPSILON_F ;

  if (x >= 0) {
    r = (x - abs_y) / (x + abs_y) ;
    angle = PI_4; //
  } else {
    r = (x + abs_y) / (abs_y - x) ;
    angle = PI_3_4; //
  }
  angle += (c3*r*r - c1) * r ;

  return (y < 0) ? _2PI - angle : angle;
}

// Fast Sqrt() function
static inline float fast_resqrt_f (float x)
{
	/* 32-bit version */
	union {
		float x ;
		int  i ;
	} u ;

	float xhalf = (float) 0.5 * x ;

	/* convert floating point value in RAW integer */
	u.x = x ;

	/* gives initial guess y0 */
	u.i = 0x5f3759df - (u.i >> 1);

	/* two Newton steps */
	u.x = u.x * ( (float) 1.5  - xhalf*u.x*u.x) ;
	u.x = u.x * ( (float) 1.5  - xhalf*u.x*u.x) ;
	return u.x ;
}

static inline float fast_sqrt_f (float x)
{
	return (x < 1e-8) ? 0 : x * fast_resqrt_f (x) ;
}

static inline int tmin_i(int n1, int n2) {
  if (n1 < n2) {
    return n1;
  }
  return n2;
}

static inline float tmin_f(float n1, float n2) {
  if (n1 < n2) {
    return n1;
  }
  return n2;
}

static inline int tmax_i(int n1, int n2) {
  if (n1 < n2) {
    return n2;
  }
  return n1;
}

static inline float tmax_f(float n1, float n2) {
  if (n1 < n2) {
    return n2;
  }
  return n1;
}

static inline size_t tmin_s(size_t n1, size_t n2) {
  if (n1 < n2) {
    return n1;
  }
  return n2;
}


// Image operations
// Get pixel from an image with unsigned char datatype.
static inline unsigned char get_pixel(
	unsigned char * imageData, 
	int w, int h, 
	int r, int c)
{	
	unsigned char val; 
	if ( c >= 0 && c < w && r >= 0 && r < h)
	{
		val = imageData[r * w + c];
	}else if (c < 0){
		val = imageData[r * w];
	}else if (c >= w){
		val = imageData[r * w + w - 1];
	}else if (r < 0){
		val = imageData[c];
	}else if (r >= h){
		val = imageData[(h-1) * w + c];
	}else{
		val = 0;
	}
	return val;
}

// Get pixel value from an image with float data type.
static inline float get_pixel_f(
	float * imageData, 
	int w, int h, 
	int r, int c)
{	
	float val; 
	if ( c >= 0 && c < w && r >= 0 && r < h)
	{
		val = imageData[r * w + c];
	}else if (c < 0){
		val = imageData[r * w];
	}else if (c >= w){
		val = imageData[r * w + w - 1];
	}else if (r < 0){
		val = imageData[c];
	}else if (r >= h){
		val = imageData[(h-1) * w + c];
	}else{
		val = 0.0f;
	}
	return val;
}

#endif
