#include <stdio.h>
#include <string.h>
#include <pthread.h>
#include <math.h>

#include "ezsift-preesm.h"


/* static pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER; */
/* static int nb = -1; */


void SPLIT_upsample2x(int parallelismLevel, int imgDouble,
		      int image_width, int image_height,
		      int tot_image_size,
		      IN float * in, OUT float * out) {
#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif

  if (imgDouble) {
    if (out != NULL) {
      size_t rChunckSize = tot_image_size / parallelismLevel; 
      size_t chunckSize = rChunckSize + image_width;
      size_t offset_in = 0;
      size_t offset_out = 0;
      for (int p = 0; p < parallelismLevel - 1; p++) {
	memcpy(out+offset_out, in+offset_in, sizeof(float)*chunckSize);
	offset_in += rChunckSize;
	offset_out += chunckSize;
      }
      memcpy(out+offset_out, in+offset_in, sizeof(float)*rChunckSize);
      // useless with ne version of upsample
      /* offset_in += rChunckSize - image_width; */
      /* offset_out += rChunckSize; */
      /* memcpy(out+offset_out, in+offset_in, sizeof(float)*image_width);     */
    }
    // useless with ne version of upsample
    /* else { */
    /*   memcpy(in + image_width*image_height, in + image_width*(image_height - 1), sizeof(float)*image_width); */
    /* } */
  }

/* #ifdef SIFT_DEBUG */
/*   char ff[512]; */
/*   unsigned char buffer[tot_image_size]; */
/*   sprintf(ff, "tofloat.pgm"); */
/*   write_float_pgm(ff, in, buffer, image_width, image_height, 1); */
/* #endif */
}


void to_uchar(int w, int h, float * img_in, unsigned char * img_out) {
  float * src = img_in;
  unsigned char * dst = img_out;
  for (int r = 0; r < h; r ++) {
    for(int c = 0; c < w; c ++) {
      // Negative number, truncate to zero.
      float tmp = *src;
      *(dst++) = tmp >= 0 ? (unsigned char) tmp : 0;
      src++;
    }
  }
}


void to_float(int parallelismLevel, int image_width, int image_height,
	      IN unsigned char * uchar_img, OUT float * float_img) {
#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  unsigned char * src = uchar_img;
  float * dst = float_img;
  for (int i = 0; i < image_height/parallelismLevel; i ++) {
    for (int j = 0; j < image_width; j ++) {
      *(dst++) = *(src++);
    }
  }
}

// Upsample the image by 2x, linear interpolation.
void upsample2x(int image_width, int image_height,
		int parallelismLevel, int imgDouble,
		IN int * iter, IN float * img, OUT float * img2x) {
#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  if (imgDouble) {
    float scale = 2.0f;
    int dstW = image_width << 1, dstH = image_height << 1;

    /* #ifdef SIFT_DEBUG */
    /*     char fgpyr[512]; */
    /*     int h = (image_height*2)/parallelismLevel; */
    /*     int w = image_width*2; */
    /*     unsigned char buffer[(image_width*image_height*4)/parallelismLevel]; */
    /*     sprintf(fgpyr, "origin_%d.pgm", index); */
    /*     write_float_pgm(fgpyr, img, buffer, w/2, h/2, 1); */
    /* #endif */
    int chunckSize = dstH/parallelismLevel;
    int end_line = (*iter == parallelismLevel-1) ? chunckSize - ceilf(scale) : chunckSize;
    int end_col = dstW - ceilf(scale);	
    for (int r = 0; r < end_line; r ++) {
      float ori_r = r / scale;
      int r1 = (int) ori_r;
      float dr = ori_r - r1;
      size_t rdstW = r*dstW;
      size_t r1imgW = r1 * image_width;
      for (int c = 0; c < end_col ; c ++) {
	float ori_c = c / scale;
	int c1 = (int) ori_c;
	float dc = ori_c - c1;

	int idx = r1imgW + c1;
	img2x[rdstW + c] = /* (unsigned char) */ ((1-dr) * (1-dc) * img[idx]
						       + dr * (1-dc) * (r1 < image_height - 1 ? img[idx + image_width] : img[idx])
						       + (1-dr)* dc * img[idx + 1]
						       + dr * dc * ((r1 < image_height - 1) ? img[idx + image_width + 1] : img[idx]));
      }
      for (int c = end_col; c < dstW ; c ++) {
			
	float ori_c = c / scale;
	int c1 = (int) ori_c;
	float dc = ori_c - c1;
	int idx = r1imgW + c1;
	img2x[rdstW + c] = /* (unsigned char) */ ((1-dr) * (1-dc) * img[idx]
						       + dr * (1-dc) * (r1 < image_height - 1 ? img[idx + image_width] : img[idx])
						       + (1-dr)* dc * img[idx]
						       + dr * dc * img[idx]);
      }
    }
    if (*iter == parallelismLevel-1) {
      for (int r = end_line; r < end_line+ceilf(scale); r ++) {
	float ori_r = r / scale;
	int r1 = (int) ori_r;
	float dr = ori_r - r1;
	size_t rdstW = r*dstW;
	size_t r1imgW = r1 * image_width;
	for (int c = 0; c < end_col ; c ++) {
	  float ori_c = c / scale;
	  int c1 = (int) ori_c;
	  float dc = ori_c - c1;

	  int idx = r1imgW + c1;
	  img2x[rdstW + c] = /* (unsigned char) */ ((1-dr) * (1-dc) * img[idx]
							 + dr * (1-dc) * img[idx]
							 + (1-dr)* dc * img[idx + 1]
							 + dr * dc * img[idx]);
	}
	for (int c = end_col; c < dstW; c ++) {
			
	  float ori_c = c / scale;
	  int c1 = (int) ori_c;
	  int idx = r1imgW + c1;
	  img2x[rdstW + c] = /* (unsigned char) */ img[idx];
	}
      }
    }
    /* #ifdef SIFT_DEBUG */
    /*   sprintf(fgpyr, "upsample_%d.pgm", index); */
    /*   write_float_pgm(fgpyr, img2x, buffer, w, h, 1); */
    /* #endif */
  }

}


// Downsample the image by 2x, nearest neighbor interpolation.
void downsample2x1(int image_width, int image_height,
		   int parallelismLevel,
		   IN float * img2x, OUT float * img) {
#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  int dstW = image_width >> 1, dstH = image_height >> 1;
  float * dst = img;
  for (int r = 0; r < dstH/parallelismLevel; r ++)	{
    int ori_r = r << 1;
    size_t indexTmp = ori_r * image_width;
    for (int c = 0; c < dstW; c ++) {
      int ori_c = c << 1;
      *(dst++) = img2x[indexTmp + ori_c];
    }
  }
}

// Downsample the image by 2x, nearest neighbor interpolation.
void downsample2xN(int image_width, int image_height,
		   IN float * fst_img, IN float * imgDownPrev,
		   IN int * iter, OUT float * imgDown2x) {

#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  int srcW = image_width >> ((*iter)+1);
  int dstW = image_width >> ((*iter)+2);
  int dstH = image_height >> ((*iter)+2);

  float * src = (*iter) ? imgDownPrev : fst_img;
  float * dst = imgDown2x;
  for (int r = 0; r < dstH; r ++)	{
    int ori_r = r << 1;
    size_t indexTmp = ori_r * srcW;
    for (int c = 0; c < dstW; c ++) {
      int ori_c = c << 1;
      *(dst++) = src[indexTmp + ori_c];
    }
  }
}
