#include <stdbool.h>
#include <float.h>
#include <stdio.h>

#include "ezsift-common.h"
#include "vvector.h"
#include "ordered_chained_list.h"


void ITERATOR_detect_keypoints(int parallelismLevel, int nOctaves, int nLayers,
			       int image_width, int image_height, int imgDouble,
			       OUT int * start_octave, OUT int * stop_octave,
			       OUT int * start_layer, OUT int * stop_layer,
			       OUT int * start_line,  OUT int * stop_line,
			       OUT int * start_col, OUT int * stop_col) {
  
  
#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  ITERATOR_generic(parallelismLevel, nOctaves, nLayers,
		   image_width, image_height, imgDouble,
		   start_octave, stop_octave,
		   start_layer, stop_layer,
		   start_line, stop_line,
		   start_col, stop_col);  
}





// Compute orientation histogram for keypoint detection.
// using pre-computed gradient information.
float compute_orientation_hist_with_gradient(int w, int h,
					     float * grdImage, 
					     float * rotImage, 
					     struct SiftKeypoint * kpt,
					     float * hist) {

  float kptr = kpt->ri;
  float kptc = kpt->ci;
  float kpt_scale = kpt->layer_scale;

  int kptr_i = (int) (kptr + 0.5f);
  int kptc_i = (int) (kptc + 0.5f);
  float d_kptr = kptr - kptr_i;
  float d_kptc = kptc - kptc_i;

  float sigma = SIFT_ORI_SIG_FCTR * kpt_scale;
  int win_radius = (int) (SIFT_ORI_RADIUS * kpt_scale);
  float exp_factor = -1.0f / (2.0f * sigma * sigma);

  size_t r, c, rw;
  float magni, angle, weight;
  int bin;
  float fbin; // float point bin

  float tmpHist[SIFT_ORI_HIST_BINS];
  memset(tmpHist, 0, SIFT_ORI_HIST_BINS * sizeof(float));
	
  for (int i = -win_radius; i <= win_radius; i ++) { // rows
    r = kptr_i + i;
    if (r <= 0 || r >= h-1) // Cannot calculate dy
      continue;

    rw = r * w;
    for (int j = -win_radius; j <= win_radius; j ++) { // columns
      c = kptc_i + j;
      if (c <= 0 || c >= w-1)
	continue;

      magni = grdImage[rw + c];
      angle = rotImage[rw + c];

      fbin = angle * SIFT_ORI_HIST_BINS / _2PI;
      weight = expf(((i-d_kptr) * (i-d_kptr) + (j-d_kptc) * (j-d_kptc)) * exp_factor);

#if SIFT_ORI_BILINEAR
      bin = (int) (fbin - 0.5f);
      float d_fbin = fbin - 0.5f - bin ;

      float mw = weight * magni;
      float dmw = d_fbin * mw;
      tmpHist[(bin + SIFT_ORI_HIST_BINS) % SIFT_ORI_HIST_BINS] += mw - dmw; 
      tmpHist[(bin + 1) % SIFT_ORI_HIST_BINS] += dmw;
#else
      bin = (int) (fbin);
      tmpHist[bin] += magni * weight;
#endif
    }
  }

#define TMPHIST(idx) (idx < 0? tmpHist[0] : (idx >= SIFT_ORI_HIST_BINS ? tmpHist[SIFT_ORI_HIST_BINS - 1] : tmpHist[idx]))

#if SIFT_USE_SMOOTH1

  // Smooth the histogram. Algorithm comes from OpenCV.
  hist[0] = (tmpHist[0] + tmpHist[2]) * 1.0f / 16.0f +
    (tmpHist[0] + tmpHist[1]) * 4.0f / 16.0f +
    tmpHist[0] * 6.0f / 16.0f;
  hist[1] = (tmpHist[0] + tmpHist[3]) * 1.0f / 16.0f +
    (tmpHist[0] + tmpHist[2]) * 4.0f / 16.0f +
    tmpHist[1] * 6.0f / 16.0f;
  hist[SIFT_ORI_HIST_BINS - 2] = (tmpHist[SIFT_ORI_HIST_BINS - 4] + tmpHist[SIFT_ORI_HIST_BINS - 1]) * 1.0f / 16.0f +
    (tmpHist[SIFT_ORI_HIST_BINS - 3] + tmpHist[SIFT_ORI_HIST_BINS - 1]) * 4.0f / 16.0f +
    tmpHist[SIFT_ORI_HIST_BINS - 2] * 6.0f / 16.0f;
  hist[SIFT_ORI_HIST_BINS - 1] = (tmpHist[SIFT_ORI_HIST_BINS - 3] + tmpHist[SIFT_ORI_HIST_BINS - 1]) * 1.0f / 16.0f +
    (tmpHist[SIFT_ORI_HIST_BINS - 2] + tmpHist[SIFT_ORI_HIST_BINS - 1]) * 4.0f / 16.0f +
    tmpHist[SIFT_ORI_HIST_BINS - 1] * 6.0f / 16.0f;

  for(int i = 2; i < SIFT_ORI_HIST_BINS - 2; i ++){
    hist[i] = (tmpHist[i - 2] + tmpHist[i + 2]) * 1.0f / 16.0f +
      (tmpHist[i - 1] + tmpHist[i + 1]) * 4.0f / 16.0f +
      tmpHist[i] * 6.0f / 16.0f;
  }

#else
  // Yet another smooth function
  // Algorithm comes from the vl_feat implementation.
  for (int iter = 0; iter < 6; iter ++){
    float prev = TMPHIST(SIFT_ORI_HIST_BINS - 1);
    float first = TMPHIST(0);
    int i;
    for (i = 0; i < SIFT_ORI_HIST_BINS - 1; i ++){
      float newh = (prev + TMPHIST(i) + TMPHIST(i + 1)) / 3.0f ;
      prev = hist[i];
      hist[i] = newh;
    }
    hist[i] = (prev + hist[i] + first) / 3.0f;
  }
#endif

  // Find the maximum item of the histogram
  float maxitem = hist[0];
  int max_i = 0;
  for (int i = 0; i < SIFT_ORI_HIST_BINS; i ++){
    if (maxitem < hist[i]){
      maxitem = hist[i];
      max_i = i;
    }
  }

  kpt->ori = max_i * _2PI / SIFT_ORI_HIST_BINS;

  return maxitem;
}

