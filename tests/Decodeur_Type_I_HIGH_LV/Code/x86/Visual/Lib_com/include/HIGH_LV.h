#ifndef HIGH_LV_H_
#define HIGH_LV_H_

#include "mpeg.h"

#include <stdio.h>
#include <stdlib.h>
#define uchar unsigned char
#define ushort unsigned short
#define ulong unsigned long
#define uint unsigned int
#define prec_synchro int
#define stream unsigned char
#define image_type unsigned char
#define dpb_type unsigned char


void init_vlc_tables_I ( REVERSE_EVENT *RESTRICT DCT3D_I );
void readm4v_init(int argc,char *argv[]);
void Init_SDL(int edge,int frame_width,int frame_height);
void SDL_Display(int edge,int frame_width,int frame_height,unsigned char *Y,unsigned char *V,unsigned char *U);
void CloseSDLDisplay();
void readm4v(const int nb_octets_to_read,const int new_buff_i,uchar *buffer);
void readm4v_double_buffering(const int nb_octets_to_read,const int pos_octet,const int new_buff_i,const unsigned char *buffer_new,const unsigned char *buffer_state,unsigned char *buffer_out,int *position_o );
void VideoObjectLayer(const int pos_i,int length_bit,const uchar *data,struct_VOLsimple *VOLsimple,uchar *vop_complexity,int *pos_o,int *vol_width,int *vol_height);
int VideoObjectPlaneI(int position_vol,const unsigned char *data,const struct_VOLsimple *VOLsimple,const unsigned char *vop_complexity,int *pos,struct_VOP *VOP,int  *vop_coding_type );
static int firstframe = 0;
void decode_I_frame ( const unsigned char *RESTRICT data, const struct_VOLsimple *RESTRICT VOLsimple, int pos_i
    , struct_VOP *RESTRICT VOP, const REVERSE_EVENT *RESTRICT DCT3D_I, int *RESTRICT pos_o, int *RESTRICT address, unsigned char *RESTRICT Lum, unsigned char *RESTRICT Cb, unsigned char *RESTRICT Cr, int *RESTRICT keyframes,unsigned char *RESTRICT Lum_o, unsigned char *RESTRICT Cb_o, unsigned char *RESTRICT Cr_o, int *RESTRICT keyframes_o, int vop_coding_type );
void New_buffer(const int nb_octets_to_read,const int pos,int *new_buff_o,int *pos_octet);
void extract_picture(int xsize,int ysize,int edge,int Crop,uchar *img_luma_in,uchar *img_Cb_in,uchar *img_Cr_in,int address_pic,uchar *img_luma_out,uchar *img_Cb_out,uchar *img_Cr_out);


#endif
