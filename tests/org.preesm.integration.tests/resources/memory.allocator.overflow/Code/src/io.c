/*	Copyright (c) 2013, Robert Wang, email: robertwgh (at) gmail.com
	All rights reserved. https://sourceforge.net/p/ezsift

	Part of "img_io.cpp" code referred to David Lowe's code
	and code from here https://sites.google.com/site/5kk73gpu2011/.

	Revision history:
	September, 15, 2013: initial version.
*/


#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>

#include "ezsift-common.h"
#include "vvector.h"


void write_data2ppm(const char* filename, unsigned char* data, int w, int h) {
  FILE* fp;
  if ((fp = fopen(filename, "wb")) == NULL) {
    fprintf(stderr, "Cannot write to file %s\n", filename);
    return;
  }
  //printf("Saving %s...", aFilename);
  /* Write header */
  fprintf(fp, "P6\n");
  fprintf(fp, "%d %d\n", w, h);
  fprintf(fp, "255\n");

  fwrite(data, sizeof(unsigned char), w*h*3, fp);
  fclose(fp);
}

void write_rgb2ppm(const char * filename, unsigned char* r, unsigned char* g, unsigned char* b, int w, int h, int tot_image_size) {
  unsigned char obuf[3*tot_image_size];
  unsigned char * dst = obuf;
  for(int i = 0; i < tot_image_size; i++){
    *(dst++) = r[i];
    *(dst++) = g[i];
    *(dst++) = b[i];
  }

  write_data2ppm(filename, obuf, w, h);
}

void setPixelRed(unsigned char* img_r, unsigned char* img_g, unsigned char* img_b, int w, int h, int r, int c) {
  size_t index = r*w + c;
  if( (r >= 0) && (r < h) && (c >= 0) && (c < w)){
    img_r[index] = 0;
    img_g[index] = 0;
    img_b[index] = 255;
  }
}


void export_kpt_list_to_file(const char * filename, int nb_kpt, 
			     struct SiftKeypoint * kpt_list,
			     int bIncludeDescriptor, int nBins) {
  FILE * fp;
  fp = fopen(filename, "wb");
  if (! fp){
    printf("Fail to open file: %s\n", filename);
    //exit(1);
  }

  fprintf(fp, "%d\t%d\n", nb_kpt, nBins);

  // initialize buffer with max mem: max chars taken by %d and %.2f
  // compute length a posteriori with str_len on buffer+current_tot_length
  
  if (bIncludeDescriptor) {
    for (size_t index = 0; index < nb_kpt; ++index) {
      struct SiftKeypoint * kpt = kpt_list + index;
      fprintf(fp, "%d\t%d\t%.2f\t%.2f\t%.2f\t%.2f\t", kpt->octave, kpt->layer, kpt->r, kpt->c, kpt->scale, kpt->ori);
      for (int i = 0; i < nBins; i ++){
	fprintf(fp, "%d\t", (int)(kpt->descriptors[i]));
      }
      fprintf(fp, "\n");
    }
  } else {
    for (size_t index = 0; index < nb_kpt; ++index) {
      struct SiftKeypoint * kpt = kpt_list + index;
      fprintf(fp, "%d\t%d\t%.2f\t%.2f\t%.2f\t%.2f\t\n", kpt->octave, kpt->layer, kpt->r, kpt->c, kpt->scale, kpt->ori);
    }
  }

  fclose(fp);
}


void export_keypoints_to_key_file(int FilePathLength, int nKeypointsMax,
				  int DumpDescriptor, int nBins,
				  IN char * filename,
				  IN int * nbKeypoints, 
				  IN SiftKpt * keypoints) {
#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif

  size_t filenameLength = strlen(filename);
  char newFileName[FilePathLength];
  if (filename[filenameLength-1] == 'm' &&
      filename[filenameLength-3] == 'p' &&
      filename[filenameLength-4] == '.') {
    memcpy(newFileName, filename, filenameLength - 4);
    newFileName[filenameLength-4] = '\0';
  } else {
    memcpy(newFileName, filename, filenameLength + 1);    
  }
  strcat(newFileName, "_keypoints.key");

  export_kpt_list_to_file(newFileName, *nbKeypoints, keypoints, DumpDescriptor, nBins);
}


