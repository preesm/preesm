/*	Copyright (c) 2013, Robert Wang, email: robertwgh (at) gmail.com
	All rights reserved. https://sourceforge.net/p/ezsift

	Some algorithms used in this code referred to:
	1. OpenCV: http://opencv.org/
	2. VLFeat: http://www.vlfeat.org/

	The SIFT algorithm was developed by David Lowe. More information can be found from:
	David G. Lowe, "Distinctive image features from scale-invariant keypoints," 
	International Journal of Computer Vision, 60, 2 (2004), pp. 91-110.

	Pay attention that the SIFT algorithm is patented. It is your responsibility to use the code
	in a legal way. Patent information:
	Method and apparatus for identifying scale invariant features in an image 
	and use of same for locating an object in an image	David G. Lowe, US Patent 6,711,293 
	(March 23, 2004). Provisional application filed March 8, 1999. Asignee: The University of 
	British Columbia.

	Revision history:
		September, 15, 2013: initial version.
		July 8th, 2014, re-organized source code. 
*/

#ifndef EZSIFT_PREESM_H
#define EZSIFT_PREESM_H

#include "preesm-specific.h"


/****************************************
 * Constant parameters
 ***************************************/

// default number of sampled intervals per octave
#define SIFT_INTVLS 3

// default sigma for initial gaussian smoothing
#define SIFT_SIGMA 1.6f

// the radius of Gaussian filter kernel 
// Gaussian filter mask will be (2*radius+1)x(2*radius+1).
// People use 2 or 3 most.
#define SIFT_GAUSSIAN_FILTER_RADIUS 3.0f

// default threshold on keypoint contrast |D(x)|
#define SIFT_CONTR_THR 8.0f

// default threshold on keypoint ratio of principle curvatures
#define SIFT_CURV_THR 10.0f

// The keypoint refinement smaller than this threshold will be discarded.
#define SIFT_KEYPOINT_SUBPiXEL_THR 0.6f

// assumed gaussian blur for input image
#define SIFT_INIT_SIGMA 0.5f

// width of border in which to ignore keypoints
#define SIFT_IMG_BORDER 5

// maximum steps of keypoint interpolation before failure
#define SIFT_MAX_INTERP_STEPS 5

// default number of bins in histogram for orientation assignment
#define SIFT_ORI_HIST_BINS 36

// determines gaussian sigma for orientation assignment
#define SIFT_ORI_SIG_FCTR 1.5f // Can affect the orientation computation.

// determines the radius of the region used in orientation assignment
#define SIFT_ORI_RADIUS (3 * SIFT_ORI_SIG_FCTR) // Can affect the orientation computation.

// orientation magnitude relative to max that results in new feature
#define SIFT_ORI_PEAK_RATIO 0.8f

// maximum number of orientations for each keypoint location
//#define SIFT_ORI_MAX_ORI 4

// determines the size of a single descriptor orientation histogram
#define SIFT_DESCR_SCL_FCTR 3.f

// threshold on magnitude of elements of descriptor vector
#define SIFT_DESCR_MAG_THR 0.2f

// factor used to convert floating-point descriptor to unsigned char
#define SIFT_INT_DESCR_FCTR 512.f

// default value of the nearest-neighbour distance ratio threshold
// |DR_nearest|/|DR_2nd_nearest|<SIFT_MATCH_NNDR_THR is considered as a match.
#define SIFT_MATCH_NNDR_THR 0.65f

#define SIFT_INTERP_ORI_HIST 1
#define SIFT_USE_SMOOTH1 1
#define SIFT_ORI_BILINEAR 1


/****************************************
 * Definitions
 ***************************************/

typedef struct SiftKeypoint {
	int octave; // octave number
	int layer;  // layer number
	float rlayer; // real number of layer number

	float r; // normalized row coordinate
	float c; // normalized col coordinate
	float scale; // normalized scale

	float ri;	//row coordinate in that layer. 
	float ci;	//column coordinate in that layer.
	float layer_scale; // the scale of that layer

	float ori; // orientation in degrees.
	float mag; // magnitude

	float descriptors[SIFT_nBins];
} SiftKpt;

// Match pair structure. Use for interest point matching.
typedef struct MatchPair {
	int r1;
	int c1;
	int r2;
	int c2;

  float threshold;
} SiftMatch;

/* for sift */