// Refine local keypoint extrema.
bool refine_local_extrema(float * dogPyr,
			  int w, int h,
			  int nDogLayers,
			  int imgDouble,
			  struct SiftKeypoint * kpt){

  int octave = kpt->octave;
  int layer = kpt->layer;
  int r = (int)kpt->ri;
  int c = (int)kpt->ci;

  float * currData = dogPyr;
  size_t tot_size = w*h;
  float * lowData = dogPyr - tot_size;
  float * highData = dogPyr + tot_size;

  int xs_i = 0, xr_i = 0, xc_i = 0;
  float tmp_r, tmp_c, tmp_layer;
  float xr = 0.0f, xc = 0.0f, xs = 0.0f;
  float x_hat[3] = {xc, xr, xs};
  float dx, dy, ds;
  float dxx, dyy, dss, dxs, dys, dxy;

  tmp_r = (float)r;
  tmp_c = (float)c;
  tmp_layer = (float)layer;

  // Interpolation (x,y,sigma) 3D space to find sub-pixel accurate 
  // location of keypoints.
  int i = 0;
  for (; i < SIFT_MAX_INTERP_STEPS; i ++){
    c += xc_i;
    r += xr_i;

    dx = (get_pixel_f(currData, w, h, r, c + 1) - get_pixel_f(currData, w, h, r, c - 1)) * 0.5f;
    dy = (get_pixel_f(currData, w, h, r + 1, c) - get_pixel_f(currData, w, h, r - 1, c)) * 0.5f;
    ds = (get_pixel_f(highData, w, h, r, c) - get_pixel_f(lowData, w, h, r, c)) * 0.5f;
    float dD[3]={-dx, -dy, -ds};

    float v2 = 2.0f * get_pixel_f(currData, w, h, r, c);
    dxx = (get_pixel_f(currData, w, h, r, c + 1) + get_pixel_f(currData, w, h, r, c - 1) - v2);
    dyy = (get_pixel_f(currData, w, h, r + 1, c) + get_pixel_f(currData, w, h, r - 1, c) - v2);
    dss = (get_pixel_f(highData, w, h, r, c) + get_pixel_f(lowData, w, h, r, c) - v2);
    dxy = (get_pixel_f(currData, w, h, r + 1, c + 1) - get_pixel_f(currData, w, h, r + 1, c - 1) - 
	   get_pixel_f(currData, w, h, r - 1, c + 1) + get_pixel_f(currData, w, h, r - 1, c - 1)) * 0.25f;
    dxs = (get_pixel_f(highData, w, h, r, c + 1) - get_pixel_f(highData, w, h, r, c - 1) - 
	   get_pixel_f(lowData, w, h, r, c + 1) + get_pixel_f(lowData, w, h, r, c - 1)) * 0.25f;
    dys = (get_pixel_f(highData, w, h, r + 1, c) - get_pixel_f(highData, w, h, r - 1, c) - 
	   get_pixel_f(lowData, w, h, r + 1, c) + get_pixel_f(lowData, w, h, r - 1, c)) * 0.25f;

    // The scale in two sides of the equation should cancel each other.
    float H[3][3] = {{dxx, dxy, dxs},{dxy, dyy, dys},{dxs, dys, dss}};
    float Hinvert[3][3]; 
    float det;

    // Matrix inversion
    // INVERT_3X3 = DETERMINANT_3X3, then SCALE_ADJOINT_3X3;
    // Using INVERT_3X3(Hinvert, det, H) is more convenient;
    // but using separate ones, we can check det==0 easily.
    float tmp;                  
    DETERMINANT_3X3 (det, H); 
    if (fabsf(det) < FLT_MIN)
      break;
    tmp = 1.0f / (det);           
    //INVERT_3X3(Hinvert, det, H); 
    SCALE_ADJOINT_3X3 (Hinvert, tmp, H);       
    MAT_DOT_VEC_3X3(x_hat, Hinvert, dD);

    xs = x_hat[2];
    xr = x_hat[1];
    xc = x_hat[0];

    // Update tmp data for keypoint update.
    tmp_r = r + xr;
    tmp_c = c + xc;
    tmp_layer = layer + xs;

    // Make sure there is room to move for next iteration.
    xc_i= ((xc >=  SIFT_KEYPOINT_SUBPiXEL_THR && c < w - 2) ?  1 : 0)
      + ((xc <= -SIFT_KEYPOINT_SUBPiXEL_THR && c > 1    ) ? -1 : 0) ;

    xr_i= ((xr >=  SIFT_KEYPOINT_SUBPiXEL_THR && r < h - 2) ?  1 : 0)
      + ((xr <= -SIFT_KEYPOINT_SUBPiXEL_THR && r > 1    ) ? -1 : 0) ;

    if (xc_i == 0 && xr_i == 0 && xs_i == 0)
      break;
  }

  // We MIGHT be able to remove the following two checking conditions.
  // Condition 1
  if (i >= SIFT_MAX_INTERP_STEPS)
    return false;
  // Condition 2.
  if (fabsf(xc) >= 1.5 || fabsf(xr) >= 1.5 || fabsf(xs) >= 1.5)
    return false;

  // If (r, c, layer) is out of range, return false.
  if (tmp_layer < 0 || tmp_layer > nDogLayers
      || tmp_r < 0 || tmp_r > h - 1 
      || tmp_c < 0 || tmp_c > w - 1)
    return false;

  float value = get_pixel_f(currData, w, h, r, c) + 0.5f * (dx * xc + dy * xr + ds * xs);
  if (fabsf(value) < SIFT_CONTR_THR)
    return false;

  float trH = dxx +  dyy;
  float detH = dxx * dyy - dxy * dxy;
  float response = (SIFT_CURV_THR + 1) * (SIFT_CURV_THR + 1) / (SIFT_CURV_THR);

  if(detH <= 0 || (trH * trH / detH) >= response)
    return false;

  // Coordinates in the current layer.
  kpt->ci = tmp_c;
  kpt->ri = tmp_r;
  kpt->layer_scale = SIFT_SIGMA * powf(2.0f, tmp_layer/SIFT_INTVLS);

  float norm = powf(2.0f, (float) (octave - imgDouble));
  // Coordinates in the normalized format (compared to the original image).
  kpt->c = tmp_c * norm;
  kpt->r = tmp_r * norm;
  kpt->rlayer = tmp_layer;
  kpt->layer = layer;

  // Formula: Scale = sigma0 * 2^octave * 2^(layer/S);
  kpt->scale = kpt->layer_scale * norm;

  return true;
}


