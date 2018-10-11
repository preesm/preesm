#include <string.h>
#include <math.h>
#include <stdio.h>

#include <pthread.h>

#include "ezsift-preesm.h"

void BarrierCounterGpyr(int nGpyrLayers, IN int * iters_in, OUT int * iters_out) {
#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  memcpy(iters_out, iters_in, sizeof(int)*nGpyrLayers);
  /* fprintf(stderr, "Tab iters:"); */
  /* for(int i = 0; i < nGpyrLayers; i++) { */
  /*   fprintf(stderr, "\t%d", iters_out[i]); */
  /* } */
  /* fprintf(stderr, "\n"); */
}


/* static pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER; */
/* static int nb = -1; */


// Apply Gaussian row filter to image, then transpose the image. 
void row_filter_transpose(float * src, float * dst,
			  float * row_buf,  int w, int h,
			 float * coef1d, int gR){
  //fprintf(stderr, "Enter inner function:\t%s\n", __FUNCTION__);
  float * row_start;
  /* pthread_mutex_lock(&mutex); */
  /* nb++; */
  /* pthread_mutex_unlock(&mutex); */
  float * srcData = src;
  float * dstData = dst + w * h - 1;
  float partialSum = 0.0f;
  float * coef = coef1d;
  float * prow;

  float firstData, lastData;
  for (int r = 0; r < h; r ++){
    row_start = srcData + r * w;
    memcpy(row_buf + gR, row_start, sizeof(float) * w);
    firstData = *(row_start);
    lastData = *(row_start + w - 1);
    for (int i = 0; i < gR; i ++){
      row_buf[i] = firstData;
      row_buf[i + w + gR] = lastData;
    }

    prow = row_buf;
    dstData = dstData - w * h + 1;
    for (int c = 0; c < w; c ++){
      partialSum = 0.0f;
      coef = coef1d;

      for (int i = -gR; i <= gR; i ++){
	partialSum += (*coef ++) * (*prow ++);
      }

      prow -= 2 * gR;
      *dstData = partialSum;
      dstData += h;
    }
    /* if (nb == 0) { */
    /*   fprintf(stderr, "dstData: %u\n", dstData); */
    /*   fflush(stderr); */
    /* } */
  }
}


void counterGpyrLayer(int nGpyrLayers, OUT int * iter) {
#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  for (int i = 0; i < nGpyrLayers; i++) {
    iter[i] = i;
  }
}

void BarrierTranspose_1(int image_width, int image_height,
		      IN float * img_in, OUT float * img_out) {
#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  size_t chunk_width = SIFT_IMAGE_H/SIFT_pLevels;
  size_t image_tot_p = SIFT_IMG_TOT/SIFT_pLevels;
  float * src = img_in;
  float * dst = img_out;
  size_t offset_src = 0;
  size_t size = chunk_width*sizeof(float);
  for (int c = 0; c < SIFT_IMAGE_W; c++) {
    size_t offset_p = 0;
    size_t offset_dst = 0;
    for (int p = 0; p < SIFT_pLevels; p++) {
      memcpy(dst+offset_dst, src+offset_p+offset_src, size);
      offset_p += image_tot_p;
      offset_dst += chunk_width;
    }
    offset_src += chunk_width;
    dst += SIFT_IMAGE_H;
  }
}

void BarrierTranspose_2(int image_width, int image_height,
		      IN float * img_in, OUT float * img_out) {
#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  size_t chunk_width = SIFT_IMAGE_W/SIFT_pLevels;
  size_t image_tot_p = SIFT_IMG_TOT/SIFT_pLevels;
  float * src = img_in;
  float * dst = img_out;
  size_t offset_src = 0;
  size_t size = chunk_width*sizeof(float);
  for (int r = 0; r < SIFT_IMAGE_H; r++) {
    size_t offset_p = 0;
    size_t offset_dst = 0;
    for (int p = 0; p < SIFT_pLevels; p++) {
      memcpy(dst+offset_dst, src+offset_p+offset_src, size);
      offset_p += image_tot_p;
      offset_dst += chunk_width;
    }
    offset_src += chunk_width;
    dst += SIFT_IMAGE_W;
  }
}

