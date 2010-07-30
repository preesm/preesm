/*****************************************************************************
   *
   *  MPEG4DSP developped in IETR image lab
   *
   *
   *
   *              Jean-Francois NEZAN <jnezan@insa-rennes.Fr>
   *              Mickael Raulet <mraulet@insa-rennes.Fr>
   *              http://www.ietr.org/gro/IMA/th3/temp/index.htm
   *
   *  Based on the XviD MPEG-4 video codec
   *
   *
   *
   *  This program is free software; you can redistribute it and/or modify
   *  it under the terms of the GNU General Public License as published by
   *  the Free Software Foundation; either version 2 of the License, or
   *  (at your option) any later version.
   *
   *  This program is distributed in the hope that it will be useful,
   *  but WITHOUT ANY WARRANTY; without even the implied warranty of
   *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   *  GNU General Public License for more details.
   *
   *  You should have received a copy of the GNU General Public License
   *  along with this program; if not, write to the Free Software
   *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
   *
   * $Id$
   *
   **************************************************************************/
#include <stdio.h>
#include <string.h>

#ifdef TI_OPTIM
#define RESTRICT restrict
#else 
#define RESTRICT 
#endif

#define EDGE_SIZE 16
#define XSIZE 720
#define YSIZE 576
#define MODE_INTER 0
#define MODE_INTER_Q 1
#define MODE_INTER4V 2
#define MODE_INTRA 3
#define MODE_INTRA_Q 4
#define MODE_STUFFING 7
#define MODE_NOT_CODED 16
#define ABS(X) (((X)>0)?(X):-(X))
#define MIN(X, Y) ((X)<(Y)?(X):(Y))
#define MAX(X, Y) ((X)>(Y)?(X):(Y))
#define SIGN(X) (((X)>0)?1:-1)

/* --- bframe specific --- */
#define MODE_DIRECT			0
#define MODE_INTERPOLATE	1
#define MODE_BACKWARD		2
#define MODE_FORWARD		3
#define MODE_DIRECT_NONE_MV	4
#define MODE_DIRECT_NO4V	5



typedef struct {
    volatile int          vop_time_increment_resolution ;
    volatile int  complexity_estimation_disable ;
    volatile int  quant_type ;
    volatile int  quant_precision ;
    volatile int          bits_per_pixel ;
    volatile int  data_partitioned ;
    volatile int  reversible_vlc ;
    volatile int  resync_marker_disable ;
    volatile int          video_object_layer_width ;
    volatile int          video_object_layer_height ;
    //TO BE SUPPORTED
    volatile int          quarterpel ;
    volatile int          sprite_enable ;
    volatile int          newpred_enable ;
    volatile int          reduced_resolution_enable ;
    volatile int          scalability ;
	volatile int          time_inc_bits ;
}   struct_VOLsimple ;

typedef struct {
    int x ;
    int y ;
}   VECTOR ;

typedef struct {
    VECTOR  mvs [4];
}   vector ;

typedef struct {
    volatile int  not_coded ;
    volatile int          rounding ;
    volatile int  vop_coding_type ;
    volatile int  modulo_time_base ;
	volatile int			time_bp;
	volatile int			time_pp;
    volatile int  vop_coded ;
    volatile int  intra_dc_vlc_thr ;
    volatile int  quant ;
    volatile int  prev_quant ;
    volatile int cbp ;
    volatile int  ac_pred_flag ;
    volatile int  mb_type ;
    volatile int fcode_forward ;
	volatile int fcode_backward ;
    volatile int   bound ;
    volatile int   resync_marker ;
}   struct_VOP ;

typedef struct {
    int rien ;
}   struct_read_vop_complexity ;

typedef struct {
    unsigned char last ;
    unsigned char run ;
    unsigned char level ;
}   EVENT ;

typedef struct {
    unsigned char     len ;
    EVENT   event ;
}   REVERSE_EVENT ;

// int div_nearest_integer(const int a,const int b);
int log_base2 (int a) ;
int cal_dc_scaler_lum (int QP) ;
int compare_abs (int val1, int val2) ;

static __inline int showNbits ( const unsigned char *const RESTRICT tab, const int position, const int n )
{
#ifdef TI_OPTIM
    
    const int       pos_bit = position & 0x7 ; // position courante dans le caractère courant 
    const int       pos_char = position >> 3 ; // caractère courant dans le tableau  
    unsigned int    res ;
    
    res = _mem4((void *)(tab + pos_char));
    res = _packlh2(res, res);
    res = _swap4(res);
    res = _extu(res, pos_bit, 32 - n);
    return (res);
#else 
    
    const int       pos_bit = position & 0x7 ; /* position courante dans le caractère courant */ 
    const int       pos_char = position >> 3 ; /* caractère courant dans le tableau */ 
    unsigned int    c
        = ((*(tab + pos_char) << 24 | *(tab + pos_char + 1) << 16 | *(tab + pos_char + 2)<<8 | *(tab + pos_char + 3)) & 0xffffffff >> pos_bit)
            >> (32 - pos_bit - n) ;
    
    return (c);
#endif
}