int compareKeypointThreshold(struct SiftKeypoint * kpt_in_list, struct SiftKeypoint * kpt_to_add) {
  if (kpt_to_add->mag < kpt_in_list->mag) {
    return -1;
  } else if (kpt_to_add->mag > kpt_in_list->mag) {
    return 1;
  }
  return 0;
}


// Keypoint detection.
void detect_keypoints(int nLayers, int totSizeWithoutLayers,
		      int parallelismLevel, int nDogLayers,int nLocalKptMax,
		      int image_width, int image_height,
		      int nOctaves /* needed only for debug */, int imgDouble,
		      IN float * dogPyr, IN float * grdPyr,  IN float * rotPyr, 
		      IN int * start_octave, IN int * stop_octave,
		      IN int * start_layer, IN int * stop_layer,
		      IN int * start_line, IN int * stop_line,
		      IN int * start_col, IN int * stop_col,
		      OUT struct SiftKeypoint * keypoints/* , OUT int * nbKeypoints */) {

#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif

/* #ifdef SIFT_DEBUG */
/*   if (*start_octave == 0 && *start_layer == 0 && *start_line == 0 && *start_col == 0) { */
/*     char fd[512]; */
/*     int w = image_width; */
/*     int h = image_height; */
/*     size_t offset = 0; */
/*     size_t sizeIn = w*h; */
/*     if (imgDouble) { */
/*       sizeIn *= 4; */
/*       h *= 2; w *= 2; */
/*     } */
/*     unsigned char buffer[sizeIn]; */
/*     for (int i = 0; i < nOctaves; i ++) { */
/*       for (int j = 0; j < nDogLayers; j ++) { */
/*       	sprintf(fd, "dog-%d-%d.pgm", i, j); */
/*       	write_float_pgm(fd, dogPyr+offset, buffer, w, h, 2); */
/*       /\* for (int j = 0; j < nLayers; j ++) { *\/ */
/*       /\* 	sprintf(fd, "mag-%d-%d.pgm", i, j); *\/ */
/*       /\* 	write_float_pgm(fd, grdPyr+offset, buffer, w, h, 2); *\/ */
/*       /\* for (int j = 0; j < nLayers; j ++) { *\/ */
/*       /\* 	sprintf(fd, "ang-%d-%d.pgm", i, j+1); *\/ */
/*       /\* 	write_float_pgm(fd, rotPyr+offset, buffer, w, h, 2); *\/ */
/*       	offset += sizeIn; */
/*       } */
/*       sizeIn >>= 2; */
/*       h >>= 1; w >>= 1; */
/*     } */
/*   } */
/* #endif */
  
  struct OrderedKptList kpt_list;
  struct ElementOrdList elts[nLocalKptMax];
  initMemKpt(&kpt_list, nLocalKptMax, elts, keypoints);
  
  size_t offset_dogPyr = 0;
  size_t offset_gpyr = 0;
  size_t w = image_width;
  size_t h = image_height;
  if (imgDouble) {
    w *= 2;
    h *= 2;
  }
  size_t tot_size = w*h;
  for (int k = 0; k < *start_octave; k++) {
    offset_dogPyr +=  tot_size*nDogLayers;
    offset_gpyr += tot_size*nLayers;
    w >>= 1;
    h >>= 1;
    tot_size >>= 2;
  }

  offset_gpyr += tot_size*(*start_layer);

  struct SiftKeypoint kpt;

  float hist[SIFT_ORI_HIST_BINS];

  // Some paper uses other thresholds, for example 3.0f for all cases
  // In Lowe's paper, |D(x)|<0.03 will be rejected.
  float contr_thr = 0.8f * SIFT_CONTR_THR;


  /* int nb1 = 0; */
  /* int nb2 = 0; */
  /* fprintf(stderr, "%d-%d\t%d-%d\t%d-%d\t%d-%d\n", *start_octave, *stop_octave, *start_layer, *stop_layer, *start_line, *stop_line, *start_col, *stop_col); */
  for (int i = *start_octave; i < *stop_octave; i ++) {

    float * lowData = dogPyr + offset_dogPyr;
    float * currData = lowData + tot_size;
    float * highData = currData + tot_size;

    if (i == *start_octave) {
      size_t layer_offset = tot_size*(*start_layer);
      currData += layer_offset;
      highData += layer_offset;
      lowData += layer_offset;
    }
    
    int begin_layer = (i == *start_octave) ? (*start_layer) + 1 : 1;
    int end_layer = (i+1 == *stop_octave) ? (*stop_layer) + 1 : nLayers+1;
    for (int j = begin_layer; j < end_layer; j ++) {

      /* fprintf(stderr, "dog = %lu\toctave: %d\tlayer: %d\n", (unsigned long) currData, i, j); */

      
      int begin_line = (i == *start_octave) && (j-1 == *start_layer) ? *start_line : 0;
      int end_line = (i+1 == *stop_octave) && (j == *stop_layer) ? *stop_line : h;
      for (int r = tmax_i(SIFT_IMG_BORDER, begin_line); r < tmin_i(end_line, h - SIFT_IMG_BORDER); r ++) {

	int begin_col = (i == *start_octave) && (j-1 == *start_layer) && (r == *start_line) ? *start_col : 0;
	int end_col = (i+1 == *stop_octave) && (j == *stop_layer) && (r+1 == *stop_line) ? *stop_col : w;

	size_t indexTmp = r*w;
	for (int c = tmax_i(SIFT_IMG_BORDER, begin_col); c < tmin_i(end_col, w - SIFT_IMG_BORDER); c ++) {
	  size_t index = indexTmp + c;
	  float val = currData[index];

	  bool bExtrema = 
	    (val >= contr_thr &&
	     val > highData[index - w - 1] && val > highData[index - w] && val > highData[index - w + 1] && 
	     val > highData[index - 1] && val > highData[index] && val > highData[index + 1] &&
	     val > highData[index + w - 1] && val > highData[index + w ] && val > highData[index  + w + 1] &&
	     val > currData[index - w - 1] && val > currData[index - w] && val > currData[index - w + 1] && 
	     val > currData[index - 1]									&& val > currData[index + 1] &&
	     val > currData[index + w - 1] && val > currData[index + w ] && val > currData[index  + w + 1] &&
	     val > lowData[index - w - 1] && val > lowData[index - w] && val > lowData[index - w + 1] && 
	     val > lowData[index - 1] && val > lowData[index] && val > lowData[index + 1] &&
	     val > lowData[index + w - 1] && val > lowData[index + w ] && val > lowData[index  + w + 1])
	    || // Local min
	    (
	     val <= -contr_thr &&
	     val < highData[index - w - 1] && val < highData[index - w] && val < highData[index - w + 1] && 
	     val < highData[index - 1] && val < highData[index] && val < highData[index + 1] &&
	     val < highData[index + w - 1] && val < highData[index + w ] && val < highData[index  + w + 1] &&
	     val < currData[index - w - 1] && val < currData[index - w] && val < currData[index - w + 1] && 
	     val < currData[index - 1]									&& val < currData[index + 1] &&
	     val < currData[index + w - 1] && val < currData[index + w ] && val < currData[index  + w + 1] &&
	     val < lowData[index - w - 1] && val < lowData[index - w] && val < lowData[index - w + 1] && 
	     val < lowData[index - 1] && val < lowData[index] && val < lowData[index + 1] &&
	     val < lowData[index + w - 1] && val < lowData[index + w ] && val < lowData[index  + w + 1]);					
	  
	  if (bExtrema) {
	    /* nb1++; */
	    kpt.octave = i;
	    kpt.layer = j;
	    kpt.ri = (float) r;
	    kpt.ci = (float) c;

	    bool bGoodKeypoint = refine_local_extrema(currData, w, h, nDogLayers, imgDouble, &kpt);

	    if(!bGoodKeypoint)
	      continue;
	    /* nb2++; */
	    
	    float max_mag = compute_orientation_hist_with_gradient(w, h,
								   grdPyr + offset_gpyr,
								   rotPyr + offset_gpyr,
								   &kpt, hist);

	    float threshold = max_mag * SIFT_ORI_PEAK_RATIO;

	    for (int ii = 0; ii < SIFT_ORI_HIST_BINS; ii ++){
#if (SIFT_INTERP_ORI_HIST == 0)
	      if(hist[ii] >= threshold){
		kpt.mag = hist[ii];
		kpt.ori = ii * _2PI / SIFT_ORI_HIST_BINS;
		addElementKpt(&kpt_list, &kpt, &compareKeypointThreshold);
		//addElementUnordered(&kpt_list, &kpt);
	      }
#else
	      // Use 3 points to fit a curve and find the accurate location of a keypoints
	      int left = ii  > 0 ? ii - 1 : SIFT_ORI_HIST_BINS - 1;
	      int right = ii < (SIFT_ORI_HIST_BINS-1) ? ii + 1 : 0;
	      float currHist = hist[ii];
	      float lhist = hist[left];
	      float rhist = hist[right];
	      if (currHist > lhist && currHist > rhist && currHist > threshold){
		// Refer to here: http://stackoverflow.com/questions/717762/how-to-calculate-the-vertex-of-a-parabola-given-three-points
		float accu_ii = ii + 0.5f * (lhist - rhist) / (lhist - 2.0f*currHist + rhist);

		// Since bin index means the starting point of a bin, so the real orientation should be bin index
		// plus 0.5. for example, angles in bin 0 should have a mean value of 5 instead of 0;
		accu_ii += 0.5f;
		accu_ii = accu_ii<0 ? (accu_ii + SIFT_ORI_HIST_BINS) : accu_ii>=SIFT_ORI_HIST_BINS ? (accu_ii - SIFT_ORI_HIST_BINS) : accu_ii;
		// The magnitude should also calculate the max number based on fitting
		// But since we didn't actually use it in image matching, we just lazily
		// use the histogram value.
		kpt.mag = currHist;
		kpt.ori = accu_ii * _2PI / SIFT_ORI_HIST_BINS;
		addElementKpt(&kpt_list, &kpt, &compareKeypointThreshold);
		//addElementUnordered(&kpt_list, &kpt);
	      }		
#endif
	    }			
	  }
	}
      }
      currData += tot_size;
      highData += tot_size;
      lowData += tot_size;
      offset_gpyr += tot_size;
    }
    offset_dogPyr += tot_size*nDogLayers;
    w >>= 1;
    h >>= 1;
    tot_size >>= 2;
  }

  //  *nbKeypoints = getSizeKpt(&kpt_list);
  // last keypoint is void and is used to store the number of keypoints detected in the octave attribute
  keypoints[nLocalKptMax].octave = getSizeKpt(&kpt_list);
  
  /* fprintf(stderr, "keypoints detected: %d\n", keypoints[nLocalKptMax].octave); */

  /* fprintf(stderr, "nb1: %d\tnb2: %d\n", nb1, nb2); */
  /* for (int i = 0; i < getSizeKpt(&kpt_list); i++) { */
  /*   fprintf(stderr, "(all) %d: %d\t%d\t%f\t%f\n", i, keypoints[i].octave, keypoints[i].layer, keypoints[i].ri, keypoints[i].ci); */
  /* } */
  /* fprintf(stderr, "\n"); */

  
  
}