void BarrierTranspose2x_1(int image_width, int image_height, int imgDouble,
			IN float * img_in, OUT float * img_out) {
#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  if (imgDouble) {
    size_t chunk_width = (2*SIFT_IMAGE_H)/SIFT_pLevels;
    size_t image_tot_p = (4*SIFT_IMG_TOT)/SIFT_pLevels;
    float * src = img_in;
    float * dst = img_out;
    size_t offset_src = 0;
    size_t size = chunk_width*sizeof(float);
    for (int c = 0; c < 2*SIFT_IMAGE_W; c++) {
      size_t offset_p = 0;
      size_t offset_dst = 0;
      for (int p = 0; p < SIFT_pLevels; p++) {
	memcpy(dst+offset_dst, src+offset_p+offset_src, size);
	offset_p += image_tot_p;
	offset_dst += chunk_width;
      }
      offset_src += chunk_width;
      dst += SIFT_IMAGE_H*2;
    }
  }
}

void BarrierTranspose2x_2(int image_width, int image_height, int imgDouble,
			IN float * img_in, OUT float * img_out) {
#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  if (imgDouble) {
    size_t chunk_width = (2*SIFT_IMAGE_W)/SIFT_pLevels;
    size_t image_tot_p = (4*SIFT_IMG_TOT)/SIFT_pLevels;
    float * src = img_in;
    float * dst = img_out;
    size_t offset_src = 0;
    size_t size = chunk_width*sizeof(float);
    for (int r = 0; r < 2*SIFT_IMAGE_H; r++) {
      size_t offset_p = 0;
      size_t offset_dst = 0;
      for (int p = 0; p < SIFT_pLevels; p++) {
	memcpy(dst+offset_dst, src+offset_p+offset_src, size);
	offset_p += image_tot_p;
	offset_dst += chunk_width;
      }
      offset_src += chunk_width;
      dst += SIFT_IMAGE_W*2;
    }
  }
}


void row_filter_transpose_1(int image_height, int image_width, int nGpyrLayers,
			    int gWmax, int parallelismLevels,
			    IN float * gaussian_coefs, IN int * column_sizes,
			    IN float * img, IN float *imgIterPrev,
			    IN int * iter, OUT float * imgGT) {
#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  int gR = column_sizes[*iter];
  float * src = *iter ? imgIterPrev : img;
  float row_buf[SIFT_IMAGE_W + SIFT_gWmax];
  row_filter_transpose(src, imgGT, row_buf, image_width, image_height/parallelismLevels, gaussian_coefs+gWmax*(*iter), gR);
}

void row_filter_transpose_2(int image_height, int image_width, int nGpyrLayers,
			    int gWmax, int parallelismLevels,
			    IN float * gaussian_coefs, IN int * column_sizes,
			    IN float * img, IN int * iter, OUT float * imgGT) {
#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  int gR = column_sizes[*iter];
  float row_buf[SIFT_IMAGE_H + SIFT_gWmax];
  row_filter_transpose(img, imgGT, row_buf, image_height, image_width/parallelismLevels, gaussian_coefs+gWmax*(*iter), gR);
}

void row_filter_transpose2x_1(int image_height, int image_width, int nGpyrLayers,
			      int gWmax, int parallelismLevels, int imgDouble,
			      IN float * gaussian_coefs, IN int * column_sizes,
			      IN float * img, IN float *imgIterPrev,
			      IN int * iter, OUT float * imgGT) {
#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  if (imgDouble) {
    int gR = column_sizes[*iter];
    float * src = *iter ? imgIterPrev : img;
    float row_buf[SIFT_IMAGE_W*2 + SIFT_gWmax];
    row_filter_transpose(src, imgGT, row_buf, 2*image_width, 2*image_height/parallelismLevels, gaussian_coefs+gWmax*(*iter), gR);
  } 
}

void row_filter_transpose2x_2(int image_height, int image_width, int nGpyrLayers,
			      int gWmax, int parallelismLevels, int imgDouble,
			      IN float * gaussian_coefs, IN int * column_sizes,
			      IN float * img, IN int * iter, OUT float * imgGT) {
#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  if (imgDouble) {
    int gR = column_sizes[*iter];
    float row_buf[SIFT_IMAGE_H*2 + SIFT_gWmax];
    row_filter_transpose(img, imgGT, row_buf, 2*image_height, 2*image_width/parallelismLevels, gaussian_coefs+gWmax*(*iter), gR);
  } 
}


