/*******************************************************************************
 * Copyright or © or Copr. %%LOWERDATE%% - %%UPPERDATE%% IETR/INSA:
 *
 * %%AUTHORS%%
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
    #include "x86_setup.h"
	#include "MED_LV.h"

    // Buffer declarations

    uchar frame_U_o_in[342912];
    uchar frame_V_o_in[342912];
    uchar frame_Y_o_in[1371648];
	uchar out_end_in_0[342912];
    uchar out_end_in_1[342912];
    uchar out_end_in_2[1371648];
    uchar *init_out_frame_U_i;
    uchar *init_out_frame_V_i;
	uchar *init_out_frame_Y_i;
	uchar out_end_in[829440];
    uchar buffer_out_in[829440];
    uchar *init_out_buffer_st_0;
    int *init_out_pos_octet;
    int pos_octet_end_in[1];
	int *init_out_new_buff__0;
    int *init_out_new_buff_i;
    int *init_out_keyframes_0;
	int out_end_in_3[1];
    int out_end_in_4[1];
    int keyframes_o_end_in[2];
    uchar RefY_o_RefY_i[1371648];
	uchar *out_RefY_i;
	uchar RefU_o_RefU_i[342912];
	uchar *out_RefU_i;
	uchar RefV_o_RefV_i[342912];
	uchar *out_RefV_i;
    uchar *out_data;
    uchar *out_data_0;
    int pos_o_pos_o[1];
    int pos_o_pos_i[1];
    struct_VOLsimple *out_VOLsimple_1;
    int pos_o_pos_vol[1];
    int xsize_o_xsize_i[1];
    uchar vop_complexity_vop_0[5];
    int MB_pos_i_MB_pos_i[99];
	short InverseQuant_BlkXn_0[16];
    short InverseQuant_BlkXn_1[16];
    short InverseQuant_BlkXn_2[16];
	short InverseQuant_BlkXn_3[16];
	short InverseQuant_BlkXn_4[16];
    short InverseQuant_BlkXn_5[16];
	short Lum_BuffA_Lum_BuffA[16];
	short Lum_BuffB_Lum_BuffB[16];
	short Lum_BuffC_Lum_BuffC[16];
	short Lum_BuffD_Lum_BuffD[16];
	short Lum_BuffE_Lum_BuffE[16];
	short Cb_BuffA_Cb_BuffA[16];
	short Cb_BuffB_Cb_BuffB[16];
	short Cb_BuffC_Cb_BuffC[16];
	short Cr_BuffA_Cr_BuffA[16];
	short Cr_BuffB_Cr_BuffB[16];
	short Cr_BuffC_Cr_BuffC[16];
    int new_buff_o_in[1];
    int tab_pos_X_tab_pos_X[4];
    uchar *out_data_1;
    struct_VOLsimple VOLsimple_in[1];   
    struct_VOLsimple *out_VOLsimple;
    unsigned char  * display_display[6];
    int adresse_adresse_pic[1];
    int pos_fin_vlc_pos[1];
    int ysize_o_ysize_i[1];
    REVERSE_EVENT DCT3D_I_DCT3D_I[4096];   
    int pos_pos_i[1];
    struct_VOLsimple *out_VOLsimple_0;
    struct_VOP VOP_VOP[1];   
    uchar new_buffer_buffer__0[414720];  
    int pos_pos[1];
    struct_VOP newVOP_VOP_0[1];
    int resync_marker_[1];
    int VideoPacketHeader__0[1];
	short InverseQuant_BlkXn_0_out[16];
    struct_VOP newVOP_VOP[1];
	image_type Display_Extract_Image_Y_o[685832];

	int vop_coding_type[1];
	
	extern int _argc;
	extern char** _argv;
 

    DWORD WINAPI computationThread_Core1( LPVOID lpParam );

    DWORD WINAPI computationThread_Core1( LPVOID lpParam ){
        // Buffer declarations
        long i ;

			int k = 0;
			int j = 0;
			int z = 0;

        {
			pos_octet_end_in[0] = 829440;
			out_end_in_3[0] = 1;
			out_end_in_4[0] = 1;

            init_MB_pos(MB_pos_i_MB_pos_i);
            init_vlc_tables_I(DCT3D_I_DCT3D_I);
            readm4v_init(_argc,_argv);
            Init_SDL(16/*edge*/, 720/*xpic*/, 576/*ypic*/);
        }
        for(j=0;j<50;j++){
			init_out_frame_U_i   = &out_end_in_0[0];
			init_out_frame_V_i   = &out_end_in_1[0];
			init_out_frame_Y_i   = &out_end_in_2[0];
			init_out_buffer_st_0 = &out_end_in[0];
			init_out_new_buff__0 = &out_end_in_4[0];
			init_out_new_buff_i  = &out_end_in_3[0];
			init_out_keyframes_0 = &keyframes_o_end_in[0];
			init_out_pos_octet   = &pos_octet_end_in[0];

            readm4v_preesm(414720/*nb_octet_to_read*/, init_out_new_buff_i, new_buffer_buffer__0);
            readm4v_double_buffering_preesm(414720/*nb_octet_to_read*/, init_out_pos_octet, init_out_new_buff__0, new_buffer_buffer__0, init_out_buffer_st_0, buffer_out_in, pos_o_pos_i);
            {//buffer_out
                out_data_0 = &buffer_out_in[0];
                out_data = &buffer_out_in[0];
                out_data_1 = &buffer_out_in[0];
				memcpy(out_end_in,buffer_out_in,829440*sizeof(unsigned char));
            }
            VideoObjectLayer_preesm(pos_o_pos_i, 6635520/*length*/, out_data_0, VOLsimple_in, vop_complexity_vop_0, pos_o_pos_vol, xsize_o_xsize_i, ysize_o_ysize_i);
            {//VOPsimple
                out_VOLsimple_1 = &VOLsimple_in[0];
                out_VOLsimple = &VOLsimple_in[0];
                out_VOLsimple_0 = &VOLsimple_in[0];
            }
            VideoObjectPlaneI_preesm(pos_o_pos_vol, out_data, out_VOLsimple_1, vop_complexity_vop_0, pos_pos_i, VOP_VOP,vop_coding_type);
			init_decode_I_frame_preesm(out_VOLsimple, pos_pos_i, init_out_frame_Y_i, init_out_frame_U_i, init_out_frame_V_i, init_out_keyframes_0, InverseQuant_BlkXn_0, InverseQuant_BlkXn_1, InverseQuant_BlkXn_2, InverseQuant_BlkXn_3, InverseQuant_BlkXn_4, InverseQuant_BlkXn_5, adresse_adresse_pic, display_display, keyframes_o_end_in, pos_o_pos_o, tab_pos_X_tab_pos_X);
            {//decode_I
                for(i = 0; i<99 ; i ++)
                {//bidon_decode_I
					//int a, b;
                    uchar *outSub_i_out_data_1 = &out_data_1 [((0*(829440))%1)];
                    int *outSub_i_MB_pos_i__0 = &MB_pos_i_MB_pos_i [((i*(1))%99)];
                    struct_VOLsimple *outSub_i_out_VOLsi_0 = &out_VOLsimple_0 [((0*(1))%1)];
                    int *_resync_marker = &resync_marker_ [((0*1)%1)];
                    int *_resync_marker_0 = &resync_marker_ [((0*1)%1)];
                    int *_resync_marker_1 = &resync_marker_ [((0*1)%1)];
					VideoPacketHeaderI_preesm(outSub_i_out_data_1, pos_o_pos_o, outSub_i_out_VOLsi_0, VOP_VOP, outSub_i_MB_pos_i__0, newVOP_VOP, VideoPacketHeader__0, resync_marker_);
					Param_MB_I_preesm(VideoPacketHeader__0, outSub_i_out_data_1, newVOP_VOP, newVOP_VOP_0, pos_pos);
                    StockBlocksLum_preesm(outSub_i_MB_pos_i__0, _resync_marker, InverseQuant_BlkXn_1, InverseQuant_BlkXn_2, InverseQuant_BlkXn_3, outSub_i_out_VOLsi_0, Lum_BuffA_Lum_BuffA, Lum_BuffB_Lum_BuffB, Lum_BuffC_Lum_BuffC, Lum_BuffD_Lum_BuffD, Lum_BuffE_Lum_BuffE);
					StockBlocksCb_preesm(outSub_i_MB_pos_i__0, InverseQuant_BlkXn_4, outSub_i_out_VOLsi_0, _resync_marker_0, Cb_BuffA_Cb_BuffA, Cb_BuffB_Cb_BuffB, Cb_BuffC_Cb_BuffC);
					StockBlocksCr_preesm(outSub_i_MB_pos_i__0, InverseQuant_BlkXn_5, outSub_i_out_VOLsi_0, _resync_marker_1, Cr_BuffA_Cr_BuffA, Cr_BuffB_Cr_BuffB, Cr_BuffC_Cr_BuffC);

					decode_I_une_partie_preesm(outSub_i_MB_pos_i__0, outSub_i_out_VOLsi_0, newVOP_VOP_0, outSub_i_out_data_1, display_display, pos_pos, tab_pos_X_tab_pos_X, DCT3D_I_DCT3D_I, InverseQuant_BlkXn_0, InverseQuant_BlkXn_1, InverseQuant_BlkXn_2, InverseQuant_BlkXn_3, InverseQuant_BlkXn_4, InverseQuant_BlkXn_5, Lum_BuffA_Lum_BuffA, Lum_BuffB_Lum_BuffB, Lum_BuffC_Lum_BuffC, Lum_BuffD_Lum_BuffD, Lum_BuffE_Lum_BuffE, Cb_BuffA_Cb_BuffA, Cb_BuffB_Cb_BuffB, Cb_BuffC_Cb_BuffC, Cr_BuffA_Cr_BuffA, Cr_BuffB_Cr_BuffB, Cr_BuffC_Cr_BuffC, frame_U_o_in, frame_V_o_in, frame_Y_o_in, pos_o_pos_o,InverseQuant_BlkXn_0_out);
				}
				memcpy(frame_Y_o_in,init_out_frame_Y_i,1371648*sizeof(unsigned char));
				memcpy(frame_U_o_in,init_out_frame_U_i,342912*sizeof(unsigned char));
				memcpy(frame_V_o_in,init_out_frame_V_i,342912*sizeof(unsigned char));
			}
			pos_fin_vlc_pos[0] = pos_o_pos_o[0];
            New_buffer_preesm(414720/*nb_octet_to_read*/, pos_fin_vlc_pos, new_buff_o_in, pos_octet_end_in);
            {//new_buff_o
				memcpy(out_end_in_4,new_buff_o_in,sizeof(int));
				memcpy(out_end_in_3,new_buff_o_in,sizeof(int));

            }
            {//frame_U
                out_RefU_i = &frame_U_o_in[0];
				memcpy(out_end_in_0,frame_U_o_in,342912*sizeof(unsigned char));
            }
            {//frame_V
                out_RefV_i = &frame_V_o_in[0];
				memcpy(out_end_in_1,frame_V_o_in,342912*sizeof(unsigned char));
            }
            {//frame_Y
                out_RefY_i = &frame_Y_o_in[0];
				memcpy(out_end_in_2,frame_Y_o_in,1371648*sizeof(unsigned char));
            }
			if(k == 0) {k=1;}
			else{
			 extract_picture_preesm(xsize_o_xsize_i, ysize_o_ysize_i, 16/*edge*/, 0/*crop*/, out_RefY_i, out_RefU_i, out_RefV_i, adresse_adresse_pic, Display_Extract_Image_Y_o); 
			 SDL_Display_preesm(16, Display_Extract_Image_Y_o); 
			}
		}
        {
           CloseSDLDisplay();
        }
        return 0;
    }//computationThread