void to_float(int parallelismLevel, int image_width, int image_height,
	      IN unsigned char * uchar_img, OUT float * float_img);

void compute_gaussian_coefs(int gWmax, int nGpyrLayers,
			    int nLayers, int imgDouble,
			    OUT int * columns_sizes,
			    OUT float * gaussian_coefs);

void counterGpyrLayer(int nGpyrLayers, OUT int * iter);

void BarrierTranspose_1(int image_width, int image_height,
			int parallelismLevel, int tot_image_size,
			IN float * img_in, OUT float * img_out);

void BarrierTranspose2x_1(int image_width, int image_height, int imgDouble,
			  int parallelismLevel, int tot_image_size,
			  IN float * img_in, OUT float * img_out);

void BarrierTranspose_2(int image_width, int image_height,
			int parallelismLevel, int tot_image_size,
			IN float * img_in, OUT float * img_out);

void BarrierTranspose2x_2(int image_width, int image_height, int imgDouble,
			  int parallelismLevel, int tot_image_size,
			  IN float * img_in, OUT float * img_out);


void row_filter_transpose_1(int image_height, int image_width, int nGpyrLayers,
			    int gWmax, int parallelismLevel,
			    IN float * gaussian_coefs, IN int * column_sizes,
			    IN float * img, IN float *imgIterPrev,
			    IN int * iter, OUT float * imgGT);

void row_filter_transpose2x_1(int image_height, int image_width, int nGpyrLayers,
			      int gWmax, int parallelismLevel, int imgDouble,
			      IN float * gaussian_coefs, IN int * column_sizes,
			      IN float * img, IN float *imgIterPrev,
			      IN int * iter, OUT float * imgGT);

void row_filter_transpose_2(int image_height, int image_width, int nGpyrLayers,
			    int gWmax, int parallelismLevel,
			    IN float * gaussian_coefs, IN int * column_sizes,
			    IN float * img, IN int * iter, OUT float * imgGT);

void row_filter_transpose2x_2(int image_height, int image_width, int nGpyrLayers,
			      int gWmax, int parallelismLevel, int imgDouble,
			      IN float * gaussian_coefs, IN int * column_sizes,
			      IN float * img, IN int * iter, OUT float * imgGT);

void seq_blur1(int image_height, int image_width, int tot_image_size, int nGpyrLayers, int gWmax,
	       int image_maxWH, IN float * gaussian_coefs, IN int * column_sizes,
	       IN float * fst_img, IN float * imgBlurPrev, IN int * iter, OUT float * imgBlurred);

void seq_blurN(int image_height, int image_width, int tot_image_size, int nGpyrLayers, int gWmax,
	       int image_maxWH, IN int * octaveLevel, IN float * gaussian_coefs, IN int * column_sizes,
	       IN float * fst_img, IN float * imgBlurPrev, IN int * iter, OUT float * imgBlurred);

void MERGE_gpyr(int nGpyrLayers, int imgDouble,
		int image_width /* needed only for debug */, 
		int image_height /* needed only for debug */, 
		int nOctavesDownN, int totSizeWithoutLayers, int tot_image_size, OUT float * gpyr,
		/* IN float * img, IN float * imgUp2x, IN float * imgDown2x1, IN float * imgDown2xN, */
		IN float * gpyrs, IN float * gpyrsUp2x, IN float * gpyrsDown2x1, IN float * gpyrsDown2xN);

void counterOctaveDownN(int nOctavesDownN, OUT int * iter);

void upsample2x(int image_width, int image_height,
		int parallelismLevel, int imgDouble,
		IN int * iter, IN float * img, OUT float * img2x);

void downsample2x1(int image_width, int image_height,
		   int parallelismLevel,
		   IN float * img2x, OUT float * img);

void downsample2xN(int image_width, int image_height,
		   IN float * fst_img, IN float * imgDownPrev,
		   IN int * iter, OUT float * imgDown2x);

void build_grd_rot_pyr(int nGpyrLayers, int totSizeWithoutLayers,
		       int parallelismLevel, int nLayers,
		       int image_width, int image_height, int imgDouble,
		       IN float * gpyr, OUT float * grdPyr,  OUT float * rotPyr, 
		       IN int * start_octave, IN int * stop_octave,
		       IN int * start_layer, IN int * stop_layer,
		       IN int * start_line, IN int * stop_line,
		       IN int * start_col, IN int * stop_col);

