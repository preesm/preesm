
#ifndef SUBSTRACT_PIC
#define SUBSTRACT_PIC

/**
 Substracts picture 2 from picture 1 and returns the difference.
 Both images have 4:2:0 YUV format with 8 bits luma and chroma.
 
 @param y1 first input luma
 @param u1 first input Cb
 @param v1 first input Cr
 @param y2 second input luma
 @param u2 second input Cb
 @param v2 second input Cr
 @param y output luma
 @param u output Cb
 @param v output Cr
 @param xsize luma picture width. Chroma have 1/2 xsize
 @param ysize luma picture heigth. Chroma have 1/2 ysize
*/
void substract_pic (unsigned char *y1, unsigned char *u1, unsigned char *v1, 
					unsigned char *y2, unsigned char *u2, unsigned char *v2, 
					unsigned char *y, unsigned char *u, unsigned char *v, 
					unsigned char *y1o, unsigned char *u1o, unsigned char *v1o,
					int xsize, int ysize);

#endif