void read_pgm(int FilePathLength, IN char * filename,
	     int image_width, int image_height, OUT unsigned char * img) {
  FILE* in_file;
  char ch, type;
  int dummy;
  int w, h;

#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  
  in_file = fopen(filename, "rb");
  if (! in_file) {
    fprintf(stderr, "ERROR(0): Fail to open file %s\n", filename);
    //return -1;
  }
  /* Determine pgm image type (only type three can be used)*/
  ch = getc(in_file);
  if (ch != 'P') {
    printf("ERROR(1): Not valid pgm/ppm file type\n");
    //return -1;
  }
  ch = getc(in_file);
  /* Convert the one digit integer currently represented as a character to
   * an integer(48 == '0') */
  type = ch - 48;
  if (type != 5) {
    printf("ERROR(2): this file type (P%d) is not supported!\n", type);
    //return -1;
  }
  while (getc(in_file) != '\n');      /* Skip to end of line*/
  while (getc(in_file) == '#') {       /* Skip comment lines */
    while (getc(in_file) != '\n');
  }
  fseek(in_file, -1, SEEK_CUR);     /* Backup one character*/

  fscanf(in_file,"%d", &w);
  fscanf(in_file,"%d", &h);

  if (image_width != w || image_height != h) {
    fprintf(stderr, "Image size parameters are not corresponding the image loaded, leaving now.\n");
    fprintf(stderr, "Found: %dx%d, expected: %dx%d", w, h, image_width, image_height);
    exit(1);
  }
	
  fscanf(in_file,"%d", &dummy);  /* Skipped here */
  while(getc(in_file) != '\n');
  fread(img, sizeof(unsigned char), w*h, in_file);
  fclose(in_file);
  //return 0;
}


int write_pgm(int w, int h, unsigned char * img,  const char * filename) {
  FILE* out_file;

  if (w <= 0 || h <= 0) {
    fprintf(stderr, "write_pgm(%s):Invalid image width or height\n", filename);
    return -1;
  }

  out_file = fopen(filename, "wb");
  if (! out_file) {
    fprintf(stderr, "Fail to open file: %s\n", filename);
    return -1;
  }

  fprintf(out_file, "P5\n");
  fprintf(out_file, "%d %d\n255\n", w, h);

  fwrite(img, sizeof(unsigned char), w * h, out_file);

  fclose(out_file);
  return 0;
}

void write_float_pgm(char* filename, float* data, unsigned char * buffer, int w, int h, int mode) {
  int i, j;
  unsigned char * dst = buffer;
  if (mode == 1) { // clop
    for (i = 0; i < h; i++) {
      size_t iw = i * w;
      for (j = 0; j < w; j++) {
	if (data[iw+j] >= 255.0) {
	  *(dst++) = 255;
	} else if (data[iw+j] <= 0.0) {
	  *(dst++) = 0;
	} else{
	  *(dst++) = (int) data[iw+j];
	}
      }
    }
  } else if(mode == 2) { // abs, x10, clop
    for (i = 0; i < h; i++) {
      size_t iw = i * w;
      for (j = 0; j < w; j++) {
	int tmpInt = (int)(fabs(data[iw+j])*10.0);
	if (fabs(data[iw+j]) >= 255) {
	  *(dst++) = 255;
	} else if (tmpInt <= 0){
	  *(dst++) = 0;
	} else {
	  *(dst++) = tmpInt;
	}
      }
    }
  } else {
    return;
  }
  write_pgm(w, h, buffer, filename);
}