void seq_blur1(int image_height, int image_width, int nGpyrLayers, int gWmax,
	       IN float * gaussian_coefs, IN int * column_sizes,
	       IN float * fst_img, IN float * imgBlurPrev, IN int * iter, OUT float * imgBlurred) {
#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  //fprintf(stderr, "*iter (seq_blur1): %d\n", *iter);
  int gR = column_sizes[*iter];
  float * src = *iter ? imgBlurPrev : fst_img;
  float tmp[SIFT_IMG_TOT/4];
  float row_buf[SIFT_IMAGE_MAX/2 + SIFT_gWmax];

  row_filter_transpose(src, tmp, row_buf, image_width/2, image_height/2, gaussian_coefs+gWmax*(*iter), gR);
  row_filter_transpose(tmp, imgBlurred, row_buf, image_height/2, image_width/2, gaussian_coefs+gWmax*(*iter), gR);

  /* char fgpyr[512]; */
  /* unsigned char buffer[SIFT_IMG_TOT/4]; */
  /* sprintf(fgpyr, "seq_blur1-%d.pgm", *iter); */
  /* write_float_pgm(fgpyr, imgBlurred, buffer, image_width/2, image_height/2, 1); */
}

void seq_blurN(int image_height, int image_width, int nGpyrLayers, int gWmax,
	       IN int * octaveLevel, IN float * gaussian_coefs, IN int * column_sizes,
	       IN float * fst_img, IN float * imgBlurPrev, IN int * iter, OUT float * imgBlurred) {
#ifdef SIFT_DEBUG  
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  /* int index = 0; */
  /* pthread_mutex_lock(&mutex); */
  /* index = nb++; */
  /* pthread_mutex_unlock(&mutex); */
  /* fprintf(stderr, "*iter (seq_blurN): %d\tindex: %d\n", *iter, index); */

  
  int gR = column_sizes[*iter];
  float * src = *iter ? imgBlurPrev : fst_img;
  int dstW = image_width >> ((*octaveLevel)+2), dstH = image_height >> ((*octaveLevel)+2);

  /* char fgpyr[512]; */
  /* unsigned char buffer[SIFT_IMG_TOT/16]; */
  /* sprintf(fgpyr, "in_seq_blurN-%d-%d.pgm", *octaveLevel, *iter); */
  /* write_float_pgm(fgpyr, src, buffer, dstW, dstH, 1); */

  float tmp[SIFT_IMG_TOT/16];
  float row_buf[SIFT_IMAGE_MAX/4 + SIFT_gWmax];

  row_filter_transpose(src, tmp, row_buf, dstW, dstH, gaussian_coefs+gWmax*(*iter), gR);
  row_filter_transpose(tmp, imgBlurred, row_buf, dstH, dstW, gaussian_coefs+gWmax*(*iter), gR);

  /* sprintf(fgpyr, "seq_blurN-%d-%d.pgm", *octaveLevel, *iter); */
  /* write_float_pgm(fgpyr, imgBlurred, buffer, dstW, dstH, 1); */
}


