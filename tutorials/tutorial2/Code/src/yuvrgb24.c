/************************************************************************
   *
   *  yuvrgb24.c, colour space conversion for tmndecode (H.263 decoder)
   *  Copyright (C) 1995, 1996  Telenor R&D, Norway
   *
   *  Contacts:
   *  Robert Danielsen                  <Robert.Danielsen@nta.no>
   *
   *  Telenor Research and Development  http://www.nta.no/brukere/DVC/
   *  P.O.Box 83                        tel.:   +47 63 84 84 00
   *  N-2007 Kjeller, Norway            fax.:   +47 63 81 00 76
   *
   *  Copyright (C) 1997  University of BC, Canada
   *  Modified by: Michael Gallant <mikeg@ee.ubc.ca>
   *               Guy Cote <guyc@ee.ubc.ca>
   *               Berna Erol <bernae@ee.ubc.ca>
   *
   *  Contacts:
   *  Michael Gallant                   <mikeg@ee.ubc.ca>
   *
   *  UBC Image Processing Laboratory   http://www.ee.ubc.ca/image
   *  2356 Main Mall                    tel.: +1 604 822 4051
   *  Vancouver BC Canada V6T1Z4        fax.: +1 604 822 5949
   *
   ************************************************************************/
/* Disclaimer of Warranty
   * 
   * These software programs are available to the user without any license fee
   * or royalty on an "as is" basis. The University of British Columbia
   * disclaims any and all warranties, whether express, implied, or
   * statuary, including any implied warranties or merchantability or of
   * fitness for a particular purpose.  In no event shall the
   * copyright-holder be liable for any incidental, punitive, or
   * consequential damages of any kind whatsoever arising from the use of
   * these programs.
   * 
   * This disclaimer of warranty extends to the user of these programs and
   * user's customers, employees, agents, transferees, successors, and
   * assigns.
   * 
   * The University of British Columbia does not represent or warrant that the
   * programs furnished hereunder are free of infringement of any
   * third-party patents.
   * 
   * Commercial implementations of H.263, including shareware, are subject to
   * royalty fees to patent holders.  Many of these patents are general
   * enough such that they are unavoidable regardless of implementation
   * design.
   * 
   */
extern unsigned char *clp ;

/* Data for ConvertYUVtoRGB */
long int             crv_tab [256];
long int             cbu_tab [256];
long int             cgu_tab [256];
long int             cgv_tab [256];
long int             tab_76309 [256];

void init_dither_tab ()
{
   long int crv, cbu, cgu, cgv ;
   int      i ;
   
   crv = 104597 ;
   cbu = 132201 ; /* fra matrise i global.h */ 
   cgu = 25675 ;
   cgv = 53279 ;
   for ( i = 0 ; i < 256 ; i++ ) 
   {
      crv_tab [i] = (i - 128) * crv ;
      cbu_tab [i] = (i - 128) * cbu ;
      cgu_tab [i] = (i - 128) * cgu ;
      cgv_tab [i] = (i - 128) * cgv ;
      tab_76309 [i] = 76309 * (i - 16);
   }
}

/**********************************************************************
   *
   *	Name:	         ConvertYUVtoRGB
   *	Description:     Converts YUV image to RGB (packed mode)
   *
   *	Input:	         pointer to source luma, Cr, Cb, destination,
   *                       image width and height
   *	Returns:
   *	Side effects:
   *
   *	Date: 951208	Author: Karl.Lillevold@nta.no
   *
   ***********************************************************************/
void ConvertYUVtoRGB ( src0, src1, src2, dst_ori, width, height )
unsigned char  *src0, *src1, *src2 ;
unsigned char  *dst_ori ;
int            width, height ;
{
   extern long int   crv_tab [];
   extern long int   cbu_tab [];
   extern long int   cgu_tab [];
   extern long int   cgv_tab [];
   extern long int   tab_76309 [];
   int               y11, y21 ;
   int               y12, y22 ;
   int               y13, y23 ;
   int               y14, y24 ;
   int               u, v ;
   int               i, j ;
   int               c11, c21, c31, c41 ;
   int               c12, c22, c32, c42 ;
   unsigned int      DW ;
   unsigned int      *id1, *id2 ;
   unsigned char     *py1, *py2, *pu, *pv ;
   unsigned char     *d1, *d2 ;
   
   d1 = dst_ori ;
   d1 += width * height * 3 - width * 3 ;
   d2 = d1 - width * 3 ;
   py1 = src0 ;
   pu = src1 ;
   pv = src2 ;
   py2 = py1 + width ;
   id1 = (unsigned int *)d1 ;
   id2 = (unsigned int *)d2 ;
   for ( j = 0 ; j < height ; j += 2 ) 
   {
      
      /* line j + 0 */
      for ( i = 0 ; i < width ; i += 4 ) 
      {
         u = *pu++ ;
         v = *pv++ ;
         c11 = crv_tab [v];
         c21 = cgu_tab [u];
         c31 = cgv_tab [v];
         c41 = cbu_tab [u];
         u = *pu++ ;
         v = *pv++ ;
         c12 = crv_tab [v];
         c22 = cgu_tab [u];
         c32 = cgv_tab [v];
         c42 = cbu_tab [u];
         y11 = tab_76309 [*py1++]; /* (255/219)*65536 */ 
         y12 = tab_76309 [*py1++];
         y13 = tab_76309 [*py1++]; /* (255/219)*65536 */ 
         y14 = tab_76309 [*py1++];
         y21 = tab_76309 [*py2++];
         y22 = tab_76309 [*py2++];
         y23 = tab_76309 [*py2++];
         y24 = tab_76309 [*py2++];
         
         /* RGBR */
         DW = clp [(y11 + c41) >> 16] | clp [(y11 - c21 - c31) >> 16] << 8
            | clp [(y11 + c11) >> 16] << 16 | clp [(y12 + c41) >> 16] << 24 ;
         *id1++ = DW ;
         
         /* GBRG */
         DW = clp [(y12 - c21 - c31) >> 16] | clp [(y12 + c11) >> 16] << 8
            | clp [(y13 + c42) >> 16] << 16 | clp [(y13 - c22 - c32) >> 16] << 24 ;
         *id1++ = DW ;
         
         /* BRGB */
         DW = clp [(y13 + c12) >> 16] | clp [(y14 + c42) >> 16] << 8
            | clp [(y14 - c22 - c32) >> 16] << 16 | clp [(y14 + c12) >> 16] << 24 ;
         *id1++ = DW ;
         
         /* RGBR */
         DW = clp [(y21 + c41) >> 16] | clp [(y21 - c21 - c31) >> 16] << 8
            | clp [(y21 + c11) >> 16] << 16 | clp [(y22 + c41) >> 16] << 24 ;
         *id2++ = DW ;
         
         /* GBRG */
         DW = clp [(y22 - c21 - c31) >> 16] | clp [(y22 + c11) >> 16] << 8
            | clp [(y23 + c42) >> 16] << 16 | clp [(y23 - c22 - c32) >> 16] << 24 ;
         *id2++ = DW ;
         
         /* BRGB */
         DW = clp [(y23 + c12) >> 16] | clp [(y24 + c42) >> 16] << 8
            | clp [(y24 - c22 - c32) >> 16] << 16 | clp [(y24 + c12) >> 16] << 24 ;
         *id2++ = DW ;
      }
      id1 -= 9 * width >> 2 ;
      id2 -= 9 * width >> 2 ;
      py1 += width ;
      py2 += width ;
   }
}