void build_dog_pyr(int nGpyrLayers, int totSizeWithoutLayers,
		   int parallelismLevel, int nDogLayers,
		   int image_width, int image_height, int imgDouble,
		   IN float * gpyr, OUT float * dogPyr, 
		   IN int * start_octave, IN int * stop_octave,
		   IN int * start_layer, IN int * stop_layer,
		   IN int * start_line, IN int * stop_line,
		   IN int * start_col, IN int * stop_col);

void detect_keypoints(int nLayers, int totSizeWithoutLayers,
		      int parallelismLevel, int nDogLayers, int nLocalKptMax,
		      int image_width, int image_height,
		      int nOctaves /* needed only for debug */, int imgDouble,
		      IN float * dogPyr, IN float * grdPyr,  IN float * rotPyr, 
		      IN int * start_octave, IN int * stop_octave,
		      IN int * start_layer, IN int * stop_layer,
		      IN int * start_line, IN int * stop_line,
		      IN int * start_col, IN int * stop_col,
		      OUT SiftKpt * keypoints/* , OUT int * nbKeypoints */);

void extract_descriptor(int nLayers, int totSizeWithoutLayers,
			int parallelismLevel, int nLocalKptMax,
			int image_width, int image_height,
			int imgDouble, int nBins, int nHistBins,
			int descrWidth, int descrHistBins,
			IN float * grdPyr,  IN float * rotPyr, 
			IN SiftKpt * keypoints_in, /* IN int * nbKeypoints_in, */
			OUT SiftKpt * keypoints_out/* , OUT int * nbKeypoints_out */);

void MERGE_keypoints(int nKeypointsMax, int parallelismLevel, int nLocalKptMax,
		     IN SiftKpt * keypoints_in, /* IN int * nbKeypoints_in, */
		     OUT SiftKpt * keypoints_out, OUT int * nbKeypoints_out);


void ITERATOR_build_dog_pyr(int parallelismLevel, int nOctaves, int nDogLayers,
			    int image_width, int image_height, int imgDouble,
			    OUT int * start_octave, OUT int * stop_octave,
			    OUT int * start_layer, OUT int * stop_layer,
			    OUT int * start_line,  OUT int * stop_line,
			    OUT int * start_col, OUT int * stop_col);

void ITERATOR_build_grd_rot_pyr(int parallelismLevel, int nOctaves, int nLayers,
				int image_width, int image_height, int imgDouble,
				OUT int * start_octave, OUT int * stop_octave,
				OUT int * start_layer, OUT int * stop_layer,
				OUT int * start_line,  OUT int * stop_line,
				OUT int * start_col, OUT int * stop_col);

void ITERATOR_detect_keypoints(int parallelismLevel, int nOctaves, int nLayers,
			       int image_width, int image_height, int imgDouble,
			       OUT int * start_octave, OUT int * stop_octave,
			       OUT int * start_layer, OUT int * stop_layer,
			       OUT int * start_line,  OUT int * stop_line,
			       OUT int * start_col, OUT int * stop_col);

void BarrierCounterGpyr(int nGpyrLayers, IN int * iters_in, OUT int * iters_out);


void SPLIT_upsample2x(int parallelismLevel, int imgDouble,
		      int image_width, int image_height,
		      int tot_image_size,
		      IN float * in, OUT float * out);

void counterPLevels(int parallelismLevel, OUT int * iter);


/* for extract */

void filename1(int FilePathLength, OUT char * filename);

void read_pgm(int FilePathLength, IN char * filename,
	      int image_width, int image_height, OUT unsigned char * img);

void draw_keypoints_to_ppm_file(int FilePathLength, int nKeypointsMax,
				int image_width, int image_height,
				int tot_image_size,
				IN int * nbKeypoints,
				IN char * filename,
				IN unsigned char * image, 
				IN struct SiftKeypoint * keypoints);


void export_keypoints_to_key_file(int FilePathLength, int nKeypointsMax,
				  int DumpDescriptor, int nBins,
				  IN char * filename,
				  IN int * nbKeypoints, 
				  IN SiftKpt * keypoints);

/* for match */


/* for internal functions */

void write_float_pgm(char* filename, float* data, unsigned char * buffer, int w, int h, int mode);



#endif