void MERGE_gpyr(int image_height, int image_width, int nGpyrLayers, int imgDouble,
		int nOctavesDownN, int totSizeWithoutLayers, OUT float * gpyr,
		/* IN float * img, IN float * imgUp2x, IN float * imgDown2x1, IN float * imgDown2xN, */
		IN float * gpyrs, IN float * gpyrsUp2x, IN float * gpyrsDown2x1, IN float * gpyrsDown2xN) {
#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  size_t offset = 0;
  int sizeIn = 0;
  if (imgDouble) {
    sizeIn = 4*image_width*image_height;
/* #ifdef SIFT_DEBUG */
/*     char ff2x[512]; */
/*     unsigned char buffer[SIFT_IMG_TOT*4]; */
/*     sprintf(ff2x, "up2x.pgm"); */
/*     write_float_pgm(ff2x, imgUp2x, buffer, image_width*2, image_height*2, 1); */
/* #endif */
    /* memcpy(gpyr, imgUp2x, sizeof(float)*sizeIn); */
    /* offset += sizeIn; */
    memcpy(gpyr+offset, gpyrsUp2x, sizeof(float)*sizeIn*nGpyrLayers);
    offset += sizeIn*nGpyrLayers;
  }
  sizeIn = image_width*image_height;
  /* memcpy(gpyr+offset, img, sizeof(float)*sizeIn); */
  /* offset += sizeIn; */
  memcpy(gpyr+offset, gpyrs, sizeof(float)*sizeIn*nGpyrLayers);
  offset += sizeIn*nGpyrLayers;

  sizeIn = sizeIn/4;
  /* memcpy(gpyr+offset, imgDown2x1, sizeof(float)*sizeIn); */
  /* offset += sizeIn; */
  memcpy(gpyr+offset, gpyrsDown2x1, sizeof(float)*sizeIn*nGpyrLayers);
  offset += sizeIn*nGpyrLayers;
  
  size_t sizeDownN = (image_width*image_height)/16;
  size_t offsetDownN = 0;
  sizeIn = sizeDownN;
  for (int i = 0; i < nOctavesDownN; i++) {
    /* memcpy(gpyr+offset, imgDown2xN+(sizeDownN*i), sizeof(float)*sizeIn); */
    /* offset += sizeIn; */
    for (int j = 0; j < nGpyrLayers; j++) {
      memcpy(gpyr+offset, gpyrsDown2xN+offsetDownN, sizeof(float)*sizeIn);
      offset += sizeIn;
      offsetDownN += sizeDownN;
    }
    sizeIn = sizeIn/4;
  }
  
/* #ifdef SIFT_DEBUG */
/*   char fgpyr[512]; */
/*   int h = image_height; */
/*   int w = image_width; */
/*   offset = 0; */
/*   sizeIn = h*w; */
/*   if (imgDouble) { */
/*     sizeIn *= 4; */
/*     h *= 2; w *= 2; */
/*   } */
/* #if SIFT_IMG_DBL */
/*   unsigned char buffer[SIFT_IMG_TOT*4]; */
/* #else */
/*   unsigned char buffer[SIFT_IMG_TOT]; */
/* #endif */
/*   for (int i = 0; i < SIFT_nOctaves; i ++) { */
/*       for (int j = 0; j < nGpyrLayers; j ++) { */
/* 	  sprintf(fgpyr, "gpyr-%d-%d.pgm", i, j); */
/* 	  write_float_pgm(fgpyr, gpyr+offset, buffer, w, h, 1); */
/* 	  offset += sizeIn; */
/*       } */
/*       sizeIn /= 4; */
/*       h /= 2; w /= 2; */
/*    } */
/* #endif */
  
}


// For build_gaussian_pyramid()
void compute_gaussian_coefs(int gWmax, int nGpyrLayers,
			    OUT int * column_sizes,
			    OUT float * gaussian_coefs){
#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  // Compute all sigmas for different layers
  int nLayers = SIFT_nLayers;
  float sigma, sigma_pre;
  float sigma0 = SIFT_SIGMA;
  float k = powf(2.0f, 1.0f / nLayers);

  float sig[SIFT_nGpyrLayers];
  
  // for whatever reason we skip the first level,
  // so we do one more layer
  sigma_pre = SIFT_IMG_DBL? 2.0f * SIFT_INIT_SIGMA : SIFT_INIT_SIGMA;
  sig[0]  = sqrtf(sigma0 * sigma0 - sigma_pre * sigma_pre);
  for (int i = 1; i < nGpyrLayers; i ++) {
    sigma_pre = powf(k, (float) (i - 1)) * sigma0;
    sigma = sigma_pre * k;
    sig[i] = sqrtf(sigma * sigma - sigma_pre * sigma_pre);
  }

  for (int i = 0; i < nGpyrLayers;  i ++) {
    // Compute Gaussian filter coefficients
    float factor = SIFT_GAUSSIAN_FILTER_RADIUS;
    int gR = (sig[i] * factor > 1.0f)? (int) ceilf(sig[i] * factor): 1;
    column_sizes[i] = gR;
    int gW = gR * 2 + 1;
    float accu = 0.0f;
    float tmp;
    for(int j = 0; j < gW; j ++) {
      tmp = (float)((j - gR) / sig[i]);
      int index = i*SIFT_gWmax+j;
      gaussian_coefs[index] = expf( tmp * tmp * -0.5f ) * (1 + j/1000.0f);
      accu += gaussian_coefs[index];
    }
    for(int j = 0; j < gW; j ++) {
      int index = i*SIFT_gWmax+j;
      gaussian_coefs[index] = gaussian_coefs[index] / accu;
    } // End compute Gaussian filter coefs
  }
}