//http://en.wikipedia.org/wiki/Midpoint_circle_algorithm
void rasterCircle(unsigned char* img_r, unsigned char* img_g, unsigned char* img_b, int w, int h, int r, int c, int radius) {
  int f = 1 - radius;
  int ddF_x = 1;
  int ddF_y = -2 * radius;
  int x = 0;
  int y = radius;

  int x0 = r;
  int y0 = c;
	
  setPixelRed(img_r, img_g, img_b, w, h, x0, y0 + radius);
  setPixelRed(img_r, img_g, img_b, w, h, x0, y0 - radius);
  setPixelRed(img_r, img_g, img_b, w, h, x0 + radius, y0);
  setPixelRed(img_r, img_g, img_b, w, h, x0 - radius, y0);

  while(x < y) {
    // ddF_x == 2 * x + 1;
    // ddF_y == -2 * y;
    // f == x*x + y*y - radius*radius + 2*x - y + 1;
    if(f >= 0) {
      y--;
      ddF_y += 2;
      f += ddF_y;
    }
    x++;
    ddF_x += 2;
    f += ddF_x;    
    setPixelRed(img_r, img_g, img_b, w, h, x0 + x, y0 + y);
    setPixelRed(img_r, img_g, img_b, w, h, x0 - x, y0 + y);
    setPixelRed(img_r, img_g, img_b, w, h, x0 + x, y0 - y);
    setPixelRed(img_r, img_g, img_b, w, h, x0 - x, y0 - y);
    setPixelRed(img_r, img_g, img_b, w, h, x0 + y, y0 + x);
    setPixelRed(img_r, img_g, img_b, w, h, x0 - y, y0 + x);
    setPixelRed(img_r, img_g, img_b, w, h, x0 + y, y0 - x);
    setPixelRed(img_r, img_g, img_b, w, h, x0 - y, y0 - x);
  }
}


void draw_red_orientation(unsigned char* img_r, unsigned char* img_g, unsigned char* img_b, int w, int h, int x, int y, float ori, int cR) {
  int xe = (int) (x + cos(ori)*cR);
  int ye = (int) (y + sin(ori)*cR);
  // Bresenham's line algorithm
  int dx =  abs(xe-x), sx = x<xe ? 1 : -1;
  int dy = -abs(ye-y), sy = y<ye ? 1 : -1;
  int err = dx+dy, e2; /* error value e_xy */

  for(;;) {  /* loop */
    //setPixelRed(imgPPM, x, y);
    setPixelRed(img_r, img_g, img_b, w, h, y, x);
    if (x==xe && y==ye) break;
    e2 = 2*err;
    if (e2 >= dy) {
      err += dy;
      x += sx;
    } /* e_xy+e_x > 0 */
    if (e2 <= dx) {
      err += dx;
      y += sy;
    } /* e_xy+e_y < 0 */
  }
}



// Combine two images horizontally
int combine_image(unsigned char * out_image,
		  int w1, int h1,
		  unsigned char * image1,
		  int w2, int h2,
		  unsigned char * image2){
  int dstH = tmax_i(h1, h2);

  // add this actor for match

  for (int r = 0; r < dstH; r ++){
    if (r < h1){
      memcpy(out_image, image1, w1 * sizeof(unsigned char));
    }else{
      memset(out_image, 0, w1 * sizeof(unsigned char));
    }
    out_image += w1;

    if (r < h2){
      memcpy(out_image, image2, w2 * sizeof(unsigned char));
    }
    else{
      memset(out_image, 0, w2 * sizeof(unsigned char));
    }
    out_image += w2;
    image1 += w1;
    image2 += w2;
  }

  return 0;
}


void draw_keypoints_to_ppm_file(int FilePathLength, int nKeypointsMax,
				int image_width, int image_height,
				int tot_image_size,
				IN int * nbKeypoints,
				IN char * filename,
				IN unsigned char * image, 
				IN struct SiftKeypoint * keypoints) {
  
#ifdef SIFT_DEBUG
  fprintf(stderr, "Enter function: %s\n", __FUNCTION__);
#endif
  
  int r, c;

  /*******************************
   * cR:
   * radius of the circle
   * cR = sigma * 4 * (2^O)
   *******************************/
  int cR;

  // initialize the imgPPM
  unsigned char img_r[tot_image_size];
  unsigned char img_g[tot_image_size];
  unsigned char img_b[tot_image_size];

  // Copy gray PGM images to color PPM images
  memcpy(img_r, image, sizeof(unsigned char) * tot_image_size);
  memcpy(img_g, image, sizeof(unsigned char) * tot_image_size);
  memcpy(img_b, image, sizeof(unsigned char) * tot_image_size);
	
  for (int index = 0; index < *nbKeypoints; ++index) {
    struct SiftKeypoint * kpt = keypoints + index;
    // derive circle radius cR
    cR = (int) kpt->scale;
    if(cR <= 1){ // avoid zero radius
      cR = 1;
    }
    r = (int) kpt->r;
    c = (int) kpt->c;
    //	draw_red_circle(&imgPPM, r, c, cR);
    rasterCircle(img_r, img_g, img_b, image_width, image_height, r, c, cR);
    rasterCircle(img_r, img_g, img_b, image_width, image_height, r, c, cR + 1);
    float ori = kpt->ori;
    draw_red_orientation(img_r, img_g, img_b, image_width, image_height, c, r, ori, cR);
  }

  // write rendered image to output
  size_t filenameLength = strlen(filename);
  char newFileName[FilePathLength];
  if (filename[filenameLength-1] == 'm' &&
      filename[filenameLength-3] == 'p' &&
      filename[filenameLength-4] == '.') {
    memcpy(newFileName, filename, filenameLength - 4);
    newFileName[filenameLength-4] = '\0';
  } else {
    memcpy(newFileName, filename, filenameLength + 1);    
  }
  strcat(newFileName, "_wkpts.ppm");

  write_rgb2ppm(newFileName, img_r, img_g, img_b, image_width, image_height, tot_image_size);

}


// Draw a line on the RGB color image.
int draw_line_to_rgb_image(unsigned char* data,
			   int w, int h, struct MatchPair * mp, int offset) {
  int r1 = mp->r1;
  int c1 = mp->c1;
  int r2 = mp->r2;
  int c2 = mp->c2+offset;

  float k = (float)(r2 - r1) / (float)(c2 - c1);
  size_t indexTmp = 3 * c1;
  size_t w3 = 3 * w;
  for (int c = c1; c < c2; c ++){
    // Line equation
    int r = (int)(k * (c - c1) + r1);
    size_t index = r * w3 + indexTmp;
    
    // Draw a blue line
    data[index] = 0;
    data[index + 1] = 0;
    data[index + 2] = 255;
    indexTmp += 3;
  }

  return 0;
}


// Draw match lines between matched keypoints between two images.
int draw_match_lines_to_ppm_file(const char * filename,
				 unsigned char * image1,
				 int w1, int h1,
				 unsigned char * image2,
				 int w2, int h2,
				 int nb_match,
				 struct MatchPair * match_list){

  //tmpImage has to be initialized by preesm

  int w = tmax_i(h1, h2);
  int h = w1+w2;
  size_t tot_size = w*h;
  unsigned char tmpImage[tot_size];
  unsigned char dstData[3*tot_size];
  
  combine_image(tmpImage, w1, h1, image1, w2, h2, image2);

  unsigned char * srcData = tmpImage;

  for (size_t i = 0; i < tot_size; i ++){
    dstData[i * 3] = srcData[i];
    dstData[i * 3 + 1] = srcData[i];
    dstData[i * 3 + 2] = srcData[i];
  }

  for (int index = 0; index < nb_match; ++index){
    struct MatchPair * mp = &(match_list[index]);
    draw_line_to_rgb_image(dstData, w, h, mp, w1);
  }

  write_data2ppm(filename, dstData, w, h);

  return 0;
}



void draw_red_circle(unsigned char* img_r, unsigned char* img_g, unsigned char* img_b, int w, int h, int r, int c, int cR) {
  int cx = -cR, cy = 0, err = 2-2*cR; /* II. Quadrant */
  do {
    setPixelRed(img_r, img_g, img_b, w, h, r-cx, c+cy); /*	  I. Quadrant */
    setPixelRed(img_r, img_g, img_b, w, h, r-cy, c-cx); /*	 II. Quadrant */
    setPixelRed(img_r, img_g, img_b, w, h, r+cx, c-cy); /* III. Quadrant */
    setPixelRed(img_r, img_g, img_b, w, h, r+cy, c+cx); /*	 IV. Quadrant */
    cR = err;
    if (cR >  cx)
      err += ++cx*2+1; /* e_xy+e_x > 0 */
    if (cR <= cy)
      err += ++cy*2+1; /* e_xy+e_y < 0 */
  } while (cx < 0);
}


void draw_circle(unsigned char* img_r, unsigned char* img_g, unsigned char* img_b, int w, int h, int r, int c, int cR, float thickness) {
  int x,y;
  int cR2 = cR*cR;
  float f = thickness/2;
  for (x=-cR; x<=+cR; x++) {
    int x2 = x*x;
    int xpc = x + c;
    for (y=-cR; y<=+cR; y++) {
      int y2 = y*y;
      if ( (x2 + y2) > (cR2 - f) && (x2 + y2) < (cR2 + f) ) { 
	setPixelRed(img_r, img_g, img_b, w, h, y + r, xpc);
      }
    }
  }
}

